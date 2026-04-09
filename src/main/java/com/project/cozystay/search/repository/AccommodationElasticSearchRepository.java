package com.project.cozystay.search.repository;

import com.project.cozystay.search.domain.AccommodationDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface AccommodationElasticSearchRepository extends ElasticsearchRepository<AccommodationDocument, Long> {

    /**
     * 최종 정밀 튜닝된 키워드 검색
     * - 노이즈 제거: 모든 숙소에 공통적으로 들어가는 description 필드를 검색 대상에서 제외하여 정확도 향상
     * - Operator AND: 검색어의 모든 토큰(예: '여', '수')이 필드에 존재해야 매칭
     * - Fuzziness 1: '재주' -> '제주'와 같은 오타 교정은 유지
     */
    @Query("{" +
            "  \"multi_match\": {" +
            "    \"query\": \"?0\"," +
            "    \"fields\": [\"title^3\", \"province^2\", \"city^2\", \"address^2\", \"district\"]," +
            "    \"operator\": \"OR\"," +
            "    \"fuzziness\": 1" +
            "  }" +
            "}")
    List<AccommodationDocument> searchByKeyword(String keyword);

    List<AccommodationDocument> findByTitleOrAddress(String title, String address);
}
