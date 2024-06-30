package com.paydai.api.application;

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
import com.paydai.api.presentation.request.EmailRequest;
import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
  private final AppConfig appConfig;
  private final JwtAuthService jwtService;
  private final InviteRepository repository;
  private final UserRepository userRepository;
  private final EmailRepository emailRepository;
  private final PasswordEncoder passwordEncoder;
  private final InviteDtoMapper inviteDtoMapper;
  private final EmailSenderService emailSenderService;
  private final PasswordRepository passwordRepository;
  private final AuthDtoMapper authenticationDTOMapper;
  private final CommSettingRepository commSettingRepository;
  private final StripeAccountRepository stripeAccountRepository;
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
  public JapiResponse createInvite(InviteRequest payload) throws MessagingException {
    try {

      InviteModel buildInvite = InviteModel.builder()
        .inviteCode(this.generateInviteCode())
        .companyEmail(payload.getCompanyEmail())
        .workspace(WorkspaceModel.builder().workspaceId(payload.getWorkspaceId()).build())
        .commission(payload.getCommission())
        .interval(payload.getInterval())
        .duration(payload.getDuration())
        .role(RoleModel.builder().roleId(payload.getRoleId()).build())
        .build();

      if (payload.getAggregate() != null) buildInvite.setAggregate(payload.getAggregate());

      // Check if invite already exiting if so then update instead of save;
      InviteModel inviteModel = repository.save(buildInvite);

      String link = appConfig.getPaydaiClientBaseUrl() + "/signup/invite?code=" + buildInvite.getInviteCode() + "&c_email=" + payload.getCompanyEmail();

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
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse acceptInvite(RegisterRequest request, String inviteCode) {
    try {
      // Check if invite exit
      InviteModel inviteModel = repository.findByInvite(inviteCode);

      if (inviteModel == null) throw new NotFoundException("Invalid invite code");

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
            .emailType(EmailType.PERSONAL)
            .user(userModel)
            .build()
        );

        // Create password hash personal access
        passwordRepository.save(
          PasswordModel.builder()
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .user(emailModel.getUser())
            .email(emailModel)
            .build()
        );
      }

      // Create email account company
      EmailModel emailAddedWorkspace = emailRepository.save(
        EmailModel.builder()
          .email(inviteModel.getCompanyEmail())
          .emailType(EmailType.COMPANY)
          .workspace(inviteModel.getWorkspace())
          .user(emailModel.getUser())
          .build()
      );

      // Create commission setting
      CommissionSettingModel buildCommSettings = CommissionSettingModel.builder()
        .commission(inviteModel.getCommission())
        .emailId(emailModel)
        .interval(inviteModel.getInterval())
        .aggregate(inviteModel.getAggregate())
        .duration(inviteModel.getDuration())
        .workspaceId(inviteModel.getWorkspace().getWorkspaceId())
        .role(inviteModel.getRole())
        .build();
      commSettingRepository.save(buildCommSettings);


      // Create password hash workspace account
      passwordRepository.save(
        PasswordModel.builder()
          .passwordHash(passwordEncoder.encode(request.getPassword()))
          .user(emailModel.getUser())
          .email(emailAddedWorkspace)
          .build()
      );

      // Create workspace to user model
      userWorkspaceRepository.save(
        UserWorkspaceModel
          .builder()
          .user(emailModel.getUser())
          .workspace(inviteModel.getWorkspace())
          .role(inviteModel.getRole())
          .build()
      );

      // generate token
      String jwt = jwtService.generateToken(emailModel.getUser());

      // Find stripe account;
      StripeAccountModel stripeAccountModel = stripeAccountRepository.findByUser(emailModel.getUser().getUserId());

      String stripeId = stripeAccountModel != null ? stripeAccountModel.getStripeId() : null;

      // delete invite from invite
      repository.removeInvite(inviteCode);

      // send welcome to paydai to email company

      AuthModelDto buildAuthDto = AuthModelDto.getAuthData(emailModel.getUser(), emailModel, jwt, stripeId);

      AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

      return JapiResponse.success(auth);
    }  catch (Exception e) { throw e; }
  }
}
