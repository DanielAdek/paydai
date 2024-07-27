package com.paydai.api.application;

import com.paydai.api.domain.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {
  @Override
  public Double calculateFeePaydai(Double revenue) {
    return 0.0;
  }

  @Override
  public Double calculateFeeSetter(Double revenue) {
    return 0.0;
  }

  @Override
  public Double calculateFeeCloser(Double revenue) {
    return 0.0;
  }
}
