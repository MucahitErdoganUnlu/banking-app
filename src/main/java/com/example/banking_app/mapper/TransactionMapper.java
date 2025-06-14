package com.example.banking_app.mapper;

import com.example.banking_app.dto.TransactionDto;
import com.example.banking_app.entity.Transaction;

public class TransactionMapper {
    
    // // unnecessary
    // public static Transaction mapToTransaction(TransactionDto transactionDto){
    //     Transaction transaction = new Transaction(
    //         transactionDto.id(),
    //         transactionDto.accountId(),
    //         transactionDto.amount(),
    //         transactionDto.transactionType(),
    //         transactionDto.timestamp()
    //     );

    //     return transaction;
    // }

    public static TransactionDto mapToTransactionDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto(
            transaction.getId(),
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getTimestamp()
        );

        return transactionDto;
    }
}
