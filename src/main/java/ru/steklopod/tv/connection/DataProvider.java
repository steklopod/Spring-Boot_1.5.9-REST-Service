package ru.steklopod.tv.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "ru.steklopod.tv")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class DataProvider {

    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${pool.size}")
    private int poolsize;
    @Value("${pool.connection.timeout}")
    private int connectionTimeOut;
    @Value("${pool.idle.timeout}")
    private int idleTimeOut;
    @Value("${pool.max.lifetime}")
    private int lifetime;

    @Bean(name = "AppDC")
    @Primary
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(poolsize);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaxLifetime(lifetime);
        hikariConfig.setConnectionTimeout(connectionTimeOut);
        hikariConfig.setIdleTimeout(idleTimeOut);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setLeakDetectionThreshold(15000);
        hikariConfig.setPoolName("Hikari-2");


        /**
         * Это свойство определяет, будет ли HikariCP изолировать внутренние запросы пула, такие как тест живой связи,
         * в своей собственной транзакции. Поскольку они обычно являются запросами только для чтения,
         * редко бывает необходимо инкапсулировать их в свою собственную транзакцию.
         * Это свойство применяется только в том случае, если autoCommit отключен. По умолчанию: false
         */
        hikariConfig.setIsolateInternalQueries(true);

        /**
         * Это свойство определяет, можно ли приостановить и возобновить пул через JMX.
         * Это полезно для некоторых сценариев автоматизации отказоустойчивости.
         * Когда пул приостановлен, вызовы getConnection () не будут таймаутом и будут удерживаться до тех пор,
         * пока пул не будет возобновлен. По умолчанию: false
         */
//        hikariConfig.setAllowPoolSuspension(true);

        /**
         Этот параметр задает количество подготовленных операторов, которые драйвер Posrgres будет кэшировать для каждого соединения.
         Значение по умолчанию - консервативное = 25. Рекомендуется установить это значение между 250-500.
         */
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", 250);

        /**
         Это максимальная длина подготовленного оператора SQL, который будет кэшировать драйвер.
         */
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", 2048);

        /**
         Ни один из вышеперечисленных параметров не имеет никакого эффекта, если кэш фактически отключен.
         */
        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", true);

        /**
         Более новые версии Postgres поддерживают подготовленные операторы на стороне сервера, это может обеспечить
         существенное повышение производительности. Установите для этого свойства значение true.
         */
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", true);

        HikariDataSource ds = new HikariDataSource(hikariConfig);
        return ds;
    }

    @Bean(name = "AppTransactionManager")
    @Primary
    public DataSourceTransactionManager txManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        jdbcTemplate.setResultsMapCaseInsensitive(true);
//        return jdbcTemplate;
//    }


}