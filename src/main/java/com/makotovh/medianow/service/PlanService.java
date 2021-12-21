package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PlanAlreadyExistsException;
import com.makotovh.medianow.exception.PlanNotFoundException;
import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanCreateRequest;
import com.makotovh.medianow.model.PlanUpdateRequest;
import com.makotovh.medianow.repository.PlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    public Mono<Plan> createPlan(PlanCreateRequest newPlan) {
    return planRepository.findByCode(newPlan.code())
            .flux().count()
            .flatMap(count -> {
                if (count == 0) {
                    return planRepository.save(new Plan(0, newPlan.code(), newPlan.name(), newPlan.description()));
                }
                return Mono.error(new PlanAlreadyExistsException(newPlan.code()));
            });
    }

    public Mono<Plan> get(String code) {
        return planRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(code)));
    }

    public Mono<Plan> update(String code, PlanUpdateRequest planToUpdate) {
        return planRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(code)))
                .map(planEntity -> new Plan(planEntity.id(), planEntity.code(), planToUpdate.name(), planToUpdate.description()))
                .flatMap(planRepository::save);
    }

    public Mono<Void> delete(String code) {
        return planRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(code)))
                .flatMap(planRepository::delete);
    }

    public Flux<Plan> getAll() {
        return planRepository.findAll();
    }
}