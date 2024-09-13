package com.paydai.api.domain.service;

import com.paydai.api.domain.model.*;
import com.paydai.api.presentation.dto.AmountDto;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import com.paydai.api.presentation.dto.invoice.CommissionData;
import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.request.InvoiceRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.*;

import java.util.List;
import java.util.UUID;

public interface InvoiceHelperService {
  UserWorkspaceModel getUserWorkspaceModel(UUID userId, UUID workspaceId);
  String getConnectedAccountId(UserWorkspaceModel closerWorkspaceModel);
  Product createProductInStripe(InvoiceRequest payload, String connectedAccountId) throws StripeException;
  Price createPriceInStripe(InvoiceRequest payload, Product product, String connectedAccountId) throws StripeException;
  List<TeamModel> getTeamMembers(UserWorkspaceModel workspaceModel);
  CommissionData getSetterCommissionData(CustomerModel customerModel, InvoiceRequest payload, CommSplitScenarioType scenarioType);
  CommissionData fetchSetterCommissionData(CustomerModel customerModel, InvoiceRequest payload, CommSplitScenarioType scenarioType);
  Customer createCustomerInStripe(CustomerModel customerModel, String connectedAccountId) throws StripeException;
  CalcRequest buildCalcRequest(InvoiceRequest payload, CommissionSettingModel commissionSettingModel, CommissionData commissionData, CommSplitScenarioType scenarioType, List<TeamModel> closerManagers, List<TeamModel> setterManagers);
  Double getInvoiceAmount(InvoiceRequest payload);
  Invoice createStripeInvoice(InvoiceRequest payload, Customer customer, CommissionRecord commissionRecord, String connectedAccountId) throws StripeException;
  InvoiceItem createStripeInvoiceItem(Invoice invoice, Price price, Customer customer, InvoiceRequest payload, String connectedAccountId) throws StripeException;
  InvoiceModel saveInvoiceToDatabase(InvoiceRequest payload, AmountDto amountDto, CommissionRecord commissionRecord, UserWorkspaceModel closerWorkspaceModel, CustomerModel customerModel, CommissionSettingModel commissionSettingModel, Product product, Invoice invoice, InvoiceItem invoiceItem);
  ProductModel saveProductToDatabase(InvoiceRequest payload, Product product);
//  void saveManagerCommissions(InvoiceModel invoiceModel, CommissionRecord commissionRecord);
}
