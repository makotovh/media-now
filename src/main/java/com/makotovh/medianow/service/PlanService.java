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
        return PlanRepository.save(new Plan(0, newPlan.name(), newPlan.description()));
    }

    public Mono<Plan> get(long id) {
        return PlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(id)));
    }

    public Mono<Plan> update(long id, PlanRequest planToUpdate) {
        return PlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(id)))
                .map(planEntity -> new Plan(planEntity.id(), planToUpdate.name(), planToUpdate.description()))
                .flatMap(PlanRepository::save);
    }

    public Mono<Void> delete(long id) {
        return PlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(id)))
                .flatMap(PlanRepository::delete);
    }

    public Flux<Plan> getAll() {
        return PlanRepository.findAll();
    }
}