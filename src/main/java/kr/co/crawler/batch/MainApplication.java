package kr.co.crawler.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.util.StopWatch;

@Slf4j
@SpringBootApplication
@ComponentScan({"kr.co.crawler"})
public class MainApplication {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConfigurableApplicationContext applicationContext = SpringApplication.run(
            MainApplication.class, args);

        log.info("Closing Application Context");
        int exitCode = SpringApplication.exit(applicationContext);
        stopWatch.stop();

        log.info("Run Time >> {} sec", stopWatch.getTotalTimeSeconds());
        System.exit(exitCode);
    }

    @Bean
    public ExitCodeEventModel exitCodeEventModelInstance() {
        return new ExitCodeEventModel();
    }

    private static class ExitCodeEventModel {
        public ExitCodeEventModel() {
            log.debug("Instantiating ExitCodeEventModel object");
        }
        @EventListener
        public void exitCodeEvent(ExitCodeEvent event) {
            log.error("exit code: " + event.getExitCode());
        }
    }
}
