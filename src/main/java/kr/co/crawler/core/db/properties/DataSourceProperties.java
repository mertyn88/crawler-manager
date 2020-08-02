package kr.co.crawler.core.db.properties;

import lombok.Data;

/**
 * database connection config properties
 */
@Data
public class DataSourceProperties {
    /**
     * JDBC 드라이버 클래스 명
     */
    private String driverClassName;
    /**
     * 데이터베이스 연결 URL
     */
    private String url;
    /**
     * 아이디
     */
    private String username;
    /**
     * 패스워드
     */
    private String password;
    /**
     * 최초 시점에 getConnection() 를 통해 커넥션 풀에 채워 넣을 커넥션 개수 (default = 0)
     */
    private int initialSize;

    /**
     * 최소한으로 유지할 커넥션 개수 (default = 0)
     */
    private int minIdle;
    /**
     * 동시에 사용할 수 있는 최대 커넥션 개수 (default = 8)
     */
    private int maxTotal;
    /**
     * Connection Pool에 반납할 때 최대로 유지될 수 있는 커넥션 개수 (default = 8)
     */
    private int maxIdle;
    /**
     * pool이 고갈되었을 경우 최대 대기 시간 (ms단위, default = -1 = 무한정)
     */
    private int maxWaitMillis;
    /**
     * 풀에 커넥션을 반환하기 전이나, 풀을 획득하기 전에 커넥션이 valid 한지를 검사
     */
    private String validationQuery;
    /**
     * 커넥션 풀에서 커넥션을 얻어올 때 테스트 실행 (default = true)
     */
    private boolean testOnBorrow;
    /**
     * 커넥션 검사 쿼리가 실패하기 전까지의 시간 제한(초(sec) 단위, dbcp 에서는 validationQueryTimeout)
     */
    private int validationQueryTimeout;
    /**
     * true 이면 풀에 의해서 생성된 커넥션은 autocommit 된다. 커넥션이 종료되기 전에 commit 처리된다.(default = 드라이버기본값)
     */
    private boolean defaultAutoCommit;
    /**
     * 풀에 의해서 생성된 커넥션의 read-only 상태를 설정 (default = 드라이버기본값)
     */
    private boolean defaultReadOnly = true;
    /**
     * 풀에 prepared statement 정보를 저장할 것인지를 설정
     */
    private boolean poolPreparedStatements = false;
    /**
     * 풀에 저장할 prepared statement 개수를 설정.
     * poolPreparedStatements=true 인 경우 반드시 이 값이 양수로 설정 되어 있어야 무한대로 저장하는 사태를 막을 수 있다.
     */
    private int maxOpenPreparedStatements = 1000;

//    private boolean autoCommit;
//    private int maximumPoolSize;
//    private int minimumIdle;
//    private int connectionTimeout;
//    private int idleTimeout;
//    private int maxLifetime;
}
