package kr.co.crawler.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "crawler", ignoreInvalidFields = true)
public class CrawlerProperties {
    private Integer threadNum;
    private Integer maxDelay;
    private Integer maxDepth;
    private String historyPath;
    private String htmlPath;
    private String imagePath;
    private SeedProperties seedInfo;
}
