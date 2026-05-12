package com.project.cozystay.search.repository;

import com.project.cozystay.search.domain.AccommodationDocument;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 숙소 검색 고도화를 위한 구현체
 */
@Repository
@RequiredArgsConstructor
public class AccommodationElasticSearchRepositoryImpl implements AccommodationElasticSearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<AccommodationDocument> searchAccommodations(
            String title,
            String state,
            String city,
            String district,
            List<Long> excludedIds,
            Pageable pageable
    ) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

        // 1. 숙소명 검색 (Full Text Search)
        if (StringUtils.hasText(title)) {
            boolQueryBuilder.must(q -> q
                .match(m -> m
                    .field("title")
                    .query(title)
                    .fuzziness("1")
                )
            );
        }

        // 2. 주소(state, city, district 등) 필터
        if (StringUtils.hasText(state)) {
            boolQueryBuilder.filter(q -> q
                .term(t -> t
                    .field("state")
                    .value(state)
                )
            );
        }

        if (StringUtils.hasText(city)) {
            boolQueryBuilder.filter(q -> q
                .term(t -> t
                    .field("city")
                    .value(city)
                )
            );
        }

        if (StringUtils.hasText(district)) {
            boolQueryBuilder.filter(q -> q
                .term(t -> t
                    .field("district")
                    .value(district)
                )
            );
        }

        // 3. 이미 예약된 날짜 제외
        if (excludedIds != null && !excludedIds.isEmpty()) {
            List<FieldValue> fieldValues = excludedIds.stream()
                    .map(FieldValue::of)
                    .toList();

            boolQueryBuilder.mustNot(q -> q
                .terms(t -> t
                    .field("id")
                    .terms(v -> v.value(fieldValues))
                )
            );
        }

        queryBuilder.withQuery(boolQueryBuilder.build()._toQuery());
        queryBuilder.withPageable(pageable);

        Query query = queryBuilder.build();
        SearchHits<AccommodationDocument> searchHits = elasticsearchOperations.search(query, AccommodationDocument.class);
        SearchPage<AccommodationDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);

        return (Page<AccommodationDocument>) SearchHitSupport.unwrapSearchHits(searchPage);
    }
}
