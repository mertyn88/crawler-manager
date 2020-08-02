package kr.co.crawler.core.db.construct;

import kr.co.crawler.core.db.TransactionManagerName;
import kr.co.crawler.core.db.annotation.MasterConnection;
import kr.co.crawler.core.db.properties.DataSourceProperties;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

class MyBatisConfig {
    public static final String BASE_PACKAGE = "kr.co.crawler";
}

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = MyBatisConfig.BASE_PACKAGE, annotationClass = MasterConnection.class, sqlSessionFactoryRef = "masterSqlSessionFactory")
class MasterMyBatisConfig {
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") BasicDataSource masterDataSource) throws Exception {
        return SqlSessionFactoryBuilder.build(masterDataSource);
    }

    @Primary
    @Bean(name = "masterDataSource", destroyMethod = "")
    public BasicDataSource dtaSource(@Qualifier("masterDatabaseProperties") DataSourceProperties dataSourceProperties) {
        dataSourceProperties.setDefaultReadOnly(false);
        return DataSourceBuilder.build(dataSourceProperties);
    }

    @Bean(name = TransactionManagerName.MASTER)
    public PlatformTransactionManager transactionManager(@Qualifier("masterDataSource") BasicDataSource masterDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(masterDataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.master")
    public DataSourceProperties masterDatabaseProperties() {
        return new DataSourceProperties();
    }
}
