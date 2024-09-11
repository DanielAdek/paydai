package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.InvoiceManagerModel;
import com.paydai.api.domain.model.PositionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvolvedManagerDto {
  private UUID managerId;
  private String manager;
  private double snapshotCommManager;
  private double snapshotCommManagerNet;
  private float snapshotCommManagerPercent;

  public static InvolvedManagerDto getInvolvedManagerDto(InvoiceManagerModel invoiceManagerModel) {
    return new InvolvedManagerDto(
      invoiceManagerModel.getManager().getId(),
      invoiceManagerModel.getManager().getFirstName(),
      invoiceManagerModel.getSnapshotCommManager(),
      invoiceManagerModel.getSnapshotCommManagerNet(),
      invoiceManagerModel.getSnapshotCommManagerPercent()
    );
  }
}
