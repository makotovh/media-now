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

    private final PlanService PlanService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<Plan> createPlan(@RequestBody @Valid PlanRequest PlanToCreate) {
        return PlanService.createPlan(PlanToCreate);
    }

    @GetMapping("/{id}")
    public Mono<Plan> getPlan(@PathVariable long id) {
        return PlanService.get(id);
    }

    @PutMapping("/{id}")
    public Mono<Plan> updatePlan(@PathVariable long id, @RequestBody @Valid PlanRequest PlanToUpdate) {
        return PlanService.update(id, PlanToUpdate);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePlan(@PathVariable long id) {
        return PlanService.delete(id);
    }

    @GetMapping
    public Flux<Plan> getPlans() {
        return PlanService.getAll();
    }
}
