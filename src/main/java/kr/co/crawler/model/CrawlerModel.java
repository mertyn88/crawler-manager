package kr.co.crawler.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlerModel {

    private String crawlerDocId;   //문서 번호
    private String crawlerBaseUrl; //기본 주소
    private String crawlerTargetUrl;   //대상 주소
    private String crawlerKeyword;
    private String crawlerTitle;
    private String crawlerImagePath;
    private String crawlerContent; //문서 내용
    private String crawlerDate;     //문서 날짜

}
