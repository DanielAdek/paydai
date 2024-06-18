package com.paydai.api.application;

import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.EmailRepository;
import com.paydai.api.domain.repository.InviteRepository;
import com.paydai.api.domain.repository.WorkspaceRepository;
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
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
  private final InviteRepository repository;
  private final WorkspaceRepository workspaceRepository;
  private final EmailRepository emailRepository;
  private final InviteDtoMapper inviteDtoMapper;
  private final EmailSenderService emailSenderService;
  private final AppConfig appConfig;
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
      // user with already existing personal email could be invite
      // check if the company email already exit
      // check if the personal email already exit
      // pick the code and collect invite info
          // role attached
      // delete invite from invite
      // save commission setting
      // save company and personal email if not exit any
      // save password for both
      // generate token
      // send welcome to paydai to email company
      // send stripe confirm account created if not created before
      // respond to client
      InviteModel inviteModel = repository.findByInvite(inviteCode);

      if (inviteModel != null) throw new NotFoundException("Invite Code is invalid");

      WorkspaceModel workspaceModel = workspaceRepository.findByWorkspaceId(inviteModel.getWorkspace().getWorkspaceId());

      UserModel userModel = UserModel.builder()
        .userType(UserType.SALES_REP)
        .firstName(request.getFirstName())
        .workspace(workspaceModel)
        .lastName(request.getLastName())
        .build();

      EmailModel emailModel = EmailModel.builder()
        .email(inviteModel.getEmail())
        .emailType(EmailType.COMPANY)
        .user(userModel)
        .build();

      return JapiResponse.success(null);
    }  catch (Exception e) { throw e; }
  }

}
