package com.mt.service;

import com.mt.dao.DAOFactory;
import com.mt.exception.CustomException;
import com.mt.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    private static Logger log = Logger.getLogger(UserService.class.getCanonicalName());

    @GET
    @Path("/{userName}")
    public User getUserByName(@PathParam("userName") String userName) throws CustomException {
        if (log.isLoggable(Level.FINEST))
            log.finest("Request Received for get User by Name " + userName);
        final User user = daoFactory.getUserDAO().getUserByName(userName);
        if (user == null) {
            throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
        }
        return user;
    }

    @GET
    @Path("/all")
    public List<User> getAllUsers() throws CustomException {
        return daoFactory.getUserDAO().getAllUsers();
    }

    @POST
    @Path("/create")
    public User createUser(User user) throws CustomException {
        if (daoFactory.getUserDAO().getUserByName(user.getUserName()) != null) {
            throw new WebApplicationException("User name already exist", Response.Status.BAD_REQUEST);
        }
        final long uId = daoFactory.getUserDAO().insertUser(user);
        return daoFactory.getUserDAO().getUserById(uId);
    }

    @PUT
    @Path("/{userId}")
    public Response updateUser(@PathParam("userId") long userId, User user) throws CustomException {
        final int updateCount = daoFactory.getUserDAO().updateUser(userId, user);
        if (updateCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") long userId) throws CustomException {
        int deleteCount = daoFactory.getUserDAO().deleteUser(userId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
