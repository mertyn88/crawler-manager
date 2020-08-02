package kr.co.crawler.service;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import kr.co.crawler.core.properties.CrawlerProperties;
import kr.co.crawler.model.CrawlerModel;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

@Slf4j
public class MyCrawler extends WebCrawler {

    private static File storageFolder;

    private CrawlerProperties crawlerProperties;
    private InsertService insertService;

    public MyCrawler(CrawlerProperties crawlerProperties, InsertService insertService){
        this.crawlerProperties = crawlerProperties;
        this.insertService = insertService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return url.getURL().matches(crawlerProperties.getVisitPattern());
    }

    public boolean shouldVisit(String url) {
        return url.matches(crawlerProperties.getVisitPattern());
    }

    @Override
    public void visit(Page page) {
        // 방문한 페이지의 내용을 처리
        if(!shouldVisit(page.getWebURL().getURL())){
            return ;
        }

        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        log.debug("Docid: {}", docid);
        log.info("URL: {}", url);
        log.debug("Domain: '{}'", domain);
        log.debug("Sub-domain: '{}'", subDomain);
        log.debug("Path: '{}'", path);
        log.debug("Parent page: {}", parentUrl);
        log.debug("Anchor text: {}", anchor);

        if (page.getParseData() instanceof HtmlParseData) {

            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            log.debug("Text length: {}", text.length());
            log.debug("Html length: {}", html.length());
            log.debug("Number of outgoing links: {}", links.size());

            // 페이지의 HTML을 파싱하기
            Document doc = Jsoup.parse(html);
            //doc.select("meta[property=og:og:description]").get(0).attr("content")
            String content = doc.select("meta[property=og:description]").get(0).attr("content");

            insertService.insertData(
                    new CrawlerModel(){{
                        setCrawlingDocId(page.getWebURL().getDocid()+"");
                        setCrawlingBaseUrl(page.getWebURL().getDomain());
                        setCrawlingTargetUrl(page.getWebURL().getURL());
                        setCrawlingContent(content);
                    }}
            );
        }
    }

    private void storeImageToLocalFile(String url, byte[] imageBytes) {
        String nameWithExtension = url.substring(url.lastIndexOf('/') + 1);
        String filename = storageFolder.getAbsolutePath() + "/" + nameWithExtension;
        try {
            Files.write(Paths.get(filename) , imageBytes);
            logger.info("Stored in file: {}", url);
        } catch (IOException iox) {
            logger.error("Failed to write file: " + filename, iox);
        }
    }
}
