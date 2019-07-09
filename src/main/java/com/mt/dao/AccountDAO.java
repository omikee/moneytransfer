package com.mt.dao;

import com.mt.exception.CustomException;
import com.mt.model.Account;
import com.mt.model.UserTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    List<Account> getAllAccounts() throws CustomException;

    Account getAccountById(long accountId) throws CustomException;

    long createAccount(Account account) throws CustomException;

    int deleteAccountById(long accountId) throws CustomException;

    int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException;

    int transferAccountBalance(UserTransaction userTransaction) throws CustomException;

}
