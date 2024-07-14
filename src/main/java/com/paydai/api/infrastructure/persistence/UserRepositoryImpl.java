package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryImpl extends UserRepository, JpaRepository<UserModel, UUID> {
  @Override
  @NotNull
  Optional<UserModel> findById(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_tbl WHERE id=?1")
  UserModel findUserById(UUID userId);

  @Modifying
  @Transactional
  @Override
  @Query(nativeQuery = true, value = "UPDATE user_tbl SET stripe_id=:stripeId, stripe_email=:stripeEmail WHERE id=:userId")
  void updateUserStripe(@Param("userId") UUID userId, @Param("stripeId") String stripeId, @Param("stripeEmail") String stripeEmail);

  //  @Override
//  @Query(nativeQuery = true, value = "SELECT stripe_id FROM user_tbl WHERE id=?1")
//  UserModel findUserStripeId(UUID userId);
}
