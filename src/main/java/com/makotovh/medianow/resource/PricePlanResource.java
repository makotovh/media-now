package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.PricePlan;
import com.makotovh.medianow.model.PricePlanRequest;
import com.makotovh.medianow.service.PricePlanService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/price-plans")
@AllArgsConstructor
public class PricePlanResource {

    private final PricePlanService pricePlanService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<PricePlan> createPricePlan(@RequestBody @Valid PricePlanRequest request) {
        return pricePlanService.createPricePlan(request);
    }

    @GetMapping("/{id}")
    public Mono<PricePlan> getPricePlan(@PathVariable("id") long id) {
        return pricePlanService.getPricePlan(id);
    }
}
