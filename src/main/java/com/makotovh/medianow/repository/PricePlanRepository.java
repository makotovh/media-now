package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.PricePlan;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PricePlanRepository extends ReactiveCrudRepository<PricePlan, Long> {

    @Query("{ 'planCode' : ?0, 'countryCode' : ?1 , 'endDate' : null }")
    Mono<PricePlan> findActiveByPlanCodeAndCountry(String planCode, String countryCode);
}
