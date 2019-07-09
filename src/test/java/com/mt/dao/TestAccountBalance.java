package com.mt.dao;

import com.mt.exception.CustomException;
import com.mt.model.Account;
import com.mt.model.UserTransaction;

import org.apache.commons.dbutils.DbUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;

public class TestAccountBalance {

    private static Logger log = Logger.getLogger(TestAccountDAO.class.getCanonicalName());
    private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    private static final int THREADS_COUNT = 100;

    @BeforeClass
    public static void setup() {
        // prepare test database and test data, Test data are initialised from
        // src/test/resources/test.sql
        h2DaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAccountSingleThreadSameCcyTransfer() throws CustomException {

        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();

        BigDecimal transferAmount = new BigDecimal(50.01234).setScale(4, RoundingMode.HALF_EVEN);

        UserTransaction transaction = new UserTransaction("EUR", transferAmount, 3L, 4L);

        long startTime = System.currentTimeMillis();

        accountDAO.transferAccountBalance(transaction);
        long endTime = System.currentTimeMillis();

        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        Account accountFrom = accountDAO.getAccountById(3);

        Account accountTo = accountDAO.getAccountById(4);

        log.finest("Account From: " + accountFrom);

        log.finest("Account From: " + accountTo);

        assertEquals(0, accountFrom.getBalance().compareTo(new BigDecimal(449.9877).setScale(4, RoundingMode.HALF_EVEN)));
        assertEquals(new BigDecimal(550.0123).setScale(4, RoundingMode.HALF_EVEN), accountTo.getBalance());
    }

    @Test
    public void testAccountMultiThreadedTransfer() throws InterruptedException, CustomException {
        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();
        // transfer a total of 200USD from 100USD balance in multi-threaded
        // mode, expect half of the transaction fail
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(() -> {
                try {
                    UserTransaction transaction = new UserTransaction("USD",
                            new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
                    accountDAO.transferAccountBalance(transaction);
                } catch (Exception e) {
                    log.severe("Error occurred during transfer " + e.getLocalizedMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        Account accountFrom = accountDAO.getAccountById(1);

        Account accountTo = accountDAO.getAccountById(2);

        log.finest("Account From: " + accountFrom);

        log.finest("Account From: " + accountTo);

        assertEquals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN), accountFrom.getBalance());
        assertEquals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN), accountTo.getBalance());
    }

    @Test
    public void testTransferFailOnDBLock() throws CustomException {
        final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE AccountId = 5 FOR UPDATE";
        Connection conn = null;
        PreparedStatement lockStmt = null;
        ResultSet rs = null;
        Account fromAccount = null;

        try {
            conn = H2DAOFactory.getConnection();
            conn.setAutoCommit(false);
            // lock account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
                        rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isLoggable(Level.FINEST))
                    log.finest("Locked Account: " + fromAccount);
            }

            if (fromAccount == null) {
                throw new CustomException("Locking error during test, SQL = " + SQL_LOCK_ACC);
            }
            // after lock account 5, try to transfer from account 6 to 5
            // default h2 timeout for acquire lock is 1sec
            BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);

            UserTransaction transaction = new UserTransaction("GBP", transferAmount, 6L, 5L);
            h2DaoFactory.getAccountDAO().transferAccountBalance(transaction);
            conn.commit();
        } catch (Exception e) {
            log.severe("Exception occurred, initiate a rollback");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                log.severe("Fail to rollback transaction" + re.getLocalizedMessage());
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
        }

        // now inspect account 3 and 4 to verify no transaction occurred
        BigDecimal originalBalance = new BigDecimal(500).setScale(4, RoundingMode.HALF_EVEN);
        assertEquals(originalBalance, h2DaoFactory.getAccountDAO().getAccountById(6).getBalance());
        assertEquals(originalBalance, h2DaoFactory.getAccountDAO().getAccountById(5).getBalance());
    }

}
