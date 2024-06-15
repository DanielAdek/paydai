package com.paydai.api.application;

import com.paydai.api.domain.model.InviteModel;
import com.paydai.api.domain.model.RoleModel;
import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.domain.repository.InviteRepository;
import com.paydai.api.domain.repository.WorkspaceRepository;
import com.paydai.api.domain.service.InviteService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.dto.invite.InviteDto;
import com.paydai.api.presentation.dto.invite.InviteDtoMapper;
import com.paydai.api.presentation.dto.invite.InviteRecord;
import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
  private final InviteRepository repository;
  private final WorkspaceRepository workspaceRepository;
  private final InviteDtoMapper inviteDtoMapper;
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
  public JapiResponse createInvite(InviteRequest payload) {
    try {
//      WorkspaceModel workspace = workspaceRepository.findByWorkspaceId(payload.getWorkspaceId());

      InviteModel buildInvite = InviteModel.builder()
        .inviteCode(this.generateInviteCode())
        .email(payload.getEmail())
        .workspace(WorkspaceModel.builder().workspaceId(payload.getWorkspaceId()).build())
        .aggregate(payload.getAggregate())
        .commission(payload.getCommission())
        .interval(payload.getInterval())
        .duration(payload.getDuration())
        .role(RoleModel.builder().roleId(payload.getRoleId()).build())
        .build();

      InviteModel inviteModel = repository.save(buildInvite);

      String link = appConfig.getPaydaiClientBaseUrl() + "/invite/" + buildInvite.getInviteCode();

      InviteDto inviteDto = InviteDto.getInviteDtoData(inviteModel, link);

      InviteRecord inviteRecord = inviteDtoMapper.apply(inviteDto);

      return JapiResponse.success(inviteRecord);
    } catch (Exception e) { throw e; }
  }
}
