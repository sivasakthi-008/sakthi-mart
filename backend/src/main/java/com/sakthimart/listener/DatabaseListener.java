package com.sakthimart.listener;

import com.sakthimart.config.DatabaseConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@WebListener
public class DatabaseListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             InputStream inputStream =
                     event.getServletContext()
                          .getResourceAsStream("/WEB-INF/classes/schema.sql")) {

            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found");
            }

            String sql = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            try (Statement statement = connection.createStatement()) {
                for (String command : sql.split(";")) {

    String trimmedCommand = command.trim();

    if (trimmedCommand.startsWith("-- Admin seed")) {
        trimmedCommand = trimmedCommand.substring(
                trimmedCommand.indexOf('\n') + 1
        ).trim();
    }

    if (!trimmedCommand.isEmpty()) {
        statement.execute(trimmedCommand);
    }
}
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to initialize database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DatabaseConfig.close();
    }
}