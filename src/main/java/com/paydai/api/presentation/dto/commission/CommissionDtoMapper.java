package com.paydai.api.presentation.dto.commission;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.function.Function;

@Service
public class CommissionDtoMapper implements Function<CommissionDto, CommissionRecord> {
  @Override
  public CommissionRecord apply(CommissionDto commissionDto) {
    return new CommissionRecord(
      commissionDto.getPaydaiFeeMerchant(),
      commissionDto.getPaydaiFeeSetterPercent(),
      commissionDto.getPaydaiFeeCloserPercent(),
      commissionDto.getPaydaiFeeMerchantPercent(),
      commissionDto.getPaydaiFeeCloser(),
      commissionDto.getCloserCommission(),
      commissionDto.getCloserOnlyNet(),
      commissionDto.getPaydaiTotalComm(),
      commissionDto.getPaydaiFeeCloser(),
      commissionDto.getPaydaiFeeSetter(),
      commissionDto.getSetterCommission(),
      commissionDto.getSetterNet(),
      commissionDto.getCloserNet(),
      commissionDto.getPaydaiApplicationFee(),
      commissionDto.getCloserManagersCommissions() != null ? commissionDto.getCloserManagersCommissions() : new ArrayList<>(),
      commissionDto.getSetterManagersCommissions() != null ? commissionDto.getSetterManagersCommissions() : new ArrayList<>()
    );
  }
}
