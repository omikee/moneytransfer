package com.mt.dao.impl;

import com.mt.dao.H2DAOFactory;
import com.mt.dao.UserDAO;
import com.mt.exception.CustomException;
import com.mt.model.User;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {

    private static Logger log = Logger.getLogger(UserDAOImpl.class.getCanonicalName());
    private final static String SQL_GET_USER_BY_ID = "SELECT * FROM User WHERE UserId = ? ";
    private final static String SQL_GET_ALL_USERS = "SELECT * FROM User";
    private final static String SQL_GET_USER_BY_NAME = "SELECT * FROM User WHERE UserName = ? ";
    private final static String SQL_INSERT_USER = "INSERT INTO User (UserName, EmailAddress) VALUES (?, ?)";
    private final static String SQL_UPDATE_USER = "UPDATE User SET UserName = ?, EmailAddress = ? WHERE UserId = ? ";
    private final static String SQL_DELETE_USER_BY_ID = "DELETE FROM User WHERE UserId = ? ";

    public List<User> getAllUsers() throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<User>();
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_USERS);
            rs = stmt.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                users.add(u);
                if (log.isLoggable(Level.FINEST))
                    log.finest("getAllUsers() Retrieve User: " + u);
            }
            return users;
        } catch (SQLException e) {
            throw new CustomException("Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public User getUserById(long userId) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User u = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_USER_BY_ID);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                if (log.isLoggable(Level.FINEST))
                    log.finest("getUserById(): Retrieve User: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new CustomException("Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public User getUserByName(String userName) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User u = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_USER_BY_NAME);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                if (log.isLoggable(Level.FINEST))
                    log.finest("Retrieve User: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new CustomException("Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public long insertUser(User user) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmailAddress());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.severe("insertUser(): Creating user failed, no rows affected." + user);
                throw new CustomException("Users Cannot be created");
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                log.severe("insertUser():  Creating user failed, no ID obtained." + user);
                throw new CustomException("Users Cannot be created");
            }
        } catch (SQLException e) {
            log.severe("Error Inserting User :" + user);
            throw new CustomException("Error creating user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, generatedKeys);
        }

    }

    public int updateUser(Long userId, User user) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_USER);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmailAddress());
            stmt.setLong(3, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.severe("Error Updating User :" + user);
            throw new CustomException("Error update user data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    public int deleteUser(long userId) throws CustomException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_USER_BY_ID);
            stmt.setLong(1, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.severe("Error Deleting User :" + userId);
            throw new CustomException("Error Deleting User ID:" + userId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

}
