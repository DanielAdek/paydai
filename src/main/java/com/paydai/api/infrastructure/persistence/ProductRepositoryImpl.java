package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.ProductModel;
import com.paydai.api.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepositoryImpl extends ProductRepository, JpaRepository<ProductModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * product_tbl WHERE stripe_product_id=?1")
  ProductModel findProduct(UUID stripeProductId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM product_tbl")
  List<ProductModel> findProducts();
}
