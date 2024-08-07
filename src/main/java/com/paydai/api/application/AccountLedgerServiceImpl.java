package com.paydai.api.application;

import com.paydai.api.domain.model.AccountLedgerModel;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import com.paydai.api.domain.service.AccountLedgerService;
import com.paydai.api.presentation.dto.account.AccountDtoMapper;
import com.paydai.api.presentation.dto.account.AccountRecord;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountLedgerServiceImpl implements AccountLedgerService {
  private final AccountLedgerRepository repository;
  private final AccountDtoMapper accountDtoMapper;

  @Override
  public JapiResponse getUserAccountLedger(UUID userId, UUID workspaceId) {
    try {
      AccountLedgerModel accountLedgerModel = repository.findAccountLedgerByUserWorkspace(userId, workspaceId);

      AccountRecord accountRecord = accountDtoMapper.apply(accountLedgerModel);

      return JapiResponse.success(accountRecord);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getUserAccountsLedger(UUID userId) {
    try {
      List<AccountLedgerModel> accountLedgerModels = repository.findAccountLedgerByUser(userId);

      List<AccountRecord> accountRecords = accountLedgerModels
        .stream()
        .map(accountDtoMapper)
        .toList();

      return JapiResponse.success(accountRecords);
    } catch (Exception e) { throw e; }
  }
}
