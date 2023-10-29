package de.mert.friendssystem;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * MariaDB
 */
public class MariaDB {
    public static DataSource connect() throws SQLException, IOException {
        Settings settings = FriendsSystem.settings;

        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.mariadb.jdbc.MariaDbDataSource");
        props.setProperty("dataSource.serverName", settings.getMariaDBAddress());
        props.setProperty("dataSource.portNumber", settings.getMariaDBPort());
        props.setProperty("dataSource.user", settings.getMariaDBUsername());
        props.setProperty("dataSource.password", settings.getMariaDBPassword());
        props.setProperty("dataSource.databaseName", settings.getMariaDBDatabase());

        HikariConfig config = new HikariConfig(props);

        config.setMaximumPoolSize(settings.getMariaDBMaxConnections());

        DataSource source = new HikariDataSource(config);

        try (Connection connection = source.getConnection()) {
            if (!connection.isValid(1)) {
                throw new SQLException("Could not establish database connection");
            }
        }

        initDb(source);

        return source;
    }

    private static void initDb(DataSource source) throws SQLException, IOException {
        String setup;
        try (InputStream in = MariaDB.class.getClassLoader().getResourceAsStream("dbsetup.sql")) {
            assert in != null;
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        }

        String[] queries = setup.split(";");

        for (String query : queries) {
            if (query.trim().isEmpty())
                continue;

            try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            }
        }
    }

    public static void logSQLError(String message, Exception error) {
        FriendsSystem plugin = FriendsSystem.getPlugin(FriendsSystem.class);

        plugin.getServer().getConsoleSender().sendMessage(FriendsSystem.PREFIX + message + ": " + error);
    }
}
