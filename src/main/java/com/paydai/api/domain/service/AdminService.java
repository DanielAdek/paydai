package com.paydai.api.domain.service;

import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

import java.util.List;

public interface AdminService {
  JapiResponse deleteConnectedAccount(List<String> connectedAccounts) throws StripeException;
  JapiResponse retrieveAllUsersStripeAccount() throws StripeException;
}
