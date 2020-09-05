package kr.co.crawler.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class Selenium {

    private String searchUrl;
    private Integer searchMoreMax;
    private String htmlPath;
    private String keyword;


    public Selenium(String searchUrl, Integer searchMoreMax, String htmlPath, String keyword) {
        this.searchUrl = searchUrl;
        this.searchMoreMax = searchMoreMax;
        this.htmlPath = htmlPath;
        this.keyword = keyword;
    }


    public String run() throws InterruptedException {

        /**
         * OS 체크
         */
        String extension = "";
        String currentOs = System.getProperty("os.name").toLowerCase();
        if(currentOs.contains("win")){
            extension = ".exe";
        } else if(currentOs.contains("mac")){
        } else {
            log.error("Set os name fail");
            return null;
        }

        /**
         *  WebDriver 경로 설정
         */
        //Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/chromedriver" + extension);
        Path path = Paths.get(System.getProperty("user.dir"), "chromedriver" + File.separator + "chromedriver" + extension);
        System.setProperty("webdriver.chrome.driver", path.toString());

        /**
         *  WebDriver 옵션 설정
         */
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");          // 최대크기로
        options.addArguments("--headless");                 // Browser를 띄우지 않음
        options.addArguments("--disable-gpu");              // GPU를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요함.
        options.addArguments("--no-sandbox");               // Sandbox 프로세스를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요함.

        /**
         * WebDriver 객체 생성
         */
        ChromeDriver driver = new ChromeDriver( options );
        /**
         * 웹페이지 요청
         */
        driver.get(searchUrl);

        try{
            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
            for(int idx = 0; idx < searchMoreMax; idx++){
                /** javascriptExecutor Exception 처리 **/
                if(driver.findElements(By.xpath("//*[@id=\"more_btn\"]/button")).size() > 0){
                    javascriptExecutor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//*[@id=\"more_btn\"]/button")));
                    log.info(keyword + " " +(idx+1) + " More button click... sync wait 10 seconds");
                    Thread.sleep(10000);
                } else {
                    log.error("javascriptExecutor more button not");
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        saveHtml(getDate() + "_" + keyword, driver.getPageSource().getBytes());

        StringBuffer resultBuffer = new StringBuffer(driver.getPageSource());

        /** chromedriver close **/
        driver.quit();
       // driver.close();

        return resultBuffer.toString();
    }


    private void saveHtml(String filename, byte[] html) {
        File savedir = new File(htmlPath);
        if( !savedir.exists() ) {
            savedir.mkdirs();
        }

        File file = new File(savedir, filename+".html");
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write( html );
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDate(){
       return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
