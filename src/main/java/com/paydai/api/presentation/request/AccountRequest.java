package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
  String email;
  UserType accountType;
}
