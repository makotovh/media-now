package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanCreateRequest;
import com.makotovh.medianow.model.PlanUpdateRequest;
import com.makotovh.medianow.service.PlanService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/plans")
@AllArgsConstructor
public class PlanResource {

    private final PlanService planService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<Plan> createPlan(@RequestBody @Valid PlanCreateRequest planToCreate) {
        return planService.createPlan(planToCreate);
    }

    @GetMapping("/{plan-code}")
    public Mono<Plan> getPlan(@PathVariable String planCode) {
        return planService.get(planCode);
    }

    @PutMapping("/{plan-code}")
    public Mono<Plan> updatePlan(@PathVariable String planCode, @RequestBody @Valid PlanUpdateRequest planToUpdate) {
        return planService.update(planCode, planToUpdate);
    }

    @DeleteMapping("/{plan-code}")
    public Mono<Void> deletePlan(@PathVariable String planCode) {
        return planService.delete(planCode);
    }

    @GetMapping
    public Flux<Plan> getPlans() {
        return planService.getAll();
    }
}
