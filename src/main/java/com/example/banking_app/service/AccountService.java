package com.example.banking_app.service;

import java.util.List;
import com.example.banking_app.dto.AccountDto;
import com.example.banking_app.dto.TransferFundDto;

public interface AccountService {
    
    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);

    AccountDto deposit(Long id, double amount);

    AccountDto withdraw(Long id, double amount);

    List<AccountDto> getAllAccounts();
    
    void deleteAccount(Long id);

    void transferFunds(TransferFundDto transferFundDto);
}
