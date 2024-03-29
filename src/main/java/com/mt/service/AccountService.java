package com.mt.service;

import com.mt.dao.DAOFactory;
import com.mt.exception.CustomException;
import com.mt.model.Account;
import com.mt.model.MoneyUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {

    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    private static Logger log = Logger.getLogger(AccountService.class.getCanonicalName());

    @GET
    @Path("/all")
    public List<Account> getAllAccounts() throws CustomException {
        return daoFactory.getAccountDAO().getAllAccounts();
    }

    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) throws CustomException {
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }

    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) throws CustomException {
        final Account account = daoFactory.getAccountDAO().getAccountById(accountId);

        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getBalance();
    }

    @POST
    @Path("/create")
    public Account createAccount(Account account) throws CustomException {
        final long accountId = daoFactory.getAccountDAO().createAccount(account);
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }

    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Account deposit(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws CustomException {

        if (amount.compareTo(MoneyUtil.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        daoFactory.getAccountDAO().updateAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }

    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Account withdraw(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws CustomException {

        if (amount.compareTo(MoneyUtil.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (log.isLoggable(Level.FINEST))
            log.finest("Withdraw service: delta change to account  " + delta + " Account ID = " + accountId);
        daoFactory.getAccountDAO().updateAccountBalance(accountId, delta.setScale(4, RoundingMode.HALF_EVEN));
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }

    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) throws CustomException {
        int deleteCount = daoFactory.getAccountDAO().deleteAccountById(accountId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
