package kr.co.crawler.batch.runners;

import kr.co.crawler.service.impl.CrawlerServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.IExitCodeExceptionMapper;

import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
@Slf4j
@Command(name = "java -jar crawler-manager.jar", mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "command")
public class AppCommand implements Callable<Integer>, IExitCodeExceptionMapper {

    private final CrawlerServiceImpl crawlerService;

    @Override
    public Integer call() throws Exception {
        crawlerService.run();

        return ExitCode.OK;
    }

    @Override
    public int getExitCode(Throwable exception) {
        return 500;
    }
}