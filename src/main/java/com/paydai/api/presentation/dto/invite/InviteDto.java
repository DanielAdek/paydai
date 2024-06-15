package com.paydai.api.presentation.dto.invite;

import com.paydai.api.domain.model.AggregateType;
import com.paydai.api.domain.model.InviteModel;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteDto {
  private UUID inviteId;
  private String email;
  private String inviteCode;
  private Double commission;
  private AggregateType aggregate;
  private int interval;
  private String duration;
  private String link;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static InviteDto getInviteDtoData(InviteModel inviteModel, String link) {
    return new InviteDto(
      inviteModel.getInviteId(),
      inviteModel.getEmail(),
      inviteModel.getInviteCode(),
      inviteModel.getCommission(),
      inviteModel.getAggregate(),
      inviteModel.getInterval(),
      inviteModel.getDuration(),
      link,
      inviteModel.getCreatedAt(),
      inviteModel.getUpdatedAt()
    );
  }
}
