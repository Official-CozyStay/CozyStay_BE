package com.project.cozystay.search.repository;

import com.project.cozystay.search.domain.AccommodationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface AccommodationElasticSearchRepository extends ElasticsearchRepository<AccommodationDocument, Long> {

    /**
     * 최종 정밀 튜닝된 키워드 검색
     * - 노이즈 제거: 모든 숙소에 공통적으로 들어가는 description 필드를 검색 대상에서 제외하여 정확도 향상
     * - Operator OR: 검색어 중 하나만 포함되어도 검색 결과에 노출 (연관 검색 지원)
     * - Fuzziness 1: '재주' -> '제주'와 같은 오타 교정은 유지
     */
    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {" +
            "        \"multi_match\": {" +
            "          \"query\": \"?0\"," +
            "          \"fields\": [\"title^3\", \"city^2\", \"state\", \"district\"]," +
            "          \"operator\": \"OR\"," +
            "          \"fuzziness\": 1" +
            "        }" +
            "      }" +
            "    ]," +
            "    \"must_not\": [" +
            "      {" +
            "        \"terms\": {" +
            "          \"id\": ?1" +
            "        }" +
            "      }" +
            "    ]" +
            "  }" +
            "}")
    Page<AccommodationDocument> searchByKeywordAndExcludeIds(String keyword, List<Long> excludedIds, Pageable pageable);

    Page<AccommodationDocument> findByIdNotIn(List<Long> excludedIds, Pageable pageable);

    List<AccommodationDocument> findByTitleOrAddress(String title, String address);
}
