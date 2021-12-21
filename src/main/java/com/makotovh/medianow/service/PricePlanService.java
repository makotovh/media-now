package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PlanNotFoundException;
import com.makotovh.medianow.exception.PricePlanAlreadyExistsException;
import com.makotovh.medianow.exception.PricePlanNotFoundException;
import com.makotovh.medianow.model.Price;
import com.makotovh.medianow.model.PricePlan;
import com.makotovh.medianow.model.PricePlanRequest;
import com.makotovh.medianow.model.PricePlanUpdateRequest;
import com.makotovh.medianow.repository.PlanRepository;
import com.makotovh.medianow.repository.PricePlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class PricePlanService {

  private final PricePlanRepository pricePlanRepository;
  private final PlanRepository planRepository;

  public Mono<PricePlan> createPricePlan(String planCode, PricePlanRequest pricePlanRequest) {
    final var startDate = LocalDate.now();
    return planRepository
        .findByCode(planCode)
        .switchIfEmpty(Mono.error(new PlanNotFoundException(planCode)))
        .flatMap(
            plan ->
                pricePlanRepository
                    .findActiveByPlanCodeAndCountryCode(planCode, pricePlanRequest.countryCode())
                    .flux()
                    .count()
                    .flatMap(
                        count -> {
                          if (count == 0) {
                            return pricePlanRepository.save(
                                new PricePlan(
                                    0,
                                    planCode,
                                    pricePlanRequest.countryCode(),
                                    pricePlanRequest.price(),
                                    startDate,
                                    null));
                          } else {
                            return Mono.error(
                                new PricePlanAlreadyExistsException(
                                    planCode, pricePlanRequest.countryCode()));
                          }
                        }));
  }

  public Mono<PricePlan> getPricePlan(Long id) {
    return pricePlanRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new PricePlanNotFoundException(id)));
  }

  public Flux<PricePlan> findByPlanCode(String planCode) {
    return pricePlanRepository.findByPlanCode(planCode);
  }

  public Mono<PricePlan> updatePricePlan(
      String planCode, String countryCode, PricePlanUpdateRequest request) {
    return pricePlanRepository.findActiveByPlanCodeAndCountryCode(planCode, countryCode)
            .switchIfEmpty(Mono.error(new PricePlanNotFoundException(planCode, countryCode)))
            .flatMap(this::inactivatePricePlan)
            .flatMap(pricePlan -> createNewPricePlanWithNewPrice(pricePlan, request.price()));
  }

  private Mono<PricePlan> inactivatePricePlan(PricePlan pricePlan) {
    return pricePlanRepository.save(new PricePlan(pricePlan.id(), pricePlan.planCode(), pricePlan.countryCode(), pricePlan.price(), pricePlan.startDate(), LocalDate.now()));
  }

  public Mono<PricePlan> createNewPricePlanWithNewPrice(PricePlan pricePlan, Price newPrice) {
    PricePlan newPricePlan = new PricePlan(0, pricePlan.planCode(), pricePlan.countryCode(), newPrice, LocalDate.now(), null);
    return pricePlanRepository.save(newPricePlan);
  }

}
