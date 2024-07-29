package com.paydai.api.application;

import com.paydai.api.domain.service.CalculatorService;
import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {
  private static final float MERCHANT_FEE_PERCENTAGE = 0.015F;
  private static final float SALES_REP_FEE_PERCENTAGE = 0.005F;
  private static final float SETTER_FEE_PERCENTAGE = 33.33F / 100;
  private static final float CLOSER_FEE_PERCENTAGE = 66.67F / 100;

  @Override
  public double calculatePFMax(double revenue) {
    return revenue * MERCHANT_FEE_PERCENTAGE;
  }

  @Override
  public double calculatePFMin(double revenue) {
    return revenue * SALES_REP_FEE_PERCENTAGE;
  }

  @Override
  public double calculatePFS(double revenue) {
    return calculatePFMin(revenue) * SETTER_FEE_PERCENTAGE;
  }

  @Override
  public double calculatePFC(double revenue) {
    return calculatePFMin(revenue) * CLOSER_FEE_PERCENTAGE;
  }

  @Override
  public double calculateSalesRepCommission(double revenue, float salesRepPercent) {
    return Math.round(revenue * (salesRepPercent / 100));
  }

  @Override
  public double calculateSalesRepNetCloserOnly(double revenue, float closerPercent) {
    return calculateSalesRepCommission(revenue, closerPercent) - calculatePFMin(revenue);
  }

  @Override
  public double calculateSalesRepNetCloser(double revenue, float closerPercent) {
    return calculateSalesRepCommission(revenue, closerPercent) - calculatePFC(revenue);
  }

  @Override
  public double calculateSalesRepNetSetter(double revenue, float setterPercent) {
    return calculateSalesRepCommission(revenue, setterPercent) - calculatePFS(revenue);
  }

  @Override
  public JapiResponse displayCommissions(CalcRequest payload) {
    try {
      Map<String, Object> commissions = new HashMap<>();
      commissions.put("paydaiFeeMerchant", calculatePFMax(payload.getRevenue()));

      if (payload.getScenario() == 1) {
        commissions.put("paydaiFeeCloserOnly", calculatePFMin(payload.getRevenue()));
        commissions.put("closerCommission", calculateSalesRepCommission(payload.getRevenue(), payload.getCloserPercent()));
        commissions.put("closerOnlyNet", calculateSalesRepNetCloserOnly(payload.getRevenue(), payload.getCloserPercent()));
        double paydaiTotalComm = calculatePFMax(payload.getRevenue()) + calculatePFMin(payload.getRevenue());
        commissions.put("paydaiTotalComm", paydaiTotalComm);
      } else {
        commissions.put("paydaiFeeCloser", calculatePFC(payload.getRevenue()));
        commissions.put("paydaiFeeSetter", calculatePFS(payload.getRevenue()));
        commissions.put("setterCommission", calculateSalesRepCommission(payload.getRevenue(), payload.getSetterPercent()));
        commissions.put("closerCommission", calculateSalesRepCommission(payload.getRevenue(), payload.getCloserPercent()));
        commissions.put("setterNet", calculateSalesRepNetSetter(payload.getRevenue(), payload.getSetterPercent()));
        commissions.put("closerNet", calculateSalesRepNetCloser(payload.getRevenue(), payload.getCloserPercent()));
        double paydaiTotalComm = calculatePFMax(payload.getRevenue()) + calculatePFMin(payload.getRevenue());
        commissions.put("paydaiTotalComm", paydaiTotalComm);
      }
      return JapiResponse.success(commissions);
    } catch (Exception e) { throw e; }
  }
}
