package com.example.banking_app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.banking_app.dto.AccountDto;
import com.example.banking_app.dto.TransactionDto;
import com.example.banking_app.dto.TransferFundDto;
import com.example.banking_app.entity.Account;
import com.example.banking_app.entity.Transaction;
import com.example.banking_app.exception.AccountException;
import com.example.banking_app.mapper.AccountMapper;
import com.example.banking_app.mapper.TransactionMapper;
import com.example.banking_app.repository.AccountRepository;
import com.example.banking_app.repository.TransactionRepository;
import com.example.banking_app.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    private static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account does not exist.";

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";

    public AccountServiceImpl(AccountRepository accountRepository,
                                TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto){
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id){
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_EXIST_MESSAGE));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount){
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_EXIST_MESSAGE));
        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount){
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_EXIST_MESSAGE));

        if (account.getBalance() < amount){
            throw new RuntimeException("Insufficient Balance!");
        }
        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts(){
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id){
        accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_EXIST_MESSAGE));
        accountRepository.deleteById(id);


    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {
        
        Account fromAccount = accountRepository.findById(transferFundDto.fromAccountId())
                                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_EXIST_MESSAGE));

        Account toAccount = accountRepository.findById(transferFundDto.toAccountId())
        .orElseThrow(() -> new AccountException(ACCOUNT_NOT_EXIST_MESSAGE));

        if (fromAccount.getBalance() - transferFundDto.amount() < 0){
            throw new RuntimeException("Insufficient balance!");
        }

        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());
        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

        Transaction transaction = new Transaction();
        transaction.setAccountId(transferFundDto.fromAccountId());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);

        return transactions.stream()
                .map((transaction) -> TransactionMapper.mapToTransactionDto(transaction))
                .collect(Collectors.toList());

    }
}
