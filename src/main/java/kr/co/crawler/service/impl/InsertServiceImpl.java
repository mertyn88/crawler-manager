package kr.co.crawler.service.impl;

import kr.co.crawler.mapper.CrawlerMapper;
import kr.co.crawler.model.CrawlerModel;
import kr.co.crawler.service.InsertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsertServiceImpl implements InsertService {

    //private final CrawlerProperties crawlerProperties;
    private final CrawlerMapper crawlerMapper;

    @Override
    public void insertData(CrawlerModel crawlerModel) {
        crawlerMapper.insertData(crawlerModel);
    }
}
