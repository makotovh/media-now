package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PlanNotFoundException;
import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanEntity;
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
        return PlanRepository.save(new PlanEntity(newPlan.name(), 0))
                .map(PlanEntity -> new Plan(PlanEntity.name(), PlanEntity.id()));
    }

    public Mono<Plan> get(long id) {
        return PlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(id)))
                .map(PlanEntity -> new Plan(PlanEntity.name(), PlanEntity.id()));
    }

    public Mono<Plan> update(long id, PlanRequest PlanToUpdate) {
        return PlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(id)))
                .map(PlanEntity -> new PlanEntity(PlanToUpdate.name(), PlanEntity.id()))
                .flatMap(PlanRepository::save)
                .map(PlanEntity -> new Plan(PlanEntity.name(), PlanEntity.id()));
    }

    public Mono<Void> delete(long id) {
        return PlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(id)))
                .flatMap(PlanRepository::delete);
    }

    public Flux<Plan> getAll() {
        return PlanRepository.findAll()
                .map(PlanEntity -> new Plan(PlanEntity.name(), PlanEntity.id()));
    }
}