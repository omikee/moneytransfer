package com.mt.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.mt.model.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAccountService extends TestService {

    @Test
    public void testGetAccountById() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        assertEquals(200, statusCode);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertEquals("user1", account.getUserName());
    }

    @Test
    public void testGetAllAccounts() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(jsonString, Account[].class);
        assertTrue(accounts.length > 0);
    }

    @Test
    public void testGetAccountBalance() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        //check the content, assert user test2 have balance 100
        String balance = EntityUtils.toString(response.getEntity());
        BigDecimal res = new BigDecimal(balance).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal db = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        assertEquals(db, res);
    }

    @Test
    public void testCreateAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        Account acc = new Account("test2", balance, "CNY");
        String jsonInString = mapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account aAfterCreation = mapper.readValue(jsonString, Account.class);
        assertEquals("test2", aAfterCreation.getUserName());
        assertEquals("CNY", aAfterCreation.getCurrencyCode());
    }

    @Test
    public void testCreateExistingAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        Account acc = new Account("user1", new BigDecimal(0), "USD");
        String jsonInString = mapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(406, statusCode);
    }

    @Test
    public void testDeleteAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testDeleteNonExistingAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

}
