package com.mt;

import com.mt.datastore.BankAccounts;
import com.mt.exception.ExceptionHandler;
import com.mt.model.Account;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.math.BigDecimal;

public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        mockBankAccounts();
        startServer();
    }

    private static void mockBankAccounts() {
        BankAccounts.getInstance().addAccount();
        BankAccounts.getInstance().updateAccount(new Account(new BigDecimal("1523.51")));
        BankAccounts.getInstance().updateAccount(new Account(new BigDecimal("3259.64")));
        BankAccounts.getInstance().updateAccount(new Account(new BigDecimal("412.12")));
    }

    private static void startServer() {
        final Server localServer = new Server(PORT);
        final ServletContextHandler context = new ServletContextHandler(localServer, "/*");
        context.addServlet(new ServletHolder(new ServletContainer(new MoneyTransferServerResources())), "/*");
        try {
            localServer.start();
            localServer.join();
        } catch (Exception e) {
            // TODO - in the future replace with actual logging (use log4j)
            e.printStackTrace();
        } finally {
            localServer.destroy();
        }
    }

    public static final class MoneyTransferServerResources extends ResourceConfig {

        public MoneyTransferServerResources() {
            super();
            packages("com.mt");
            register(JacksonFeature.class);
            register(new ExceptionHandler());
        }
    }

}
