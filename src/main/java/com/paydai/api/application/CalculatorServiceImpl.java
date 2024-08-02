package com.paydai.api.application;

import com.paydai.api.domain.model.CommSplitScenarioType;
import com.paydai.api.domain.service.CalculatorService;
import com.paydai.api.presentation.dto.commission.CommissionDto;
import com.paydai.api.presentation.dto.commission.CommissionDtoMapper;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import com.paydai.api.presentation.request.CalcRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {
  private final CommissionDtoMapper commissionDtoMapper;
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
  public CommissionRecord displayCommissions(CalcRequest payload) {
    try {
      CommissionDto commissionDto = CommissionDto.builder().paydaiFeeMerchant(calculatePFMax(payload.getRevenue())).build();

      if (payload.getScenario() == CommSplitScenarioType.CLOSER_ONLY) {
        commissionDto.setPaydaiFeeCloserOnly(calculatePFMin(payload.getRevenue()));
        commissionDto.setCloserCommission(calculateSalesRepCommission(payload.getRevenue(), payload.getCloserPercent()));
        commissionDto.setCloserOnlyNet(calculateSalesRepNetCloserOnly(payload.getRevenue(), payload.getCloserPercent()));
        double paydaiTotalComm = calculatePFMax(payload.getRevenue()) + calculatePFMin(payload.getRevenue());
        commissionDto.setPaydaiTotalComm(paydaiTotalComm);
      }
      if (payload.getScenario() == CommSplitScenarioType.CLOSER_AND_SETTER) {
        commissionDto.setPaydaiFeeCloser(calculatePFC(payload.getRevenue()));
        commissionDto.setPaydaiFeeSetter(calculatePFS(payload.getRevenue()));
        commissionDto.setSetterCommission(calculateSalesRepCommission(payload.getRevenue(), payload.getSetterPercent()));
        commissionDto.setCloserCommission(calculateSalesRepCommission(payload.getRevenue(), payload.getCloserPercent()));
        commissionDto.setSetterNet(calculateSalesRepNetSetter(payload.getRevenue(), payload.getSetterPercent()));
        commissionDto.setCloserNet(calculateSalesRepNetCloser(payload.getRevenue(), payload.getCloserPercent()));
        double paydaiTotalComm = calculatePFMax(payload.getRevenue()) + calculatePFMin(payload.getRevenue());
        commissionDto.setPaydaiTotalComm(paydaiTotalComm);
      }
      return commissionDtoMapper.apply(commissionDto);
    } catch (Exception e) { throw e; }
  }
}
