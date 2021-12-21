package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.PricePlan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PricePlanRepository extends ReactiveCrudRepository<PricePlan, Long> {

//    @Query("{ 'plan_code' : ?0, 'country_code' : ?1 , 'end_date' : null }")
//    @Query("Select * from price_plan where plan_code = ?0 and country_code = ?1 and end_date is null")
//    @Query("Select * from price_plan where plan_code = ?0 and country_code = ?1")
    Mono<PricePlan> findActiveByPlanCodeAndCountryCode(String planCode, String countryCode);

    Flux<PricePlan> findByPlanCode(String planCode);
}
