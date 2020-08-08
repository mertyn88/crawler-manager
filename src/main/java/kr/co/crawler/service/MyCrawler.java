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
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MyCrawler extends CustomCrawler{

    private CrawlerProperties crawlerProperties;
    private InsertService insertService;

    public MyCrawler(CrawlerProperties crawlerProperties, InsertService insertService){
        this.crawlerProperties = crawlerProperties;
        this.insertService = insertService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        //return url.getURL().matches(crawlerProperties.getVisitPattern());
        return url.getURL().matches("https://post.naver.com/viewer/postView.nhn\\?volumeNo=.*searchRank=.*$");
    }

    public boolean shouldVisit(String url) {
        //return url.matches(crawlerProperties.getVisitPattern());
        return url.matches("https://post.naver.com/viewer/postView.nhn\\?volumeNo=.*searchRank=.*$");
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

            //Elements eleList1 = doc.select("span").select(".se_ff_nanumgothic");
            //#SEDOC-1488784856643-775776969
            Elements eleList1 = doc.select("#wrap div#cont #__clipContent");

            String temp1  = eleList1.get(0).toString().replaceAll("(?s)<!-- SE3-TEXT \\{ -->.*?<!-- \\} SE3-TEXT -->", "");

           /* try{
                runSelenium(parentUrl);
            }catch (Exception e){
                e.printStackTrace();
            }*/


            System.out.println("타이틀 "+  doc.select("meta[property=nv:news:title]").get(0).attr("content"));
            System.out.println("이미지 "+  doc.select("meta[property=og:image]").get(0).attr("content"));

            String content = "";
            Pattern pat = Pattern.compile("(?s)<!-- SE3-TEXT \\{ -->.*?<!-- \\} SE3-TEXT -->");
            Matcher match = pat.matcher(eleList1.get(0).toString());
            int matchCount = 0;
            while (match.find()) {
                System.out.println(matchCount + " : " + match.group());
                content += match.group()+ " ";
                matchCount++;
            }





            /*String content = doc.select("meta[property=og:description]").get(0).attr("content");*/

            String finalContent = content.trim();
            insertService.insertData(
                    new CrawlerModel(){{
                        setCrawlingDocId(page.getWebURL().getDocid()+"");
                        setCrawlingBaseUrl(page.getWebURL().getDomain());
                        setCrawlingTargetUrl(page.getWebURL().getURL());
                        setCrawlingContent(finalContent);
                    }}
            );
        }
    }

    public static String runSelenium(String URL) throws InterruptedException {
        // 1. WebDriver 경로 설정
        Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        // 2. WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");          // 최대크기로
        options.addArguments("--headless");                 // Browser를 띄우지 않음
        options.addArguments("--disable-gpu");              // GPU를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요함.
        options.addArguments("--no-sandbox");               // Sandbox 프로세스를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요함.

        // 3. WebDriver 객체 생성
        ChromeDriver driver = new ChromeDriver( options );

        // 4. 웹페이지 요청
        driver.get(URL);

        // 5. HTML 저장.
       // saveHtml("test1.html", driver.getPageSource().getBytes() );


        //try {

            //JavascriptExecutor js = (JavascriptExecutor)driver;
            /*js.executeScript(
                    "var inputs = document.getElementsByTagName('input');" +
                            "for(var i = 0; i < inputs.length; i++) { " +
                            "    inputs[i].type = 'radio';" +
                            "}" );*/



/*            IReadOnlyCollection<WebElement> buttons =
                    Driver.FindElements(By.XPath(".//button[@onclick]"));
            IJavaScriptExecutor js = (IJavaScriptExecutor)Driver;
            foreach (IWebElement button in buttons)
            {
                js.ExecuteScript("arguments[0].click();", button);
            }*/

            // js.executeScript("mug.common.nclick(this, '.pmore', '', '', window['g_nclick_prefix']);");
            // saveHtml("test2.html", driver.getPageSource().getBytes() );

            JavascriptExecutor js2 = (JavascriptExecutor) driver;
            js2.executeScript("arguments[0].click();", driver.findElement(By.xpath("//*[@id=\"more_btn\"]/button")));
            Thread.sleep(3000);

            saveHtml("test5.html", driver.getPageSource().getBytes());

            js2.executeScript("arguments[0].click();", driver.findElement(By.xpath("//*[@id=\"more_btn\"]/button")));
        Thread.sleep(3000);
            saveHtml("test6.html", driver.getPageSource().getBytes());

            return new StringBuffer(driver.getPageSource()).toString();
            //WebElement test =   driver.findElement(By.xpath("//*[@id=\"more_btn\"]/button"))

            // 6. 트윗 목록 Block 조회, 로드될 때까지 최대 30초간 대기
            //WebDriverWait wait = new WebDriverWait(driver, 30);
            //WebElement parent = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section[aria-labelledby*=\"accessible-list\"]")));

            //WebElement parent = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("*[@id='more_btn']/button")));
          /*  WebElement parent = driver.findElement(By.id("more_btn"));
            parent.submit();

            saveHtml("test2.html", driver.getPageSource().getBytes() );

            // 7. 트윗 콘텐츠 조회
            List<WebElement> contents = parent.findElements(By.cssSelector("div.css-1dbjc4n.r-my5ep6.r-qklmqi.r-1adg3ll"));
            System.out.println( "조회된 콘텐츠 수 : "+contents.size() );

            if( contents.size() > 0 ) {
                // 8. 트윗 상세 내용 탐색
                for(WebElement content : contents ) {
                    try {
                        String username = content.findElement(By.cssSelector("span > span.css-901oao.css-16my406.r-1qd0xha.r-ad9z0x.r-bcqeeo.r-qvutc0")).getText();
                        String id = content.findElement(By.cssSelector("span.css-901oao.css-16my406.r-1qd0xha.r-ad9z0x.r-bcqeeo.r-qvutc0")).getText();
                        String text = content.findElement(By.cssSelector("div.css-901oao.r-hkyrab.r-1qd0xha.r-a023e6.r-16dba41.r-ad9z0x.r-bcqeeo.r-bnwqim.r-qvutc0")).getText();

                        System.out.println( "========================" );
                        System.out.println( username+" "+id );
                        System.out.println( text );
                        System.out.println( "========================" );
                    } catch ( NoSuchElementException e ) {
                        // pass
                    }
                }
            }

        } catch ( TimeoutException e ) {
            System.out.println("목록을 찾을 수 없습니다.");
        } finally {
            // 9. HTML 저장.
            saveHtml("twitter-selenium-loaded.html", driver.getPageSource().getBytes() );
        }*/

            // WebDriver 종료
            //driver.quit();
      //  }
    }


    public static void saveHtml(String filename, byte[] html) {
        File savedir = new File("C:\\Temp");
        if( !savedir.exists() ) {
            savedir.mkdirs();
        }

        File file = new File(savedir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write( html );
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
