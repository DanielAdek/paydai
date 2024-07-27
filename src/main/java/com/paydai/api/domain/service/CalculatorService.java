package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.Map;

public interface CalculatorService {
  double calculatePFMax(double revenue);
  double calculateSalesRepCommission(double revenue, float salesRepPercent);
  double calculateSalesRepNetCloserOnly(double revenue, float closerPercent);
  double calculatePFMin(double revenue);
  double calculatePFS(double revenue);
  double calculatePFC(double revenue);
  double calculateSalesRepNetCloser(double revenue, float closerPercent);
  double calculateSalesRepNetSetter(double revenue, float setterPercent);
  JapiResponse displayCommissions(CalcRequest payload);
}
