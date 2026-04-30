package com.project.cozystay.global.init;

import com.project.cozystay.search.service.ElasticsearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE) // 모든 초기화 작업(DataInitializer 포함)이 끝난 후 실행
public class ElasticsearchInitializer implements CommandLineRunner {

    private final ElasticsearchSyncService elasticsearchSyncService;

    @Override
    public void run(String... args) throws Exception {
        log.info("애플리케이션 기동 후 Elasticsearch 자동 동기화 시작...");
        try {
            int count = elasticsearchSyncService.syncAll();
            log.info("Elasticsearch 자동 동기화 성공 (총 {}건)", count);
        } catch (Exception e) {
            log.error("Elasticsearch 자동 동기화 중 오류 발생: {}", e.getMessage());
        }
    }
}
