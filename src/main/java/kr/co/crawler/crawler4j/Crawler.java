package kr.co.crawler.crawler4j;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import kr.co.crawler.core.properties.CrawlerProperties;
import kr.co.crawler.crawler4j.custom.CustomCrawler;
import kr.co.crawler.model.CrawlerModel;
import kr.co.crawler.service.InsertService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler extends CustomCrawler {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Crawler.class);
    private String title ;
    private String imageUrl;
    private String imagePath;
    private String content;
    private String keyword;

    private CrawlerProperties crawlerProperties;
    private InsertService insertService;

    public Crawler(String keyword, CrawlerProperties crawlerProperties, InsertService insertService){
        this.keyword = keyword;
        this.crawlerProperties = crawlerProperties;
        this.insertService = insertService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return url.getURL().matches(crawlerProperties.getSeedInfo().getSearchUrlPattern());
    }

    public boolean shouldVisit(String url) {
        return url.matches(crawlerProperties.getSeedInfo().getSearchUrlPattern());
    }

    @Override
    public void visit(Page page) {
        // 방문한 페이지의 내용을 처리
        if(!shouldVisit(page.getWebURL().getURL())){
            return ;
        }

        /**
         * 초기화 (content의 경우 초기값 null)
         */
        init();

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
            Element htmlElement = doc.select("#wrap div#cont #__clipContent").get(0);


            /**
             * 타이틀 구하기
             */
            title = doc.select("meta[property=nv:news:title]").get(0).attr("content");
            log.info("Title :: {}", title);
            imageUrl = doc.select("meta[property=og:image]").get(0).attr("content");
            log.info("Image url :: {}", imageUrl);

            /**
             * 이미지 다운로드
             */
            try{
                imagePath = downloadImage(imageUrl);
            }catch (IOException e){
                e.printStackTrace();
            }

            /**
             * 내용 추출
             */
            Pattern pat = Pattern.compile(crawlerProperties.getSeedInfo().getSearchDataPattern());
            Matcher match = pat.matcher(htmlElement.toString());
            while (match.find()) {
                String matchData = match.group()
                        .replaceAll(crawlerProperties.getSeedInfo().getSearchDataRemoveFront(), "")
                        .replaceAll(crawlerProperties.getSeedInfo().getSearchDataRemoveBack(), "");
                content += removeTag(matchData.trim()).replace("&nbsp;"," ")  + " ";
            }

            if(!content.equals("")){
                insertService.insertData(
                        new CrawlerModel(){{
                            setCrawlingDocId(page.getWebURL().getDocid()+"");
                            setCrawlingBaseUrl(page.getWebURL().getDomain());
                            setCrawlingTargetUrl(page.getWebURL().getURL());
                            setCrawlingKeyword(keyword);
                            setCrawlingTitle(title);
                            setCrawlingImagePath(imagePath);
                            setCrawlingContent(content.trim());
                        }}
                );
            }
        }
    }

    public void init(){
        title = "";
        imageUrl = "";
        imagePath = "";
        content = "";
    }

    /**
     * 모든 HTML 태그를 제거하고 반환한다.
     *
     * @param html
     * @throws Exception
     */
    private String removeTag(String html) {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }



    private String downloadImage(String strUrl) throws IOException {

        String fileNm = crawlerProperties.getImagePath() + File.separator + RandomStringUtils.randomAlphanumeric(32);

        URL url = null;
        InputStream in = null;
        OutputStream out = null;

        try {

            url = new URL(strUrl);
            in = url.openStream();
            out = new FileOutputStream(fileNm); //저장경로

            while(true){
                //이미지를 읽어온다.
                int data = in.read();
                if(data == -1){
                    break;
                }
                //이미지를 쓴다.
                out.write(data);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(in != null){in.close();}
            if(out != null){out.close();}
        }

        return fileNm;
    }

}
