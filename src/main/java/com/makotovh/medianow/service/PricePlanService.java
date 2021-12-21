package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PlanNotFoundException;
import com.makotovh.medianow.exception.PricePlanAlreadyExistsException;
import com.makotovh.medianow.exception.PricePlanNotFoundException;
import com.makotovh.medianow.model.*;
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
                    .findByPlanCodeAndCountryCodeAndEndDateIsNull(planCode, pricePlanRequest.countryCode())
                    .flux()
                    .count()
                    .flatMap(
                        count -> {
                          if (count == 0) {
                            return pricePlanRepository
                                .save(
                                    new PricePlanEntity(
                                        0,
                                        planCode,
                                        pricePlanRequest.countryCode(),
                                        pricePlanRequest.price().amount(),
                                        pricePlanRequest.price().currencyCode(),
                                        startDate,
                                        null))
                                .map(this::toPricePlan);
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
        .map(this::toPricePlan)
        .switchIfEmpty(Mono.error(new PricePlanNotFoundException(id)));
  }

  public Flux<PricePlan> findByPlanCode(String planCode) {
    return pricePlanRepository.findByPlanCode(planCode).map(this::toPricePlan);
  }

  public Mono<PricePlan> updatePricePlan(
      String planCode, String countryCode, PricePlanUpdateRequest request) {
    return pricePlanRepository
        .findByPlanCodeAndCountryCodeAndEndDateIsNull(planCode, countryCode)
        .map(this::toPricePlan)
        .switchIfEmpty(Mono.error(new PricePlanNotFoundException(planCode, countryCode)))
        .flatMap(this::inactivatePricePlan)
        .flatMap(pricePlan -> createNewPricePlanWithNewPrice(pricePlan, request.price()));
  }

  private Mono<PricePlan> inactivatePricePlan(PricePlan pricePlan) {
    return pricePlanRepository
        .save(
            new PricePlanEntity(
                pricePlan.id(),
                pricePlan.planCode(),
                pricePlan.countryCode(),
                pricePlan.price().amount(),
                pricePlan.price().currencyCode(),
                pricePlan.startDate(),
                LocalDate.now()))
        .map(this::toPricePlan);
  }

  public Mono<PricePlan> createNewPricePlanWithNewPrice(PricePlan pricePlan, Price newPrice) {
    var newPricePlan =
        new PricePlanEntity(
            0, pricePlan.planCode(), pricePlan.countryCode(), newPrice.amount(), newPrice.currencyCode(), LocalDate.now(), null);
    return pricePlanRepository.save(newPricePlan).map(this::toPricePlan);
  }

  private PricePlan toPricePlan(PricePlanEntity pricePlanEntity) {
    return new PricePlan(
        pricePlanEntity.id(),
        pricePlanEntity.planCode(),
        pricePlanEntity.countryCode(),
        new Price(pricePlanEntity.priceAmount(), pricePlanEntity.priceCurrency()),
        pricePlanEntity.startDate(),
        pricePlanEntity.endDate());
  }

  public Flux<PricePlan> findByPlanCodeAndCountryCode(String planCode, String countryCode) {
    return pricePlanRepository.findByPlanCodeAndCountryCode(planCode, countryCode).map(this::toPricePlan);
  }
}
