package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryImpl extends UserRepository, JpaRepository<UserModel, UUID> {
  @Override
  Optional<UserModel> findByUserId(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_tbl WHERE id=?1")
  UserModel findUserById(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT stripe_id FROM user_tbl WHERE id=?1")
  String findUserStripeId(UUID userId);
}
