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
      accountLedger.getRevenue(),
      accountLedger.getLiability(),
      accountLedger.getUser().getId(),
      accountLedger.getWorkspace().getId()
    );
  }
}