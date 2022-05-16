package com.nttdata.createTransaction.repository;

import java.util.List;

import com.nttdata.createTransaction.entity.Transaction;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository <Transaction, String> {
    List<Transaction> findByIdProduct (String IdProduct);
}
