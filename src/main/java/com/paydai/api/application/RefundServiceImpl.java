package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.RefundRepository;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.service.ProfileService;
import com.paydai.api.domain.service.RefundService;
import com.paydai.api.presentation.dto.refund.RefundDtoMapper;
import com.paydai.api.presentation.dto.refund.RefundRecord;
import com.paydai.api.presentation.request.RefundRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
  private final RefundRepository repository;
  private final ProfileService profileService;
  private final RefundDtoMapper refundDtoMapper;
  private final InvoiceRepository invoiceRepository;

  @Override
  @TryCatchException
  public JapiResponse create(RefundRequest payload) {
    InvoiceModel invoiceModel = invoiceRepository.findByInvoiceCode(payload.getInvoiceCode());

    if (invoiceModel == null) throw new NotFoundException("Invalid Invoice Id");

    UserModel userModel = UserModel.builder().id(payload.getSalesRepId()).build();

    if (userModel == null) throw new NotFoundException("Invalid User Id");

    WorkspaceModel workspaceModel = WorkspaceModel.builder().id(payload.getWorkspaceId()).build();

    if (workspaceModel == null) throw new NotFoundException("Invalid Workspace Id");

    RefundModel refundModel = repository.save(
      RefundModel.builder()
        .amount(payload.getAmount())
        .totalPaid(0.0)
        .status(RefundStatus.PENDING)
        .reason(payload.getReason())
        .user(userModel)
        .invoice(invoiceModel)
        .workspace(workspaceModel)
        .build()
    );
    return JapiResponse.success(refundDtoMapper.apply(refundModel));
  }

  @Override
  @TryCatchException
  public JapiResponse getRefundRequests(UUID workspaceId) {
    UserModel userModel = profileService.getLoggedInUser();
    List<RefundModel> refundModels = repository.findRefundRequests(userModel.getId(), workspaceId);
    if (refundModels == null || refundModels.isEmpty()) {
      return JapiResponse.success(refundModels);
    }
    List<RefundRecord> refundRecords = refundModels.stream().map(refundDtoMapper).toList();
    return JapiResponse.success(refundRecords);
  }

  /**
   * @desc this endpoint is for merchant use
   * @param workspaceId
   * @return
   */
  @Override
  @TryCatchException
  public JapiResponse getRefundsRequests(UUID workspaceId) {
    List<RefundModel> refundModels = repository.findRefundsRequests(workspaceId);
    if (refundModels == null || refundModels.isEmpty()) {
      return JapiResponse.success(refundModels);
    }
    List<RefundRecord> refundRecords = refundModels.stream().map(refundDtoMapper).toList();
    return JapiResponse.success(refundRecords);
  }

  @Override
  @TryCatchException
  public JapiResponse getLiabilityBalance(UUID workspaceId) {
    UserModel userModel = profileService.getLoggedInUser();
    double liability = Optional.ofNullable(repository.findLiabilities(userModel.getId(), workspaceId)).orElse(0.0);
    Map<String, Double> response = new HashMap<>();
    response.put("liability", liability);
    return JapiResponse.success(response);
  }

  @Override
  @TryCatchException
  public JapiResponse getLiabilityBalance() {
    UserModel userModel = profileService.getLoggedInUser();
    double liability = Optional.ofNullable(repository.findTotalLiabilities(userModel.getId())).orElse(0.0);
    Map<String, Double> response = new HashMap<>();
    response.put("liability", liability);
    return JapiResponse.success(response);
  }
}
