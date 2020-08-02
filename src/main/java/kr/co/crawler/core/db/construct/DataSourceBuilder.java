package kr.co.crawler.core.db.construct;

import kr.co.crawler.core.db.properties.DataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * DataSourceBuilder
 */
@Slf4j
public class DataSourceBuilder {
    /**
     * BasicDataSource build
     * @param properties DataSourceProperties
     * @return BasicDataSource
     */
    public static BasicDataSource build(DataSourceProperties properties) {
        log.info("configureDataSource : <{}>", properties.toString());

        if (StringUtils.isBlank(properties.getDriverClassName()) || StringUtils.isBlank(properties.getUrl())) {
            throw new RuntimeException("Database properties read error.");
        }

        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(properties.getDriverClassName());
            dataSource.setUrl(properties.getUrl());
            dataSource.setUsername(properties.getUsername());
            dataSource.setPassword(properties.getPassword());
            dataSource.setInitialSize(properties.getInitialSize());
            dataSource.setMaxTotal(properties.getMaxTotal());
            dataSource.setMaxIdle(properties.getMaxIdle());
            dataSource.setMinIdle(properties.getMinIdle());
            dataSource.setMaxWaitMillis(properties.getMaxWaitMillis());
            dataSource.setValidationQuery(properties.getValidationQuery());
            dataSource.setTestOnBorrow(properties.isTestOnBorrow());
            dataSource.setTestOnReturn(false);
            dataSource.setValidationQueryTimeout(properties.getValidationQueryTimeout());
            dataSource.setDefaultAutoCommit(properties.isDefaultAutoCommit());
            dataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
            dataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
            dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("DataSource build error : " + e.getMessage());
        }
    }
}
