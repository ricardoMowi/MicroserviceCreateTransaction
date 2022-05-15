package com.nttdata.createTransaction.service;

import java.util.List;

import com.nttdata.createTransaction.entity.Transaction;

public interface TransactionService {
    List<Transaction> getAll();
    Transaction createTransaction(Transaction new_transaction);
}


