package com.paydai.api.presentation.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class AuthDtoMapper implements Function<AuthModelDto, AuthRecordDto> {
//  private static List<AuthAccess> getGrantedAuthorities(AuthModel authModel) {
//    List<AuthAccess> authorities = new ArrayList<>();
//
//    for (GrantedAuthority grantedAuthority : authModel.getAuthorities()) {
//      authorities.add(AuthAccess.valueOf(grantedAuthority.getAuthority()));
//    }
//
//    return authorities;
//  }

  @Override
  public AuthRecordDto apply(AuthModelDto authModel) {
    return new AuthRecordDto(
      authModel.getId(),
      authModel.getUserType(),
      authModel.getEmail(),
      authModel.getEmailType(),
      authModel.getStripeId(),
      authModel.getToken(),
      authModel.getCreatedAt(),
      authModel.getUpdatedAt()
    );
  }
}