package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.PricePlanEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PricePlanRepository extends ReactiveCrudRepository<PricePlanEntity, Long> {

    Mono<PricePlanEntity> findByPlanCodeAndCountryCodeAndEndDateIsNull(String planCode, String countryCode);

    Flux<PricePlanEntity> findByPlanCode(String planCode);
}
