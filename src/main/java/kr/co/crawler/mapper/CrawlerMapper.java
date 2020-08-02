package kr.co.crawler.mapper;

import kr.co.crawler.core.db.annotation.MasterConnection;
import kr.co.crawler.model.CrawlerModel;

@MasterConnection
public interface CrawlerMapper {

    void insertData(CrawlerModel crawlerModel);
}
