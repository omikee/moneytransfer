package com.mt.dao;

import com.mt.exception.CustomException;
import com.mt.model.Account;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.*;

public class TestAccountDAO {

    private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    @BeforeClass
    public static void setup() {
        // prepare test database and test data. Test data are initialised from
        // src/test/resources/test.sql
        h2DaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllAccounts() throws CustomException {
        List<Account> allAccounts = h2DaoFactory.getAccountDAO().getAllAccounts();
        assertTrue(allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountById() throws CustomException {
        Account account = h2DaoFactory.getAccountDAO().getAccountById(1L);
        assertEquals("user1", account.getUserName());
    }

    @Test
    public void testGetNonExistingAccById() throws CustomException {
        Account account = h2DaoFactory.getAccountDAO().getAccountById(100L);
        assertNull(account);
    }

    @Test
    public void testCreateAccount() throws CustomException {
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        Account a = new Account("test2", balance, "CNY");
        long aid = h2DaoFactory.getAccountDAO().createAccount(a);
        Account afterCreation = h2DaoFactory.getAccountDAO().getAccountById(aid);
        assertEquals("test2", afterCreation.getUserName());
        assertEquals("CNY", afterCreation.getCurrencyCode());
        assertEquals(balance, afterCreation.getBalance());
    }

    @Test
    public void testDeleteAccount() throws CustomException {
        int rowCount = h2DaoFactory.getAccountDAO().deleteAccountById(2L);
        // assert one row(user) deleted
        assertEquals(1, rowCount);
        // assert user no longer there
        assertNull(h2DaoFactory.getAccountDAO().getAccountById(2L));
    }

    @Test
    public void testDeleteNonExistingAccount() throws CustomException {
        int rowCount = h2DaoFactory.getAccountDAO().deleteAccountById(500L);
        // assert no row(user) deleted
        assertEquals(0, rowCount);
    }

    @Test
    public void testUpdateAccountBalanceSufficientFund() throws CustomException {
        BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdated = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaDeposit);
        assertEquals(1, rowsUpdated);
        assertEquals(afterDeposit, h2DaoFactory.getAccountDAO().getAccountById(1L).getBalance());
        BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
        assertEquals(1, rowsUpdatedW);
        assertEquals(afterWithDraw, h2DaoFactory.getAccountDAO().getAccountById(1L).getBalance());
    }

    @Test(expected = CustomException.class)
    public void testUpdateAccountBalanceNotEnoughFund() throws CustomException {
        BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
        assertEquals(0, rowsUpdatedW);
    }

}