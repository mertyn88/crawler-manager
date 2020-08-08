package kr.co.crawler.service.impl;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import kr.co.crawler.core.properties.CrawlerProperties;
import kr.co.crawler.crawler4j.CrawlerFactory;
import kr.co.crawler.crawler4j.custom.CustomCrawlerController;
import kr.co.crawler.selenium.Selenium;
import kr.co.crawler.service.CrawlerService;
import kr.co.crawler.service.InsertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final CrawlerProperties crawlerProperties;
    private final InsertService insertService;

    private CrawlConfig crawlConfig;
    private CustomCrawlerController crawlController;
    private String viewUrl;
    private String saveUrl;

    @Override
    public void run() throws Exception {

        for(String keyword : crawlerProperties.getSeedInfo().getSearchKeywordList()){
            if(getUrlData(keyword)){
                log.info("Save Url data! wait 5 seconds & crawling start [" + keyword + "]");
                Thread.sleep(5000);
                settingCrawler();
                startCrawler(keyword);
            }else{
                log.error("Process fail [" + keyword + "]");
            }
        }
    }

    /**
     * 셀레니움으로 컨트롤하여 HTML을 저장, api-manager에 전송
     * @return
     */
    public boolean getUrlData(String keyword) throws InterruptedException {
        Selenium selenium = new Selenium(
                crawlerProperties.getSeedInfo().getSearchUrl().replace("#keyword#", keyword)
                , crawlerProperties.getSeedInfo().getSearchMoreMax()
                , crawlerProperties.getHtmlPath()
                , keyword
        );
        String crawlData = selenium.run();
        if(crawlData == null){
            return false;
        }
        log.debug("{}", crawlData);
        saveUrl = crawlerProperties.getSeedInfo().getTemporarySaveUrl();
        log.info("Temporary save page url : {}", saveUrl);
        if(saveUrl == null){
            log.error("Temporary url is null");
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("htmlData", crawlData);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity( saveUrl, request , String.class );

        return response.getStatusCode() == HttpStatus.OK ? true : false;
    }

    @Override
    public void settingCrawler() throws Exception {

        /**
         * Crawler4j config 설정
         */
        crawlConfig = new CrawlConfig();
        crawlConfig.setMaxDepthOfCrawling(crawlerProperties.getMaxDepth());			// 시작 URL에서 몇 단계까지 탐색할지 설정
        crawlConfig.setPolitenessDelay(crawlerProperties.getMaxDelay());				// 동일 호스트에 대한 요청 delay 설정 (ms)
        crawlConfig.setCrawlStorageFolder(crawlerProperties.getHistoryPath());	// 크롤러의 데이터 저장 디렉터리 지정

        /**
         * Crawler4j robot 설정
         */
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        viewUrl = crawlerProperties.getSeedInfo().getTemporaryViewUrl();
        log.info("Temporary save page url : {}", viewUrl);
        if(viewUrl == null){
            log.error("Temporary url is null");
            return;
        }

        /**
         * Crawler4j 대상 url 추가
         */
        crawlController = new CustomCrawlerController(crawlConfig, pageFetcher, robotstxtServer);
        crawlController.addSeed(viewUrl);
    }

    @Override
    public void startCrawler(String keyword) {
        /**
         * 팩토리 처리
         */
        CrawlerFactory factory = new CrawlerFactory(keyword, crawlerProperties, insertService);

        /**
         * 크롤링 시작
         */
        crawlController.start(factory, crawlerProperties.getThreadNum());
    }


}
