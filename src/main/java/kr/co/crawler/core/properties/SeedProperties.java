package kr.co.crawler.core.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeedProperties {
    String visitUrl;
    String visitPattern;
    List<String> crawlerPattern;
}
