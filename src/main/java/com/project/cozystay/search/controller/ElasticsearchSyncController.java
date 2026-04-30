package com.project.cozystay.search.controller;

import com.project.cozystay.search.service.ElasticsearchSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ElasticsearchSyncController {

    private final ElasticsearchSyncService elasticsearchSyncService;

    @GetMapping("/api/test/es-sync")
    public String syncDataToElasticsearch() {
        int count = elasticsearchSyncService.syncAll();
        return "데이터 동기화 완료! 총 " + count + "개의 숙소가 인덱싱되었습니다.";
    }
}
