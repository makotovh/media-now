package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.PricePlan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricePlanRepository extends ReactiveCrudRepository<PricePlan, Long> {
}
