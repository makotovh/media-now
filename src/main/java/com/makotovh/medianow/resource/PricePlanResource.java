package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.PricePlan;
import com.makotovh.medianow.model.PricePlanRequest;
import com.makotovh.medianow.model.PricePlanUpdateRequest;
import com.makotovh.medianow.service.PricePlanService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/plans/{plan-code}/price-plans")
@AllArgsConstructor
public class PricePlanResource {

  private final PricePlanService pricePlanService;

  @PostMapping
  @ResponseStatus(CREATED)
  public Mono<PricePlan> createPricePlan(
      @PathVariable("plan-code") String planCode, @RequestBody @Valid PricePlanRequest request) {
    return pricePlanService.createPricePlan(planCode, request);
  }

  @GetMapping("/{id}")
  public Mono<PricePlan> getPricePlan(@PathVariable("id") long id) {
    return pricePlanService.getPricePlan(id);
  }

  @GetMapping
  public Flux<PricePlan> findPricePlanByCode(@PathVariable("plan-code") String planCode) {
    return pricePlanService.findByPlanCode(planCode);
  }

  @PutMapping("/country/{country-code}")
  public Mono<PricePlan> updatePricePlan(
      @PathVariable("plan-code") String planCode,
      @PathVariable("country-code") String countryCode,
      @RequestBody @Valid PricePlanUpdateRequest request) {
    return pricePlanService.updatePricePlan(planCode, countryCode, request);
  }
}
