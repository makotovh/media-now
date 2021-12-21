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

  @GetMapping
  public Flux<PricePlan> findPricePlanByCode(
      @PathVariable("plan-code") String planCode,
      @RequestParam(value = "showInactive", required = false, defaultValue = "false") Boolean showInactive) {
    return pricePlanService
        .findByPlanCode(planCode)
        .filter(pricePlan -> showInactive || pricePlan.isActive());
  }

  @PutMapping("/country/{country-code}")
  public Mono<PricePlan> updatePricePlan(
      @PathVariable("plan-code") String planCode,
      @PathVariable("country-code") String countryCode,
      @RequestBody @Valid PricePlanUpdateRequest request) {
    return pricePlanService.updatePricePlan(planCode, countryCode, request);
  }

  @GetMapping("/country/{country-code}")
  public Flux<PricePlan> getPricePlanByCountry(
      @PathVariable("plan-code") String planCode,
      @PathVariable("country-code") String countryCode,
      @RequestParam(value = "showInactive", required = false, defaultValue = "false") Boolean showInactive) {
    return pricePlanService.findByPlanCodeAndCountryCode(planCode, countryCode)
            .filter(pricePlan -> showInactive || pricePlan.isActive());
  }
}
