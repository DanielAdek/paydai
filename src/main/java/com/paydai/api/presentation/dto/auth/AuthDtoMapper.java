package com.paydai.api.presentation.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class AuthDtoMapper implements Function<AuthModelDto, AuthRecordDto> {

  @Override
  public AuthRecordDto apply(AuthModelDto authModelDto) {
    return new AuthRecordDto(
      authModelDto.getId(),
      authModelDto.getUserType(),
      authModelDto.getEmail(),
      authModelDto.getEmailType(),
      authModelDto.getStripeId(),
      authModelDto.getToken(),
      authModelDto.getCreatedAt(),
      authModelDto.getUpdatedAt(),
      authModelDto.getRole(),
      authModelDto.getWorkspace()
    );
  }
}