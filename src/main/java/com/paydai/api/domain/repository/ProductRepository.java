package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.ProductModel;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
  ProductModel save(ProductModel buildProduct);
  List<ProductModel> findProducts();
  ProductModel findProduct(UUID stripeProductId);
}
