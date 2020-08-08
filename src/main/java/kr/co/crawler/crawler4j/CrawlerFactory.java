package kr.co.crawler.crawler4j;

import kr.co.crawler.core.properties.CrawlerProperties;
import kr.co.crawler.crawler4j.custom.CustomCrawler;
import kr.co.crawler.crawler4j.custom.CustomCrawlerController;
import kr.co.crawler.service.InsertService;

public class CrawlerFactory implements CustomCrawlerController.CustomCrawlerFactory {

    InsertService insertService;
    CrawlerProperties crawlerProperties;
    String keyword;

    public CrawlerFactory(String keyword, CrawlerProperties crawlerProperties, InsertService insertService) {
        this.keyword = keyword;
        this.crawlerProperties = crawlerProperties;
        this.insertService = insertService;
    }

    @Override
    public CustomCrawler newInstance() throws Exception {
        return new Crawler(keyword, crawlerProperties, insertService);
    }
}
