package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PricePlanNotFoundException;
import com.makotovh.medianow.model.PricePlan;
import com.makotovh.medianow.model.PricePlanRequest;
import com.makotovh.medianow.repository.PricePlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class PricePlanService {

  private final PricePlanRepository pricePlanRepository;

  public Mono<PricePlan> createPricePlan(PricePlanRequest pricePlanRequest) {
    var startDate = LocalDate.now();
    if (pricePlanRequest.startDate() != null) {
      startDate = pricePlanRequest.startDate();
    }
    var pricePlan = new PricePlan(
        0,
        pricePlanRequest.name(),
        pricePlanRequest.description(),
        pricePlanRequest.countryCode(),
        pricePlanRequest.price(),
        startDate,
        null);
    return pricePlanRepository.save(pricePlan);
  }

  public Mono<PricePlan> getPricePlan(Long id) {
    return pricePlanRepository.findById(id)
            .switchIfEmpty(Mono.error(new PricePlanNotFoundException(id)));
  }
}
