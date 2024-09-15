package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.model.TxnStatusType;
import com.paydai.api.domain.model.PayoutTxnModel;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.domain.repository.PayoutTxnRepository;
import com.paydai.api.domain.service.PayoutTxnService;
import com.paydai.api.presentation.dto.transaction.PayoutTxnDtoMapper;
import com.paydai.api.presentation.dto.transaction.PayoutTxnRecord;
import com.paydai.api.presentation.request.PayoutRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Payout;
import com.stripe.model.Topup;
import com.stripe.net.RequestOptions;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.TopupCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutTxnServiceImpl implements PayoutTxnService {
  private final PayoutTxnRepository repository;
  private final PayoutTxnDtoMapper payoutTxnDtoMapper;

  /**
   * @desc This is used to send funds out of paydai to local bank
   */
  @Override
  @TryCatchException
  public JapiResponse createPayout(PayoutRequest payload) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    Payout payout;
    try {
      PayoutCreateParams params = PayoutCreateParams.builder()
        .setAmount(Double.valueOf(payload.getAmount()).longValue())
        .setCurrency(payload.getCurrency())
        .setMethod(payload.getType())
        .setSourceType(payload.getSourceType())
        .setDescription(payload.getDescription())
        .setDestination(payload.getDestination())
        .build();

      RequestOptions requestOptions = RequestOptions.builder()
        .setStripeAccount(userModel.getStripeId())
        .build();

      payout = Payout.create(params, requestOptions);
    } catch (StripeException e) {
      throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
    }

    repository.save(
      PayoutTxnModel.builder()
        .payoutInitiatedDate(LocalDateTime.now())
        .amount(payload.getAmount())
        .currency(payload.getCurrency())
        .type(payload.getType())
        .sourceType(payload.getSourceType())
        .user(userModel)
        .workspace(WorkspaceModel.builder().id(payload.getWorkspaceId()).build())
        .status(TxnStatusType.PAYMENT_CREATED)
        .build()
    );
    return JapiResponse.success(payout.getStatus());
  }

  @Override
  @TryCatchException
  public JapiResponse getUserPayoutTransactions(UUID userId) {
    List<PayoutTxnModel> payoutTxnModels = repository.findUserPayouts(userId);
    List<PayoutTxnRecord> payoutTxnRecord = payoutTxnModels.stream().map(payoutTxnDtoMapper).toList();
    return JapiResponse.success(payoutTxnRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getUserPayoutTransactions(UUID userId, UUID workspaceId) {
    List<PayoutTxnModel> payoutTxnModels = repository.findUserWorkspacePayouts(userId, workspaceId);
    List<PayoutTxnRecord> payoutTxnRecord = payoutTxnModels.stream().map(payoutTxnDtoMapper).toList();
    return JapiResponse.success(payoutTxnRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse topUpAccount(double amount, String currency) throws StripeException {
    TopupCreateParams params = TopupCreateParams.builder()
      .setAmount(Double.valueOf(amount).longValue() * 100)
      .setCurrency(currency)
      .setDescription("Top up test")
      .setStatementDescriptor("Top-up")
      .build();

    Topup topup = Topup.create(params);

    Map<String, Object> response = new HashMap<>();

    response.put("id", topup.getId());
    response.put("amount", topup.getAmount());
    response.put("status", topup.getStatus());
    response.put("created", topup.getCreated());
    response.put("currency", topup.getCurrency());

    return JapiResponse.success(response);
  }
}
