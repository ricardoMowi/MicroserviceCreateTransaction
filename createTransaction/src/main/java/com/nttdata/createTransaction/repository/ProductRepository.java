package com.nttdata.createTransaction.repository;

import java.util.List;

import com.nttdata.createTransaction.entity.Product;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository <Product, String>{
    List<Product> findByClientId(String clientId);
    List<Product> findByProductTypeAndStatus(String ProductType, String Status);
    List<Product> findByProductTypeAndClientId (String ProductType, String clientId);    
}