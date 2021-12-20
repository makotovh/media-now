package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.PricePlan;
import com.makotovh.medianow.model.PricePlanRequest;
import com.makotovh.medianow.repository.PricePlanRepository;
import com.makotovh.medianow.service.PricePlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(PricePlanResource.class)
@Import(PricePlanService.class)
class PricePlanResourceTest {

  @MockBean private PricePlanRepository pricePlanRepository;

  @Autowired private WebTestClient webTestClient;

  @Test
  void testCreatePricePlan() {
    var name = "Premium";
    var description = "Premium plan";
    var price = new BigDecimal("100.00");
    var startDate = LocalDate.now();
    var countryCode = "SE";
    var pricePlan = new PricePlan(1, name, description, countryCode, price, startDate, null);

    var pricePlanRequest = new PricePlanRequest(name, description, countryCode, price, startDate);

    when(pricePlanRepository.save(any(PricePlan.class))).thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(PricePlan.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "  "})
  void shouldValidateNameWhenCreateNewPricePlan(String name) {
    var description = "Premium plan";
    var price = new BigDecimal("100.00");
    var startDate = LocalDate.now();
    var countryCode = "SE";
    var pricePlan = new PricePlan(1, name, description, countryCode, price, startDate, null);

    var pricePlanRequest = new PricePlanRequest(name, description, countryCode, price, startDate);

    when(pricePlanRepository.save(any(PricePlan.class))).thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-10", "-100"})
  void shouldValidatePriceWhenCreateNewPricePlan(String wrongPrice) {
    var name = "Premium";
    var description = "Premium plan";
    var price = new BigDecimal(wrongPrice);
    var startDate = LocalDate.now();
    var countryCode = "SE";
    var pricePlan = new PricePlan(1, name, description, countryCode, price, startDate, null);

    var pricePlanRequest = new PricePlanRequest(name, description, countryCode, price, startDate);

    when(pricePlanRepository.save(any(PricePlan.class))).thenReturn(Mono.just(pricePlan));
    webTestClient
        .post()
        .uri("/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldGetCurrentDateWhenStartDateWasNotInformed() {
    var name = "Premium";
    var description = "Premium plan";
    var price = new BigDecimal("100.00");
    LocalDate expectedStartDate = LocalDate.now();
    var countryCode = "SE";

    var pricePlanRequest = new PricePlanRequest(name, description, countryCode, price, null);
    var createdPricePlan = new PricePlan(1, name, description, countryCode, price, expectedStartDate, null);

    when(pricePlanRepository.save(new PricePlan(0, name, description, countryCode, price, eq(expectedStartDate), null))).thenReturn(Mono.just(createdPricePlan));
    webTestClient
        .post()
        .uri("/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(PricePlan.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "  ", "BRA", "SWEDEN", "se", "Br"})
  void shouldValidateCountryCodeWhenCreateNewPricePlan(String countryCode) {
    var name = "Premium";
    var description = "Premium plan";
    var price = new BigDecimal("100.00");
    var startDate = LocalDate.now();

    var pricePlanRequest = new PricePlanRequest(name, description, countryCode, price, startDate);
    webTestClient
        .post()
        .uri("/price-plans")
        .bodyValue(pricePlanRequest)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void testGetPricePlan() {
    var name = "Premium";
    var description = "Premium plan";
    var price = new BigDecimal("100.00");
    var startDate = LocalDate.now();
    var countryCode = "SE";
    var pricePlan = new PricePlan(1, name, description, countryCode, price, startDate, null);

    when(pricePlanRepository.findById(1L)).thenReturn(Mono.just(pricePlan));
    webTestClient
        .get()
        .uri("/price-plans/1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(PricePlan.class);
  }

  @Test
  void testGetPricePlanNotFound() {
    when(pricePlanRepository.findById(1L)).thenReturn(Mono.empty());
    webTestClient
        .get()
        .uri("/price-plans/1")
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
