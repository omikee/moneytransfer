package com.mt.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mt.dao.DAOFactory;
import com.mt.exception.CustomException;
import com.mt.model.MoneyUtil;
import com.mt.model.UserTransaction;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    @POST
    public Response transferFund(UserTransaction transaction) throws CustomException {

        String currency = transaction.getCurrencyCode();
        if (MoneyUtil.validateCcyCode(currency)) {
            int updateCount = daoFactory.getAccountDAO().transferAccountBalance(transaction);
            if (updateCount == 2) {
                return Response.status(Response.Status.OK).build();
            } else {
                // transaction failed
                throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
            }

        } else {
            throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
        }

    }

}
