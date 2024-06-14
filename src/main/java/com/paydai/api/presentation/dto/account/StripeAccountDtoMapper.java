package com.paydai.api.presentation.dto.account;

import com.paydai.api.domain.model.StripeAccountModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StripeAccountDtoMapper implements Function<StripeAccountModel, StripeAccountRecord> {
  @Override
  public StripeAccountRecord apply(StripeAccountModel stripeAccModel) {
    return new StripeAccountRecord(
      stripeAccModel.getStripeAccountId(),
      stripeAccModel.getUserId(),
      stripeAccModel.getStripeId(),
      stripeAccModel.getPersonalEmail(),
      stripeAccModel.getCreatedAt(),
      stripeAccModel.getUpdatedAt()
    );
  }
}