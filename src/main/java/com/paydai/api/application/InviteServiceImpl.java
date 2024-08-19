package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.domain.service.InviteService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.infrastructure.security.JwtAuthService;
import com.paydai.api.presentation.dto.auth.AuthDtoMapper;
import com.paydai.api.presentation.dto.auth.AuthModelDto;
import com.paydai.api.presentation.dto.auth.AuthRecordDto;
import com.paydai.api.presentation.dto.invite.InviteDto;
import com.paydai.api.presentation.dto.invite.InviteDtoMapper;
import com.paydai.api.presentation.dto.invite.InviteRecord;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
import com.paydai.api.presentation.request.EmailRequest;
import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
  private final AppConfig appConfig;
  private final JwtAuthService jwtService;
  private final InviteRepository repository;
  private final RoleDtoMapper roleDtoMapper;
  private final UserRepository userRepository;
  private final EmailRepository emailRepository;
  private final PasswordEncoder passwordEncoder;
  private final InviteDtoMapper inviteDtoMapper;
  private final EmailSenderService emailSenderService;
  private final AuthDtoMapper authenticationDTOMapper;
  private final WorkspaceDtoMapper workspaceDtoMapper;
  private final CommSettingRepository commSettingRepository;
  private final UserWorkspaceRepository userWorkspaceRepository;

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final int CODE_LENGTH = 8;
  private static final SecureRandom random = new SecureRandom();

  private String generateInviteCode() {
    StringBuilder code = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
    }
    return code.toString();
  }

  @Override
  @TryCatchException
  public JapiResponse createInvite(InviteRequest payload) throws MessagingException {
    InviteModel inviteModel;
    inviteModel = repository.findByInvited(payload.getRoleId(), payload.getWorkspaceId(), payload.getCompanyEmail());

    if (inviteModel == null) {
      InviteModel buildInvite = InviteModel.builder()
        .inviteCode(this.generateInviteCode())
        .companyEmail(payload.getCompanyEmail())
        .workspace(WorkspaceModel.builder().id(payload.getWorkspaceId()).build())
        .commission(payload.getCommission())
        .interval(payload.getInterval())
        .intervalUnit(payload.getIntervalUnit())
        .role(RoleModel.builder().id(payload.getRoleId()).build())
        .build();

      if (payload.getAggregate() != null) buildInvite.setAggregate(payload.getAggregate());

      // Check if invite already exiting if so then update instead of save;
      inviteModel = repository.save(buildInvite);
    }

    String link = appConfig.getPaydaiClientBaseUrl() + "/signup/invite?code=" + inviteModel.getInviteCode() + "&c_email=" + payload.getCompanyEmail();

    InviteDto inviteDto = InviteDto.getInviteDtoData(inviteModel, link);

    InviteRecord inviteRecord = inviteDtoMapper.apply(inviteDto);

    // SEND EMAIL NOTIFICATION
    EmailRequest buildEmail = EmailRequest.builder()
      .toEmail(payload.getCompanyEmail())
      .subject("You have been invited to join a workspace")
      .isHTML(true)
      .message(link)
      .build();
    emailSenderService.sendMail(buildEmail);

    return JapiResponse.success(inviteRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse acceptInvite(RegisterRequest request, String inviteCode) {
    // Check if invite exit
    InviteModel inviteModel = repository.findByInvite(inviteCode);

    if (inviteModel == null) throw new NotFoundException("Invalid invite code");

    String passwordHash = passwordEncoder.encode(request.getPassword());

    // Check if the personal email already exit
    EmailModel emailModel;

    emailModel = emailRepository.findEmailQuery(request.getEmail());

    if (emailModel == null) {
      UserModel userModel = userRepository.save(
        UserModel.builder()
          .firstName(request.getFirstName())
          .lastName(request.getLastName())
          .userType(UserType.SALES_REP)
          .build());

      // Create email personal
      emailModel = emailRepository.save(
        EmailModel.builder()
          .email(request.getEmail())
          .passwordHash(passwordHash)
          .emailType(EmailType.PERSONAL)
          .user(userModel)
          .build()
      );
    }

    // Create email account company
    EmailModel emailAddedWorkspace = emailRepository.save(
      EmailModel.builder()
        .email(inviteModel.getCompanyEmail())
        .emailType(EmailType.COMPANY)
        .passwordHash(passwordHash)
        .user(emailModel.getUser())
        .build()
    );


    // Create commission setting
    CommissionSettingModel commissionSettingModel = commSettingRepository.save(
      CommissionSettingModel.builder()
        .commission(inviteModel.getCommission())
        .interval(inviteModel.getInterval())
        .aggregate(inviteModel.getAggregate())
        .intervalUnit(inviteModel.getIntervalUnit())
        .build()
    );

    // Create workspace to user model
    UserWorkspaceModel userWorkspaceModel = userWorkspaceRepository.save(
      UserWorkspaceModel.builder()
        .user(emailModel.getUser())
        .workspace(inviteModel.getWorkspace())
        .role(inviteModel.getRole())
        .email(emailAddedWorkspace)
        .commission(commissionSettingModel)
        .build()
    );

    // generate token
    String jwt = jwtService.generateToken(emailModel.getUser());

    // delete invite from invite
    repository.removeInvite(inviteCode);

    // send welcome to paydai to email company

    RoleRecord role = userWorkspaceModel != null ? roleDtoMapper.apply(userWorkspaceModel.getRole()) : null;

    WorkspaceRecord workspace = userWorkspaceModel != null ? workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace()) : null;

    AuthModelDto buildAuthDto = AuthModelDto.getAuthData(emailModel.getUser(), emailModel, jwt, role, workspace);

    AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

    return JapiResponse.success(auth);
  }

  @Override
  @TryCatchException
  public JapiResponse getWorkspaceInvites(UUID workspaceId) {
    List<InviteModel> inviteModels = repository.findWorkspaceInvites(workspaceId);

    List<InviteRecord> inviteRecords = inviteModels
      .stream()
      .map(inviteModel -> inviteDtoMapper.apply(
        InviteDto.getInviteDtoData(inviteModel, "")
      ))
      .toList();

    return JapiResponse.success(inviteRecords);
  }
}
