package com.mt.dao;

import com.mt.exception.CustomException;
import com.mt.model.User;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;

public class TestUserDAO {

    private static Logger log = Logger.getLogger(TestUserDAO.class.getCanonicalName());

    private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    @BeforeClass
    public static void setup() {
        // prepare test database and test data by executing sql script test.sql
        log.finest("setting up test database and sample data....");
        h2DaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllUsers() throws CustomException {
        List<User> allUsers = h2DaoFactory.getUserDAO().getAllUsers();
        assertTrue(allUsers.size() > 1);
    }

    @Test
    public void testGetUserById() throws CustomException {
        User u = h2DaoFactory.getUserDAO().getUserById(2L);
        assertEquals("user2", u.getUserName());
    }

    @Test
    public void testGetNonExistingUserById() throws CustomException {
        User u = h2DaoFactory.getUserDAO().getUserById(500L);
        assertNull(u);
    }

    @Test
    public void testGetNonExistingUserByName() throws CustomException {
        User u = h2DaoFactory.getUserDAO().getUserByName("noname");
        assertNull(u);
    }

    @Test
    public void testCreateUser() throws CustomException {
        User u = new User("user", "user@mail.ru");
        long id = h2DaoFactory.getUserDAO().insertUser(u);
        User uAfterInsert = h2DaoFactory.getUserDAO().getUserById(id);
        assertEquals("user", uAfterInsert.getUserName());
        assertEquals("user@mail.ru", u.getEmailAddress());
    }

    @Test
    public void testUpdateUser() throws CustomException {
        User u = new User(1L, "user2updated", "user2updated@mail.ru");
        int rowCount = h2DaoFactory.getUserDAO().updateUser(1L, u);
        // assert one row(user) updated
        assertEquals(1, rowCount);
        assertEquals("user2updated@mail.ru", h2DaoFactory.getUserDAO().getUserById(1L).getEmailAddress());
    }

    @Test
    public void testUpdateNonExistingUser() throws CustomException {
        User u = new User(500L, "user2updated", "user2updated@mail.ru");
        int rowCount = h2DaoFactory.getUserDAO().updateUser(500L, u);
        // assert one row(user) updated
        assertEquals(0, rowCount);
    }

    @Test
    public void testDeleteUser() throws CustomException {
        int rowCount = h2DaoFactory.getUserDAO().deleteUser(1L);
        // assert one row(user) deleted
        assertEquals(1, rowCount);
        // assert user no longer there
        assertNull(h2DaoFactory.getUserDAO().getUserById(1L));
    }

    @Test
    public void testDeleteNonExistingUser() throws CustomException {
        int rowCount = h2DaoFactory.getUserDAO().deleteUser(500L);
        // assert no row(user) deleted
        assertEquals(0, rowCount);
    }

}
