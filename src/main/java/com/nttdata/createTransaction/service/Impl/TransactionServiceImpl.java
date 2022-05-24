package com.nttdata.createTransaction.service.Impl;

import java.util.List;

import com.nttdata.createTransaction.entity.Transaction;
import com.nttdata.createTransaction.repository.TransactionRepository;
import com.nttdata.createTransaction.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;
    @Override
    public List<Transaction> getAll() {
      return transactionRepository.findAll();
    }

    @Override
    public Transaction createTransaction(Transaction transaction){
      return transactionRepository.save(transaction);
    }
}