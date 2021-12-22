package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanCreateRequest;
import com.makotovh.medianow.model.PlanUpdateRequest;
import com.makotovh.medianow.model.PricePlanEntity;
import com.makotovh.medianow.repository.PlanRepository;
import com.makotovh.medianow.repository.PricePlanRepository;
import com.makotovh.medianow.service.PlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(PlanResource.class)
@Import({PlanService.class})
class PlanResourceTest {

    @MockBean
    private PlanRepository planRepository;

    @MockBean
    private PricePlanRepository pricePlanRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final String planCode = "TEST";
    private final String planName = "testPlan";
    private final String planDescription = "test description";

    @Test
    void addPlan() {
        PlanCreateRequest planCreateRequest = new PlanCreateRequest(planCode, planName, planDescription);
        Plan planEntity = new Plan(1, planCode, planName, planDescription);
        Plan expectedPlan = new Plan(1, planCode, planName, planDescription);

        when(planRepository.findByCode(planCode)).thenReturn(Mono.empty());
        when(planRepository.save(new Plan(0, planCode, planName, planDescription))).thenReturn(Mono.just(planEntity));

        webTestClient.post()
                .uri("/plans")
                .bodyValue(planCreateRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldValidatePlan(String planName) {
        webTestClient.post()
                .uri("/plans")
                .bodyValue(new PlanCreateRequest(planCode, planName, planDescription))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldValidatePlanCodeAlreadyExists() {
        when(planRepository.findByCode(planCode)).thenReturn(Mono.just(new Plan(1, planCode, planName, planDescription)));
        webTestClient.post()
                .uri("/plans")
                .bodyValue(new PlanCreateRequest(planCode, planName, planDescription))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "%$%$$%$%", "TEST_11", "1212-TE", "TEST-", "-TEST", "-", "VALID-PATTERN-BUT-TOO-LONG"})
    void shouldValidatePlanCode(String planCode) {
        webTestClient.post()
                .uri("/plans")
                .bodyValue(new PlanCreateRequest(planCode, planName, planDescription))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MAKOTO", "TEST", "TEST-11", "TEST-TEST", "TEST11TEST", "MAKOTO-TEST-TEST-123"})
    void shouldAcceptValidPlanCode(String planCode) {
        Plan planEntity = new Plan(1, planCode, planName, planDescription);

        when(planRepository.findByCode(planCode)).thenReturn(Mono.empty());
        when(planRepository.save(new Plan(0, planCode, planName, planDescription))).thenReturn(Mono.just(planEntity));

        webTestClient.post()
                .uri("/plans")
                .bodyValue(new PlanCreateRequest(planCode, planName, planDescription))
                .exchange()
                .expectStatus().isCreated();
    }


    @Test
    void getPlan() {
        Plan expectedPlan = new Plan(1, planCode, planName, planDescription);

        when(planRepository.findByCode(planCode)).thenReturn(Mono.just(new Plan(1, planCode, planName, planDescription)));

        webTestClient.get()
                .uri("/plans/TEST")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @Test
    void getPlanNotFound() {
        when(planRepository.findByCode(planCode)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/plans/TEST")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updatePlan() {
        PlanUpdateRequest planCreateRequest = new PlanUpdateRequest(planName, planDescription);
        Plan planEntity = new Plan(1, planCode, planName, planDescription);
        Plan expectedPlan = new Plan(1, planCode, planName, planDescription);

        when(planRepository.findByCode(planCode)).thenReturn(Mono.just(planEntity));
        when(planRepository.save(planEntity)).thenReturn(Mono.just(planEntity));

        webTestClient.put()
                .uri("/plans/TEST")
                .bodyValue(planCreateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @Test
    void updatePlanNotFound() {
        when(planRepository.findByCode(planCode)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/plans/TEST")
                .bodyValue(new PlanUpdateRequest(planName, planDescription))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deletePlan() {
        Plan testPlan = new Plan(1, planCode,planName, planDescription);
        when(planRepository.findByCode(planCode)).thenReturn(Mono.just(testPlan));
        when(pricePlanRepository.findByPlanCode(planCode)).thenReturn(Flux.empty());
        when(planRepository.delete(testPlan)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/plans/TEST")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deletePlanNotFound() {
        when(planRepository.findByCode(planCode)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/plans/TEST")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotAllowToDeletePlanWithPricePlanAssigned() {
        Plan testPlan = new Plan(1, planCode,planName, planDescription);
        var pricePlan = new PricePlanEntity(1, planCode, "SE", new BigDecimal("1.00"), "SEK", LocalDate.now(), null);

        when(planRepository.findByCode(planCode)).thenReturn(Mono.just(testPlan));
        when(pricePlanRepository.findByPlanCode(planCode)).thenReturn(Flux.just(pricePlan));
        when(planRepository.delete(testPlan)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/plans/TEST")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void listPlans() {
        Plan plan = new Plan(1, planCode, planName, planDescription);
        Plan expectedPlan = new Plan(1, planCode, planName, planDescription);
        List<Plan> plans = new ArrayList<>();
        plans.add(plan);
        when(planRepository.findAll()).thenReturn(Flux.fromIterable(plans));

        webTestClient.get()
                .uri("/plans")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Plan.class)
                .isEqualTo(Collections.singletonList(expectedPlan));
    }
}