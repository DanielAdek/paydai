package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.model.AccountLedgerModel;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import com.paydai.api.domain.service.AccountLedgerService;
import com.paydai.api.presentation.dto.account.AccountDtoMapper;
import com.paydai.api.presentation.dto.account.AccountRecord;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountLedgerServiceImpl implements AccountLedgerService {
  private final AccountLedgerRepository repository;
  private final AccountDtoMapper accountDtoMapper;

  @Override
  @TryCatchException
  public JapiResponse getUserAccountLedger(UUID userId) throws StripeException {
    AccountLedgerModel accountLedgerModel = updateSalesRepAccountLedgerBalance(userId);

    AccountRecord accountRecord = accountDtoMapper.apply(accountLedgerModel);

    return JapiResponse.success(accountRecord);
  }

  @Override
  @TryCatchException
  public AccountLedgerModel updateSalesRepAccountLedgerBalance(UUID userId) throws StripeException {
    AccountLedgerModel accountLedgerModel = repository.findAccountLedgerByUser(userId);

    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(accountLedgerModel.getUser().getStripeId()).build();

    Balance balances = Balance.retrieve(requestOptions);

    Balance.Available balAvailable = balances.getAvailable().stream().filter(bal -> bal.getCurrency().equals(accountLedgerModel.getCurrency())).findFirst().get();

    Balance.Pending balPending = balances.getPending().stream().filter(bal -> bal.getCurrency().equals(accountLedgerModel.getCurrency())).findFirst().get();

    accountLedgerModel.setBalance((double) balAvailable.getAmount() / 100);

    double pendBal = (double) balPending.getAmount() / 100;

    accountLedgerModel.setPendBal(pendBal <=0 ? 0 : pendBal);

    return repository.save(accountLedgerModel);
  }
}
