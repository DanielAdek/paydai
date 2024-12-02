package com.paydai.api.presentation.dto.account;

import com.paydai.api.domain.model.AccountLedgerModel;
import com.paydai.api.domain.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountDtoMapper implements Function<AccountLedgerModel, AccountRecord> {
  @Override
  public AccountRecord apply(AccountLedgerModel accountLedger) {
    return new AccountRecord(
      accountLedger.getBalance(),
      accountLedger.getPendBal(),
      accountLedger.getCurrency(),
      accountLedger.getUser().getId()
    );
  }
}