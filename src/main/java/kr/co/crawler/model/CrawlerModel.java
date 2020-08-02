package kr.co.crawler.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlerModel {

    private String crawlingDocId;   //문서 번호
    private String crawlingBaseUrl; //기본 주소
    private String crawlingTargetUrl;   //대상 주소
    private String crawlingContent; //문서 내용

}
