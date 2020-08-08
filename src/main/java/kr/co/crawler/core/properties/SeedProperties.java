package kr.co.crawler.core.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeedProperties {
    private String searchUrl;
    private String searchUrlPattern;
    private String searchDataPattern;
    private Integer searchMoreMax;
    private String temporaryViewUrl;
    private String temporarySaveUrl;
    private List<String> searchKeywordList;

    private String searchDataRemoveFront;
    private String searchDataRemoveBack;
}
