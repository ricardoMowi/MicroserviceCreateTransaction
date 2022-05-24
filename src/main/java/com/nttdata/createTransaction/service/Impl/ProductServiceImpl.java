package com.nttdata.createTransaction.service.Impl;

import java.util.List;

import com.nttdata.createTransaction.entity.Product;
import com.nttdata.createTransaction.repository.ProductRepository;
import com.nttdata.createTransaction.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Override
    public List<Product> getAll() {
      return productRepository.findAll();
    }

    @Override
    public Product createProduct(Product product){
      return productRepository.save(product);
    }
}