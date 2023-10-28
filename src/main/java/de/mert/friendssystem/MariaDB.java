package de.mert.friendssystem;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * MariaDB
 */
public class MariaDB {
    public DataSource connect() {
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

        return new HikariDataSource(config);
    }
}
