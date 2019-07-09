package com.mt;

import com.mt.dao.DAOFactory;
import com.mt.service.AccountService;
import com.mt.service.ServiceExceptionMapper;
import com.mt.service.TransactionService;
import com.mt.service.UserService;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.Logger;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getCanonicalName());

    public static void main(String[] args) throws Exception {
        log.info("main");

        Options options = new Options();
        options.addOption("p", true, "server port");

        CommandLineParser parser = new DefaultParser();
        int port = 8080;
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("p")) {
                port = Integer.parseInt(cmd.getOptionValue("p"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initDb();
        startServer(port);
    }

    private static void initDb() {
        log.info("Init DB");
        DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
        h2DaoFactory.populateTestData();
    }

    private static void startServer(int port) throws Exception {
        log.info("Start Server");
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                String.join(",", UserService.class.getCanonicalName(),
                        AccountService.class.getCanonicalName(),
                        ServiceExceptionMapper.class.getCanonicalName(),
                        TransactionService.class.getCanonicalName()));
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

}
