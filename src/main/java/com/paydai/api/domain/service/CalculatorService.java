package com.paydai.api.domain.service;

import java.util.Map;

public interface CalculatorService {
  Double calculateFeePaydai(Double revenue);
  Double calculateFeeSetter(Double revenue);
  Double calculateFeeCloser(Double revenue);
}
