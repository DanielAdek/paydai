package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.repository.UserRepository;
import com.paydai.api.domain.service.AdminService;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountCollection;
import com.stripe.param.AccountListParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
  private final UserRepository userRepository;

  @Override
  @TryCatchException
  public JapiResponse deleteConnectedAccount(List<String> connectedAccounts) {
    connectedAccounts.forEach(connectAccount -> {
      Account resource;
      try {
        resource = Account.retrieve(connectAccount);
        resource.delete();
      } catch (StripeException e) {
        throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
      }
    });
    return JapiResponse.success(null);
  }

  @Override
  @TryCatchException
  public JapiResponse retrieveAllUsersStripeAccount() throws StripeException {
    AccountListParams params = AccountListParams.builder().build();
    AccountCollection accounts = Account.list(params);
    List<String> accountsIds = new ArrayList<>();
    accounts.getData().forEach(account -> {
      accountsIds.add(account.getId());
    });
    return JapiResponse.success(accountsIds);
  }
}
