package com.paydai.api.presentation.dto.commission;

import com.paydai.api.domain.model.InvoiceManagerModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDto {
  private double paydaiFeeMerchant;
  private float paydaiFeeSetterPercent;
  private float paydaiFeeCloserPercent;
  private float paydaiFeeMerchantPercent;
  private double paydaiFeeCloserOnly;
  private double closerCommission;
  private double closerOnlyNet;
  private double paydaiTotalComm;
  private double paydaiFeeCloser;
  private double paydaiFeeSetter;
  private double setterCommission;
  private double setterNet;
  private double closerNet;
  private double paydaiApplicationFee;

  @Builder.Default
  private List<InvoiceManagerModel> closerManagersCommissions = new ArrayList<>();

  @Builder.Default
  private List<InvoiceManagerModel> setterManagersCommissions = new ArrayList<>();
}
