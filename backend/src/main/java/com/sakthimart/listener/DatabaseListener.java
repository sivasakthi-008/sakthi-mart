package com.sakthimart.listener;

import com.sakthimart.config.DatabaseConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DatabaseListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            DatabaseConfig.getDataSource().getConnection().close();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to initialize database connection pool", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DatabaseConfig.close();
    }
}