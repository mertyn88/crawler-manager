package kr.co.crawler.service;

import kr.co.crawler.core.properties.CrawlerProperties;

public class MyCrawlerFactory implements CustomCrawlerController.CustomCrawlerFactory {

    InsertService insertService;
    CrawlerProperties crawlerProperties;

    public MyCrawlerFactory(CrawlerProperties crawlerProperties, InsertService insertService) {
        this.crawlerProperties = crawlerProperties;
        this.insertService = insertService;
    }

    @Override
    public CustomCrawler newInstance() throws Exception {
        return new MyCrawler(crawlerProperties, insertService);
    }
}
