package kr.co.crawler.service;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import kr.co.crawler.core.properties.CrawlerProperties;

public class MyCrawlerFactory implements CrawlController.WebCrawlerFactory {

    InsertService insertService;
    CrawlerProperties crawlerProperties;

    public MyCrawlerFactory(CrawlerProperties crawlerProperties, InsertService insertService) {
        this.crawlerProperties = crawlerProperties;
        this.insertService = insertService;
    }

    @Override
    public WebCrawler newInstance() throws Exception {
        return new MyCrawler(crawlerProperties, insertService);
    }
}
