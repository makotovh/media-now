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

    @GetMapping("/{id}")
    public Mono<Plan> getPlan(@PathVariable long id) {
        return planService.get(id);
    }

    @PutMapping("/{id}")
    public Mono<Plan> updatePlan(@PathVariable long id, @RequestBody @Valid PlanRequest PlanToUpdate) {
        return planService.update(id, PlanToUpdate);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePlan(@PathVariable long id) {
        return planService.delete(id);
    }

    @GetMapping
    public Flux<Plan> getPlans() {
        return planService.getAll();
    }
}
