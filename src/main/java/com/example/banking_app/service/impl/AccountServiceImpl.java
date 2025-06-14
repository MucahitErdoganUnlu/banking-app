package com.example.banking_app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.banking_app.dto.AccountDto;
import com.example.banking_app.dto.TransferFundDto;
import com.example.banking_app.entity.Account;
import com.example.banking_app.exception.AccountException;
import com.example.banking_app.mapper.AccountMapper;
import com.example.banking_app.repository.AccountRepository;
import com.example.banking_app.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    private static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account does not exist.";

    public AccountServiceImpl(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
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

        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());
        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());
    }
}
