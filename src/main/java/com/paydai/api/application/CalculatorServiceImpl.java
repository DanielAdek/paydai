package com.paydai.api.application;

import com.paydai.api.domain.model.CommSplitScenarioType;
import com.paydai.api.domain.model.InvoiceManagerModel;
import com.paydai.api.domain.model.TeamModel;
import com.paydai.api.domain.service.CalculatorService;
import com.paydai.api.presentation.dto.commission.CommissionDto;
import com.paydai.api.presentation.dto.commission.CommissionDtoMapper;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import com.paydai.api.presentation.request.CalcRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

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
    return revenue * (salesRepPercent / 100);
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
  public double formatAmount(double amt) {
    DecimalFormat df =  new DecimalFormat("#.00");
    return Double.parseDouble(df.format(amt));
  }

  @Override
  public CommissionRecord displayCommissions(CalcRequest payload) {
    try {
      double paydaiFeeMerchant = formatAmount(calculatePFMax(payload.getRevenue()));

      CommissionDto commissionDto = CommissionDto.builder()
        .paydaiFeeMerchant(paydaiFeeMerchant)
        .paydaiFeeMerchantPercent(MERCHANT_FEE_PERCENTAGE)
        .build();

      if (payload.getScenario() == CommSplitScenarioType.CLOSER_ONLY) {
        // Calculate closer's commission
        double _closerCommission = calculateSalesRepCommission(payload.getRevenue(), payload.getCloserPercent());
        double closerCommission = formatAmount(_closerCommission);
        commissionDto.setCloserCommission(closerCommission);

        // calculate all managers fee if applicable
        double closerManagerCommission = 0.0;
        if (payload.getCloserManager() != null) {
          if (!payload.getCloserManager().isEmpty())
            closerManagerCommission = payload.getCloserManager().stream()
              .map(teamModel -> this.createInvoiceManagerModel(teamModel, payload))
              .peek(invoiceManagerModel -> {
                commissionDto.getCloserManagersCommissions().add(invoiceManagerModel);
              })
              .mapToDouble(InvoiceManagerModel::getSnapshotCommManager)
              .sum();
        }

        // Set fee to be collected by closer
        commissionDto.setPaydaiFeeCloserOnly(formatAmount(calculatePFMin(payload.getRevenue())));

        // Set closer's net income
        commissionDto.setCloserNet(formatAmount(calculateSalesRepNetCloserOnly(payload.getRevenue(), payload.getCloserPercent())));

        // Set Paydai's income
        commissionDto.setPaydaiFeeCloserPercent(SALES_REP_FEE_PERCENTAGE);
        commissionDto.setPaydaiTotalComm(formatAmount(calculatePFMax(payload.getRevenue()) + calculatePFMin(payload.getRevenue())));
        commissionDto.setPaydaiApplicationFee(paydaiFeeMerchant + closerCommission + closerManagerCommission);
      }

      if (payload.getScenario() == CommSplitScenarioType.CLOSER_AND_SETTER) {
        // Calculate sales-reps commissions
        double _setterCommission = calculateSalesRepCommission(payload.getRevenue(), payload.getSetterPercent());
        double _closerCommission = calculateSalesRepCommission(payload.getRevenue(), payload.getCloserPercent());
        double setterCommission = formatAmount(_setterCommission);
        double closerCommission = formatAmount(_closerCommission);
        commissionDto.setSetterCommission(setterCommission);
        commissionDto.setCloserCommission(closerCommission);

        double closerManagerCommission = 0.0;

        if (payload.getCloserManager() != null)
          if (!payload.getCloserManager().isEmpty())
            closerManagerCommission = payload.getCloserManager().stream()
            .map(teamModel -> this.createInvoiceManagerModel(teamModel, payload))
            .peek(invoiceManagerModel -> {
              commissionDto.getCloserManagersCommissions().add(invoiceManagerModel);
            })
            .mapToDouble(InvoiceManagerModel::getSnapshotCommManager)
            .sum();

        double setterManagerCommission = 0.0;

        if (payload.getSetterManager() != null)
          if (!payload.getSetterManager().isEmpty())
            setterManagerCommission = payload.getSetterManager().stream()
            .map(teamModel -> this.createInvoiceManagerModel((TeamModel) teamModel, payload))
            .peek(invoiceManagerModel -> {
              commissionDto.getCloserManagersCommissions().add(invoiceManagerModel);
            })
            .mapToDouble(InvoiceManagerModel::getSnapshotCommManager)
            .sum();

        // Calculate sales rep fees
        commissionDto.setPaydaiFeeCloser(formatAmount(calculatePFC(payload.getRevenue())));
        commissionDto.setPaydaiFeeSetter(formatAmount(calculatePFS(payload.getRevenue())));

        // Calculate sales rep incomes
        commissionDto.setSetterNet(formatAmount(calculateSalesRepNetSetter(payload.getRevenue(), payload.getSetterPercent())));
        commissionDto.setCloserNet(formatAmount(calculateSalesRepNetCloser(payload.getRevenue(), payload.getCloserPercent())));

        // Calculate paydai fees
        commissionDto.setPaydaiTotalComm(formatAmount(calculatePFMax(payload.getRevenue()) + calculatePFMin(payload.getRevenue())));
        commissionDto.setPaydaiApplicationFee(formatAmount(setterCommission + closerCommission + paydaiFeeMerchant + closerManagerCommission + setterManagerCommission));
        commissionDto.setPaydaiFeeSetterPercent(SETTER_FEE_PERCENTAGE);
        commissionDto.setPaydaiFeeCloserPercent(CLOSER_FEE_PERCENTAGE);
      }

      return commissionDtoMapper.apply(commissionDto);
    } catch (Exception e) { throw e; }
  }

  private InvoiceManagerModel createInvoiceManagerModel(TeamModel teamModel, CalcRequest payload) {
    float commission = teamModel.getUserWorkspace().getCommission().getCommission();
    double managerCommission = formatAmount((commission / 100) * payload.getRevenue());
    return InvoiceManagerModel.builder()
      .manager(teamModel.getManager())
      .workspace(teamModel.getWorkspace())
      .snapshotCommManager(managerCommission)
      .snapshotCommManagerNet(managerCommission)
      .snapshotManagerFeePercent(0.0f)
      .snapshotCommManagerPercent(commission)
      .build();
  }
}
