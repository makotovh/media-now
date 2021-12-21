package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanRequest;
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
    public Mono<Plan> createPlan(@RequestBody @Valid PlanRequest planToCreate) {
        return planService.createPlan(planToCreate);
    }

    @GetMapping("/{code}")
    public Mono<Plan> getPlan(@PathVariable String code) {
        return planService.get(code);
    }

    @PutMapping("/{code}")
    public Mono<Plan> updatePlan(@PathVariable String code, @RequestBody @Valid PlanRequest PlanToUpdate) {
        return planService.update(code, PlanToUpdate);
    }

    @DeleteMapping("/{code}")
    public Mono<Void> deletePlan(@PathVariable String code) {
        return planService.delete(code);
    }

    @GetMapping
    public Flux<Plan> getPlans() {
        return planService.getAll();
    }
}
