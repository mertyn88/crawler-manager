package kr.co.crawler.service;

public interface CrawlerService {
    void run() throws Exception;
    void settingCrawler() throws Exception;
    void startCrawler(String keyword);
}
