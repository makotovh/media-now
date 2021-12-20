package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.Plan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends ReactiveCrudRepository<Plan, Long> {
}
