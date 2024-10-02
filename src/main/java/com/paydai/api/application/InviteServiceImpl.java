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
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
  private final AppConfig appConfig;
  private final JwtAuthService jwtService;
  private final InviteRepository repository;
  private final RoleDtoMapper roleDtoMapper;
  private final RoleRepository roleRepository;
  private final TeamRepository teamRepository;
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
  @Transactional
  public JapiResponse createInvite(InviteRequest payload) throws MessagingException {
    InviteModel inviteModel = repository.findByInvited(payload.getRoleId(), payload.getWorkspaceId(), payload.getCompanyEmail());

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

      if (payload.getAggregate() != null) {
        switch (payload.getAggregate()) {
          case STAFF -> buildInvite.setPosition(PositionType.OVER_ALL_MANAGER);
          case CLOSER -> buildInvite.setPosition(PositionType.OVER_ALL_CLOSER);
          case SETTER -> buildInvite.setPosition(PositionType.OVER_ALL_SETTER);
          case CUSTOM -> {
            if (!payload.getSelectedSalesRep().isEmpty()) {
              buildInvite.setSelectedSalesRep(payload.getSelectedSalesRep());
            }
            if (payload.getAggregateCustomRep().equals(AggregateCustomRep.SALES_REPS)) {
              buildInvite.setPosition(PositionType.SALES_REP_MANAGER);
            }
            if (payload.getAggregateCustomRep().equals(AggregateCustomRep.CLOSER_ONLY)) {
              buildInvite.setPosition(PositionType.CLOSER_MANAGER);
            }
            if (payload.getAggregateCustomRep().equals(AggregateCustomRep.SETTER_ONLY)) {
              buildInvite.setPosition(PositionType.SETTER_MANAGER);
            }
          }
          default -> throw new NotFoundException("Unexpected value: " + payload.getAggregate());
        }
        buildInvite.setAggregate(payload.getAggregate());
      }

      // Check if invite already exiting if so then update instead of save;
      inviteModel = repository.save(buildInvite);
    }

    String link = appConfig.getPaydaiClientBaseUrl() + "/signup/invite?code=" + inviteModel.getInviteCode() + "&c_email=" + payload.getCompanyEmail();

    InviteDto inviteDto = InviteDto.getInviteDtoData(inviteModel, link);

    InviteRecord inviteRecord = inviteDtoMapper.apply(inviteDto);

    // SEND EMAIL NOTIFICATION // todo ensure to use correct paydai smtp for sending email
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
  @Transactional
  public JapiResponse acceptInvite(RegisterRequest request, String inviteCode) {
    // Check if invite exists
    InviteModel inviteModel = repository.findByInvite(inviteCode)
      .orElseThrow(() -> new NotFoundException("Invalid invite code"));

    String passwordHash = passwordEncoder.encode(request.getPassword());

    // Check if the personal email already exists
    EmailModel emailModel = emailRepository.findByEmail(request.getEmail())
      .orElseGet(() -> {
        // Create new user and email
        UserModel userModel = userRepository.save(
          UserModel.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .userType(UserType.SALES_REP)
            .country(request.getCountry().getName())
            .countryCode(request.getCountry().getCode())
            .build()
        );

        return emailRepository.save(
          EmailModel.builder()
            .email(request.getEmail())
            .passwordHash(passwordHash)
            .emailType(EmailType.PERSONAL)
            .access(true)
            .user(userModel)
            .build()
        );
      });

    // Create company email account
    EmailModel emailAddedWorkspace = emailRepository.save(
      EmailModel.builder()
        .email(inviteModel.getCompanyEmail())
        .emailType(EmailType.COMPANY)
        .access(false)
        .passwordHash(passwordHash)
        .user(emailModel.getUser())
        .build()
    );

    // Create commission setting
    CommissionSettingModel commissionSettingModel = commSettingRepository.save(
      CommissionSettingModel.builder()
        .commission(inviteModel.getCommission())
        .interval(inviteModel.getInterval())
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
        .removed(false)
        .commission(commissionSettingModel)
        .build()
    );

    // Handle different position types
    if (inviteModel.getPosition() != null) {
      List<UserWorkspaceModel> userWorkspaceModels = new ArrayList<>();

      if (inviteModel.getPosition().equals(PositionType.OVER_ALL_SETTER)) {
        UUID setterRoleId = roleRepository.findByRole("setter").getId();
        userWorkspaceModels = userWorkspaceRepository.findUserByRoleWorkspaces(setterRoleId, inviteModel.getWorkspace().getId());
      } else if (inviteModel.getPosition().equals(PositionType.OVER_ALL_CLOSER)) {
        UUID closerRoleId = roleRepository.findByRole("closer").getId();
        userWorkspaceModels = userWorkspaceRepository.findUserByRoleWorkspaces(closerRoleId, inviteModel.getWorkspace().getId());
      } else {
        if (!inviteModel.getSelectedSalesRep().isEmpty()) {
          for (UUID selectedRep : inviteModel.getSelectedSalesRep()) {
            UserWorkspaceModel foundWorkspaceModels = userWorkspaceRepository.findOneByUserId(selectedRep, inviteModel.getWorkspace().getId());
            if (foundWorkspaceModels != null) {
              userWorkspaceModels.add(foundWorkspaceModels);
            }
          }
        }

      }

      // Save team members
      if (!userWorkspaceModels.isEmpty())
        for (UserWorkspaceModel userWorkspace : userWorkspaceModels) {  // todo: check to find a better way to create many into database
          teamRepository.save(
            TeamModel.builder()
              .manager(emailModel.getUser())
              .userWorkspace(userWorkspaceModel)
              .positionType(inviteModel.getPosition())
              .member(userWorkspace.getUser())
              .workspace(userWorkspace.getWorkspace())
              .build()
          );
        }

      if (!userWorkspaceModels.isEmpty())
          repository.removeAssignedTeamMembers(inviteCode);
    }

    // Generate token
    String jwt = jwtService.generateToken(emailModel.getUser());

    // Remove invite
    repository.removeInvite(inviteCode);

    // Map role and workspace to records
    RoleRecord role = roleDtoMapper.apply(userWorkspaceModel.getRole());
    WorkspaceRecord workspace = workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace());

    // Build Auth DTO
    AuthModelDto buildAuthDto = AuthModelDto.getAuthData(emailModel.getUser(), emailModel, jwt, role, workspace);
    AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

    // Return success response
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

  @Override
  @TryCatchException
  public JapiResponse cancelInvite(String inviteCode) {
    InviteModel inviteModel = repository.findByInvite(inviteCode).orElseThrow(() -> new NotFoundException("Invalid invite code"));
    repository.removeInvite(inviteCode);
    return JapiResponse.success(null);
  }
}
