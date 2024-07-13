package com.paydai.api.presentation.dto.account;

import com.paydai.api.domain.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StripeAccountDtoMapper implements Function<UserModel, StripeAccountRecord> {
  @Override
  public StripeAccountRecord apply(UserModel stripeAccModel) {
    return new StripeAccountRecord(
      stripeAccModel.getId(),
      stripeAccModel.getStripeId()
    );
  }
}