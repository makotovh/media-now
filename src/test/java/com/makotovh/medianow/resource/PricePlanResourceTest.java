package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.*;
import com.makotovh.medianow.repository.PlanRepository;
import com.makotovh.medianow.repository.PricePlanRepository;
import com.makotovh.medianow.service.PricePlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(PricePlanResource.class)
@Import(PricePlanService.class)
class PricePlanResourceTest {

  @MockBean private PricePlanRepository pricePlanRepository;
  @MockBean private PlanRepository planRepository;

  @Autowired private WebTestClient webTestClient;

  private String planCode = "PREMIUM";
  private String countryCode = "SE";
  private Price price = new Price(new BigDecimal("100.00"), "SEK");
  private LocalDate startDate = LocalDate.now().minus(1, ChronoUnit.YEARS);
  private Plan plan = new Plan(1, planCode, "Premium", "Premium Plan");

  @Test
  void testCreatePricePlan() {
    var pricePlan = new PricePlan(1, planCode, countryCode, price, startDate, null);

    var pricePlanRequest = new PricePlanRequest(countryCode, price);

    when(planRepository.findByCode(planCode)).thenReturn(Mono.just(plan));
    when(pricePlanRepository.findActiveByPlanCodeAndCountryCode(planCode, countryCode))
        .thenReturn(Mono.empty());
    when(pricePlanRepository.save(any(PricePlan.class))).thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(PricePlan.class);
  }

  @ParameterizedTest
  @CsvSource(value = {"-1:SEK", "-10:SEK", "-100:BRL", "10:DOLLAR"}, delimiter = ':')
  void shouldValidatePriceWhenCreateNewPricePlan(String wrongPrice, String currencyCode) {
    var pricePlanRequest = new PricePlanRequest(countryCode, new Price(new BigDecimal(wrongPrice), currencyCode));
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "  ", "BRA", "SWEDEN", "se", "Br"})
  void shouldValidateCountryCodeWhenCreateNewPricePlan(String countryCode) {
    var pricePlanRequest = new PricePlanRequest(countryCode, price);
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldNotCreateNewPlanWhenPricePlanAlreadyExists() {
    var pricePlanRequest = new PricePlanRequest(countryCode, price);
    var pricePlan = new PricePlan(1, planCode, countryCode, price, startDate, null);

    when(planRepository.findByCode(planCode)).thenReturn(Mono.just(plan));
    when(pricePlanRepository.findActiveByPlanCodeAndCountryCode(planCode, countryCode))
        .thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  //TODO finish that after implement update price plan
  @Test
  void shouldBePossibleCreateNewPricePlanWhenPricePlanAlreadyExistsButIsInactive() {
    var pricePlanRequest = new PricePlanRequest(countryCode, price);
    var endDate = LocalDate.now();
    var pricePlan = new PricePlan(1, planCode, countryCode, price, startDate, endDate);

    when(planRepository.findByCode(planCode)).thenReturn(Mono.just(plan));
    when(pricePlanRepository.findActiveByPlanCodeAndCountryCode(planCode, countryCode))
        .thenReturn(Mono.just(pricePlan));
    when(pricePlanRepository.save(any(PricePlan.class))).thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(PricePlan.class);
  }

  @Test
  void shouldNotCreateNewPlanWhenPlanIsNotFound() {
    var pricePlanRequest = new PricePlanRequest(countryCode, price);

    when(planRepository.findByCode(planCode)).thenReturn(Mono.empty());
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetPricePlan() {
    var pricePlan = new PricePlan(1, planCode, countryCode, price, startDate, null);

    when(pricePlanRepository.findById(1L)).thenReturn(Mono.just(pricePlan));
    webTestClient
        .get()
        .uri("/plans/PREMIUM/price-plans/1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(PricePlan.class);
  }

  @Test
  void testGetPricePlanNotFound() {
    when(pricePlanRepository.findById(1L)).thenReturn(Mono.empty());
    webTestClient.get().uri("/plans/PREMIUM/price-plans/1").exchange().expectStatus().isNotFound();
  }

  @Test
  void testGetAllPricePlanForAPlan() {
    var pricePlan1 = new PricePlan(1, planCode, countryCode, price, startDate, null);
    var pricePlan2 =
        new PricePlan(
            2,
            "Basic",
            countryCode,
            new Price(new BigDecimal("40.00"), "SEK"),
            LocalDate.now().minus(1, ChronoUnit.YEARS),
            startDate);
    var pricePlan3 =
        new PricePlan(3, "Basic", countryCode, new Price(new BigDecimal("50.00"), "SEK"), startDate, null);

    when(pricePlanRepository.findByPlanCode(planCode))
        .thenReturn(Flux.just(pricePlan1, pricePlan2, pricePlan3));
    webTestClient
        .get()
        .uri("/plans/PREMIUM/price-plans")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(PricePlan.class);
  }

  @Test
  void testUpdatePricePlan() {
    var currentPricePlan = new PricePlan(1, planCode, countryCode, price, startDate, null);
    PricePlan inactivatedPricePlan = new PricePlan(currentPricePlan.id(), planCode, countryCode, price, startDate, LocalDate.now());
    Price newPrice = new Price(new BigDecimal("1.00"), "SEK");
    var newPricePlan = new PricePlan(0, planCode, countryCode, newPrice, LocalDate.now(), null);

    when(pricePlanRepository.findActiveByPlanCodeAndCountryCode(planCode, countryCode)).thenReturn(Mono.just(currentPricePlan));
    when(pricePlanRepository.save(inactivatedPricePlan)).thenReturn(Mono.just(currentPricePlan));
    when(pricePlanRepository.save(newPricePlan)).thenReturn(Mono.just(currentPricePlan));
    webTestClient
        .put()
        .uri("/plans/PREMIUM/price-plans/country/SE")
        .bodyValue(new PricePlanUpdateRequest(newPrice))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(PricePlan.class);
  }

  @Test
  void shouldReturnNotFoundWhenUpdatePricePlanNotFound() {
    Price newPrice = new Price(new BigDecimal("1.00"), "SEK");

    when(pricePlanRepository.findActiveByPlanCodeAndCountryCode(planCode, countryCode)).thenReturn(Mono.empty());
    webTestClient
        .put()
        .uri("/plans/PREMIUM/price-plans/country/SE")
        .bodyValue(new PricePlanUpdateRequest(newPrice))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @ParameterizedTest
  @CsvSource(value = {"-1:SEK", "-10:SEK", "-100:BRL", "10:DOLLAR"}, delimiter = ':')
  void shouldReturnBadRequestWhenUpdatePricePlanWithInvalidPrice(String price, String currency) {
    Price newPrice = new Price(new BigDecimal(price), currency);

    webTestClient
        .put()
        .uri("/plans/PREMIUM/price-plans/country/SE")
        .bodyValue(new PricePlanUpdateRequest(newPrice))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
