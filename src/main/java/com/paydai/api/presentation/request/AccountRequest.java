package com.paydai.api.presentation.request;

import com.paydai.api.presentation.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
  String email;
  AccountType accountType;
}
