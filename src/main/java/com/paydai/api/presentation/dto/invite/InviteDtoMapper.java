package com.paydai.api.presentation.dto.invite;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class InviteDtoMapper implements Function<InviteDto, InviteRecord> {
  @Override
  public InviteRecord apply(InviteDto inviteDto) {
    return new InviteRecord(
      inviteDto.getInviteId(),
      inviteDto.getEmail(),
      inviteDto.getInviteCode(),
      inviteDto.getCommission(),
      inviteDto.getAggregate(),
      inviteDto.getInterval(),
      inviteDto.getDuration(),
      inviteDto.getLink(),
      inviteDto.getCreatedAt(),
      inviteDto.getUpdatedAt()
    );
  }
}
