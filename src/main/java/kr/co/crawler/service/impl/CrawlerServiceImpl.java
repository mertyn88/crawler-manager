package kr.co.crawler.service.impl;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import kr.co.crawler.core.properties.CrawlerProperties;
import kr.co.crawler.core.properties.SeedProperties;
import kr.co.crawler.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final CrawlerProperties crawlerProperties;
    private final InsertService insertService;

    CrawlConfig crawlConfig;
    CustomCrawlerController crawlController;

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
        //crawlConfig.setMaxPagesToFetch(300);       //Unlimited Pages
       // crawlConfig.setResumableCrawling(true);

       // crawlConfig.setResumableCrawling(false);
       // crawlConfig.setIncludeBinaryContentInCrawling(false);
       // crawlConfig.setShutdownOnEmptyQueue(false);

        //https://avmix.co.kr/news/?page=2



        // CrawController 준비하기
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
       // pageFetcher.
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
       // robotstxtConfig.setEnabled(false);
        robotstxtConfig.setEnabled(false);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        crawlController = new CustomCrawlerController(crawlConfig, pageFetcher, robotstxtServer);

        /**
         * 팩토리 처리
         */
       // CrawlController.WebCrawlerFactory<MyCrawler> factory = new MyCrawler(crawlerProperties,insertService);

        // 크롤링 시작 URL 지정하기
       /* for(String site : crawlerProperties.getSiteList()){
          crawlController.addSeed(site);
        }*/

       int idx = 1;
       for(SeedProperties seedProperties : crawlerProperties.getSeedInfo()){

           String String = MyCrawler.runSelenium(seedProperties.getVisitUrl());
           System.out.println(String.toString());


           //저 밑에 들어가는 시드가 내가 원하느넉여야함
           String url = "http://localhost:8080/test/test2";

          /* Map<String, String> params = new HashMap<String, String>();
           params.put("htmlData", String);

           RestTemplate restTemplate = new RestTemplate();
           ResponseEntity<String> response = restTemplate.postForEntity( url, String, String.class );

           System.out.println(response.getStatusCode());*/
           RestTemplate restTemplate = new RestTemplate();
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
           MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
           map.add("htmlData", String);
           HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
           ResponseEntity<String> response2 = restTemplate.postForEntity( url, request , String.class );

           //System.exit(1);




           /*String visitUrl;
           do{
               visitUrl = seedProperties.getVisitUrl().replace("#page#", Integer.toString(idx));
               //존재하는지 체크
               crawlController.addSeed(visitUrl);
               idx++;
               //openConnection(visitUrl);
           }while (openConnection(visitUrl));*/

           //crawlController.addSeed(seedProperties.getVisitUrl());
           crawlController.addSeed("http://localhost:8080/test/test");

       }

        MyCrawlerFactory factory = new MyCrawlerFactory(crawlerProperties, insertService);

        // 크롤링 시작하기
        //crawlController.star
        crawlController.start(factory, crawlerProperties.getThreadNum());


    }

    @Override
    public void startCrawler() {

    }


    public static boolean doesURLExist(URL url) throws IOException, ProtocolException {
        // We want to check the current URL
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // We don't need to get data
        httpURLConnection.setRequestMethod("HEAD");

        // Some websites don't like programmatic access so pretend to be a browser
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = httpURLConnection.getResponseCode();

        // We only accept response code 200, 302
        switch (responseCode){
            case HttpURLConnection.HTTP_OK:
                log.debug(url.getHost() + " HttpURLConnection.HTTP_OK :::: " + HttpURLConnection.HTTP_OK);
                return true;
            case HttpURLConnection.HTTP_MOVED_TEMP:
                log.debug(url.getHost() + " HttpURLConnection.HTTP_MOVED_TEMP :::: " + HttpURLConnection.HTTP_MOVED_TEMP);
                return true;
            default:
                return false;
        }
    }


    private static boolean openConnection(String url) throws IOException {
        HttpURLConnection connection;
        int code;

        connection = (HttpURLConnection) new URL(url).openConnection();
        code = connection.getResponseCode();
        // redirected = (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER);

        log.debug(url);

        return code == HttpURLConnection.HTTP_OK;

       /* do {
            connection = (HttpURLConnection) new URL(url).openConnection();
            code = connection.getResponseCode();
            redirected = (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER);
            if (redirected) {
                url = connection.getHeaderField("Location");
                connection.disconnect();
            }
        } while (redirected);

        switch (code){
            case HttpURLConnection.HTTP_OK:
                log.debug(url + " HttpURLConnection.HTTP_OK :::: " + HttpURLConnection.HTTP_OK);
                return true;
            case HttpURLConnection.HTTP_MOVED_TEMP:
                log.debug(url + " HttpURLConnection.HTTP_MOVED_TEMP :::: " + HttpURLConnection.HTTP_MOVED_TEMP);
                return true;
            default:
                return false;
        }
    }*/
    }
}
