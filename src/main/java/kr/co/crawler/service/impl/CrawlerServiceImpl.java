package kr.co.crawler.service.impl;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import kr.co.crawler.core.properties.CrawlerProperties;
import kr.co.crawler.service.CrawlerService;
import kr.co.crawler.service.InsertService;
import kr.co.crawler.service.MyCrawler;
import kr.co.crawler.service.MyCrawlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final CrawlerProperties crawlerProperties;
    private final InsertService insertService;

    CrawlConfig crawlConfig;
    CrawlController crawlController;

    @Override
    public void run() throws Exception {
        settingCrawler();
        startCrawler();
    }

    @Override
    public void settingCrawler() throws Exception {

        crawlConfig = new CrawlConfig();
        crawlConfig.setMaxDepthOfCrawling(crawlerProperties.getMaxDepth());			// 시작 URL에서 몇 단계까지 탐색할지 설정
        crawlConfig.setPolitenessDelay(crawlerProperties.getMaxDelay());				// 동일 호스트에 대한 요청 delay 설정 (ms)
        crawlConfig.setCrawlStorageFolder(crawlerProperties.getHistoryPath());	// 크롤러의 데이터 저장 디렉터리 지정

        // CrawController 준비하기
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        crawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);

        /**
         * 팩토리 처리
         */
       // CrawlController.WebCrawlerFactory<MyCrawler> factory = new MyCrawler(crawlerProperties,insertService);

        // 크롤링 시작 URL 지정하기
        for(String site : crawlerProperties.getSiteList()){
            crawlController.addSeed(site);
        }

        MyCrawlerFactory factory = new MyCrawlerFactory(crawlerProperties, insertService);

        // 크롤링 시작하기
        //crawlController.star
        crawlController.start(factory, crawlerProperties.getThreadNum());
    }

    @Override
    public void startCrawler() {

    }
}
