package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PlanNotFoundException;
import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanRequest;
import com.makotovh.medianow.repository.PlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository PlanRepository;

    public Mono<Plan> createPlan(PlanRequest newPlan) {
    return PlanRepository.save(new Plan(newPlan.code(), newPlan.name(), newPlan.description()));
    }

    public Mono<Plan> get(String code) {
        return PlanRepository.findById(code)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(code)));
    }

    public Mono<Plan> update(String code, PlanRequest planToUpdate) {
        return PlanRepository.findById(code)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(code)))
                .map(planEntity -> new Plan(planEntity.code(), planToUpdate.name(), planToUpdate.description()))
                .flatMap(PlanRepository::save);
    }

    public Mono<Void> delete(String code) {
        return PlanRepository.findById(code)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(code)))
                .flatMap(PlanRepository::delete);
    }

    public Flux<Plan> getAll() {
        return PlanRepository.findAll();
    }
}