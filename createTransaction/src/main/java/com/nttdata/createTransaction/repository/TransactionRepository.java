package com.nttdata.createTransaction.repository;

import com.nttdata.createTransaction.entity.Transaction;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository <Transaction, String> {
    
}
