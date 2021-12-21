package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.DeletePlanWithPricePlansException;
import com.makotovh.medianow.exception.PlanAlreadyExistsException;
import com.makotovh.medianow.exception.PlanNotFoundException;
import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanCreateRequest;
import com.makotovh.medianow.model.PlanUpdateRequest;
import com.makotovh.medianow.repository.PlanRepository;
import com.makotovh.medianow.repository.PricePlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PricePlanRepository pricePlanRepository;

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

    public Mono<Plan> get(String planCode) {
        return planRepository.findByCode(planCode)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(planCode)));
    }

    public Mono<Plan> update(String planCode, PlanUpdateRequest planToUpdate) {
        return planRepository.findByCode(planCode)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(planCode)))
                .map(planEntity -> new Plan(planEntity.id(), planEntity.code(), planToUpdate.name(), planToUpdate.description()))
                .flatMap(planRepository::save);
    }

    public Mono<Void> delete(String planCode) {
        return planRepository.findByCode(planCode)
                .switchIfEmpty(Mono.error(new PlanNotFoundException(planCode)))
                .flatMap(plan -> pricePlanRepository.findByPlanCode(planCode)
                        .count()
                        .flatMap(count -> {
                            if (count == 0) {
                                return planRepository.delete(plan);
                            }
                            return Mono.error(new DeletePlanWithPricePlansException(planCode));
                        }));
    }

    public Flux<Plan> getAll() {
        return planRepository.findAll();
    }
}