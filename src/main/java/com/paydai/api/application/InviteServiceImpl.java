package com.paydai.api.application;

import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.domain.service.InviteService;
import com.paydai.api.infrastructure.config.AppConfig;
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
  private final InviteRepository repository;
  private final UserRepository userRepository;
  private final EmailRepository emailRepository;
  private final PasswordEncoder passwordEncoder;
  private final InviteDtoMapper inviteDtoMapper;
  private final EmailSenderService emailSenderService;
  private final PasswordRepository passwordRepository;
  private final WorkspaceRepository workspaceRepository;
  private final CommSettingRepository commSettingRepository;
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
        .email(payload.getCompanyEmail())
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
      // check if invite exit
      InviteModel inviteModel = repository.findByInvite(inviteCode);

      if (inviteModel == null) {
        throw new NotFoundException("Invalid invite code");
      }

      // check if the company email already exit
      EmailModel emailModel = emailRepository.findEmailQuery(request.getEmail());


      // check if the personal email already exit


      // pick the code and collect invite info
          // role attached


      // delete invite from invite
      repository.removeInvite(inviteCode);

      // save commission setting
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

      // Create user paydai account
      UserModel userModel = UserModel.builder()
//        .workspace()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .userType(UserType.SALES_REP)
//        .userWorkspaces()
        .build();

      // save company and personal email if not exit any
      EmailModel emailToSave = EmailModel.builder()
        .email(request.getEmail())
        .emailType(EmailType.COMPANY)
//        .workspace()
        .user(userModel)
//        .password()
        .build();
      EmailModel savedEmail = emailRepository.save(emailToSave);

      // Password hash
      PasswordModel buildPassword = PasswordModel.builder()
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .user(userModel)
        .email(savedEmail)
        .build();
      passwordRepository.save(buildPassword);

      // generate token

      // send welcome to paydai to email company

      // send stripe confirm account created if not created before

      WorkspaceModel workspaceModel = workspaceRepository.findByWorkspaceId(inviteModel.getWorkspace().getWorkspaceId());

      UserModel _userModel = UserModel.builder()
        .userType(UserType.SALES_REP)
        .firstName(request.getFirstName())
        .workspace(workspaceModel)
        .lastName(request.getLastName())
        .build();

      EmailModel _emailModel = EmailModel.builder()
        .email(inviteModel.getEmail())
        .emailType(EmailType.COMPANY)
        .user(userModel)
        .build();

      return JapiResponse.success(null);
    }  catch (Exception e) { throw e; }
  }

}
