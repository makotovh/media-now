package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.Plan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlanRepository extends ReactiveCrudRepository<Plan, String> {

    Mono<Plan> findByCode(String planCode);
}
