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
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
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
    var pricePlan =
        new PricePlanEntity(
            1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);

    var pricePlanRequest = new PricePlanRequest(countryCode, price);

    when(planRepository.findByCode(planCode)).thenReturn(Mono.just(plan));
    when(pricePlanRepository.findByPlanCodeAndCountryCodeAndEndDateIsNull(planCode, countryCode))
        .thenReturn(Mono.empty());
    when(pricePlanRepository.save(any(PricePlanEntity.class))).thenReturn(Mono.just(pricePlan));
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
  @CsvSource(
      value = {"-1:SEK", "-10:SEK", "-100:BRL", "10:DOLLAR"},
      delimiter = ':')
  void shouldValidatePriceWhenCreateNewPricePlan(String wrongPrice, String currencyCode) {
    var pricePlanRequest =
        new PricePlanRequest(countryCode, new Price(new BigDecimal(wrongPrice), currencyCode));
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
    var pricePlan =
        new PricePlanEntity(
            1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);

    when(planRepository.findByCode(planCode)).thenReturn(Mono.just(plan));
    when(pricePlanRepository.findByPlanCodeAndCountryCodeAndEndDateIsNull(planCode, countryCode))
        .thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/plans/PREMIUM/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
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
  void testGetAllPricePlanForAPlan() {
    var pricePlan1 =
        new PricePlanEntity(
            1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);
    var pricePlan2 =
        new PricePlanEntity(
            2,
            "Basic",
            countryCode,
            new BigDecimal("40.00"),
            "SEK",
            LocalDate.now().minus(1, ChronoUnit.YEARS),
            startDate);
    var pricePlan3 =
        new PricePlanEntity(
            3, "Basic", countryCode, new BigDecimal("50.00"), "SEK", startDate, null);

    when(pricePlanRepository.findByPlanCode(planCode))
        .thenReturn(Flux.just(pricePlan1, pricePlan2, pricePlan3));
    webTestClient
        .get()
        .uri("/plans/PREMIUM/price-plans")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(PricePlan.class)
        .hasSize(2);
  }

  @Test
  void testGetAllPricePlanIncludingInactive() {
    var pricePlan1 =
            new PricePlanEntity(
                    1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);
    var pricePlan2 =
            new PricePlanEntity(
                    2,
                    "Basic",
                    countryCode,
                    new BigDecimal("40.00"),
                    "SEK",
                    LocalDate.now().minus(1, ChronoUnit.YEARS),
                    startDate);
    var pricePlan3 =
            new PricePlanEntity(
                    3, "Basic", countryCode, new BigDecimal("50.00"), "SEK", startDate, null);

    when(pricePlanRepository.findByPlanCode(planCode))
            .thenReturn(Flux.just(pricePlan1, pricePlan2, pricePlan3));
    webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/plans/PREMIUM/price-plans")
                    .queryParam("showInactive", true)
                    .build()
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PricePlan.class)
            .hasSize(3);
  }

  @Test
  void shouldFilterPricePlanByRangeOfDates() {
    var startDate1 = LocalDate.of(2010, 1, 1);
    var endDate1 = LocalDate.of(2020, 12, 31);

    var startDate2 = LocalDate.of(2020, 4, 1);

    var startDate3 = LocalDate.of(2020, 12, 31);
    var endDate3 = LocalDate.of(2030, 12, 31);

    var startDate4 = LocalDate.of(2011, 12, 31);
    var endDate4 = LocalDate.of(2012, 1, 1);

    var startDate5 = LocalDate.of(2012, 1, 1);

    var startDate6 = LocalDate.of(2019, 12, 31);
    var endDate6 = LocalDate.of(2021, 1, 1);
    var pricePlan1 =
            new PricePlanEntity(
                    1, planCode, countryCode, price.amount(), price.currencyCode(), startDate1, endDate1);
    var pricePlan2 =
            new PricePlanEntity(
                    2,
                    "PREMIUM",
                    countryCode,
                    new BigDecimal("40.00"),
                    "SEK",
                    startDate2,
                    null);
    var pricePlan3 =
            new PricePlanEntity(
                    3, "PREMIUM", countryCode, price.amount(), price.currencyCode(), startDate3, endDate3);
    var pricePlan4 =
            new PricePlanEntity(
                    4, "PREMIUM", countryCode, price.amount(), price.currencyCode(), startDate4, endDate4);
    var pricePlan5 =
            new PricePlanEntity(
                    5, "PREMIUM", countryCode, price.amount(), price.currencyCode(), startDate5, null);
    var pricePlan6 =
            new PricePlanEntity(
                    6, "PREMIUM", countryCode, price.amount(), price.currencyCode(), startDate6, endDate6);

    when(pricePlanRepository.findByPlanCode(planCode))
            .thenReturn(Flux.just(pricePlan1, pricePlan2, pricePlan3, pricePlan4, pricePlan5, pricePlan6));
    webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/plans/PREMIUM/price-plans/years/2020")
                    .build()
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PricePlan.class)
            .value(pricePlans -> assertThat(pricePlans.stream().map(PricePlan::id).collect(toList()))
                    .isEqualTo(List.of(1L, 2L, 3L)))
            .hasSize(3);
  }

  @Test
  void testUpdatePricePlan() {
    var currentPricePlan = new PricePlanEntity(1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);
    var inactivatedPricePlan =
        new PricePlanEntity(
            currentPricePlan.id(), planCode, countryCode, price.amount(), price.currencyCode(), startDate, LocalDate.now());
    Price newPrice = new Price(new BigDecimal("1.00"), "SEK");
    var newPricePlan = new PricePlanEntity(0, planCode, countryCode, newPrice.amount(), newPrice.currencyCode(), LocalDate.now(), null);

    when(pricePlanRepository.findByPlanCodeAndCountryCodeAndEndDateIsNull(planCode, countryCode))
        .thenReturn(Mono.just(currentPricePlan));
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

    when(pricePlanRepository.findByPlanCodeAndCountryCodeAndEndDateIsNull(planCode, countryCode))
        .thenReturn(Mono.empty());
    webTestClient
        .put()
        .uri("/plans/PREMIUM/price-plans/country/SE")
        .bodyValue(new PricePlanUpdateRequest(newPrice))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @ParameterizedTest
  @CsvSource(
      value = {"-1:SEK", "-10:SEK", "-100:BRL", "10:DOLLAR"},
      delimiter = ':')
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

  @Test
  void testGetPricePlansForPlanAndCountry() {
    var pricePlan1 =
            new PricePlanEntity(
                    1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);
    var pricePlan2 =
            new PricePlanEntity(
                    2,
                    "Basic",
                    countryCode,
                    new BigDecimal("40.00"),
                    "SEK",
                    LocalDate.now().minus(1, ChronoUnit.YEARS),
                    startDate);

    when(pricePlanRepository.findByPlanCodeAndCountryCode(planCode, countryCode))
            .thenReturn(Flux.just(pricePlan1, pricePlan2));
    webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/plans/PREMIUM/price-plans/country/SE")
                    .build()
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PricePlan.class)
            .hasSize(1);
  }

  @Test
  void testGetPricePlansForPlanAndCountryWithInactive() {
    var pricePlan1 =
            new PricePlanEntity(
                    1, planCode, countryCode, price.amount(), price.currencyCode(), startDate, null);
    var pricePlan2 =
            new PricePlanEntity(
                    2,
                    "Basic",
                    countryCode,
                    new BigDecimal("40.00"),
                    "SEK",
                    LocalDate.now().minus(1, ChronoUnit.YEARS),
                    startDate);

    when(pricePlanRepository.findByPlanCodeAndCountryCode(planCode, countryCode))
            .thenReturn(Flux.just(pricePlan1, pricePlan2));
    webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/plans/PREMIUM/price-plans/country/SE")
                    .queryParam("showInactive", true)
                    .build()
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PricePlan.class)
            .hasSize(2);
  }
}
