package com.paydai.api.presentation.dto.profile;

import com.paydai.api.domain.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ProfileDtoMapper implements Function<UserModel, ProfileRecord> {
  @Override
  public ProfileRecord apply(UserModel userModel) {
    return new ProfileRecord(
      userModel.getId(),
      userModel.getFirstName(),
      userModel.getLastName()
    );
  }
}
