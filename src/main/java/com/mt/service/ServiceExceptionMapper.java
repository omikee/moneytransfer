package com.mt.service;

import com.mt.exception.ErrorResponse;
import com.mt.exception.CustomException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<CustomException> {
    private static Logger log = Logger.getLogger(ServiceExceptionMapper.class.getCanonicalName());

    public ServiceExceptionMapper() {
    }

    public Response toResponse(CustomException daoException) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Mapping exception to Response....");
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(daoException.getMessage());

        // return internal server error for DAO exceptions
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }

}
