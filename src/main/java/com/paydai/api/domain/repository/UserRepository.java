package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<UserModel> findById(UUID userId);
  UserModel save(UserModel buildUser);
  UserModel findUserById(UUID userId);
//  UserModel findUserStripeId(UUID userId);
}
