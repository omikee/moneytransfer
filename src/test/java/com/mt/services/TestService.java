package com.mt.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.dao.DAOFactory;
import com.mt.service.AccountService;
import com.mt.service.ServiceExceptionMapper;
import com.mt.service.TransactionService;
import com.mt.service.UserService;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class TestService {

    protected static Server server = null;
    protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    protected static HttpClient client;
    protected static DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    protected ObjectMapper mapper = new ObjectMapper();
    protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8084");

    @BeforeClass
    public static void setup() throws Exception {
        h2DaoFactory.populateTestData();
        startServer();
        connManager.setDefaultMaxPerRoute(100);
        connManager.setMaxTotal(200);
        client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();
    }

    @AfterClass
    public static void closeClient() throws Exception {
        //server.stop();
        HttpClientUtils.closeQuietly(client);
    }

    private static void startServer() throws Exception {
        if (server == null) {
            server = new Server(8084);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                    String.join(",", UserService.class.getCanonicalName(),
                            AccountService.class.getCanonicalName(),
                            ServiceExceptionMapper.class.getCanonicalName(),
                            TransactionService.class.getCanonicalName()));
            server.start();
        }
    }

}
