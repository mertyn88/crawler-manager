-- create cralwer table
CREATE TABLE `crawler` (
   `crawler_seq` INT(11) NOT NULL AUTO_INCREMENT COMMENT '크롤링 시퀀스',
   `crawler_doc_id` VARCHAR(250) NOT NULL COMMENT '크롤링 문서번호',
   `crawler_base_url` VARCHAR(250) DEFAULT NULL COMMENT '기본 주소',
   `crawler_target_url` VARCHAR(400) DEFAULT NULL COMMENT '대상 주소',
   `crawler_keyword` VARCHAR(250) DEFAULT NULL COMMENT '크롤링 검색 키워드',
   `crawler_title` VARCHAR(400) DEFAULT NULL COMMENT '크롤링 타이틀',
   `crawler_image_path` VARCHAR(400) DEFAULT NULL COMMENT '크롤링 이미지',
   `crawler_content` LONGTEXT COMMENT '크롤링 내용',
   `crawler_date` VARCHAR(400) DEFAULT NULL COMMENT '크롤링 날짜',
   `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '크롤링 등록',
   `chg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '크롤링 변경일',
   PRIMARY KEY (`crawler_seq`, `crawler_doc_id`),
   UNIQUE KEY unique_id (`crawler_title`)
) COMMENT '크롤링'
;

