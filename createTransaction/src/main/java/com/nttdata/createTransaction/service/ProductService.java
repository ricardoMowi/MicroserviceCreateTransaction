package com.nttdata.createTransaction.service;

import java.util.List;

import com.nttdata.createTransaction.entity.Product;

public interface ProductService {
    List<Product> getAll();
    Product createProduct(Product new_product);
}
