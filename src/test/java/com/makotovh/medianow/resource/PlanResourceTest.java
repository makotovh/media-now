package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanRequest;
import com.makotovh.medianow.repository.PlanRepository;
import com.makotovh.medianow.service.PlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(PlanResource.class)
@Import({PlanService.class})
class PlanResourceTest {

    @MockBean
    private PlanRepository planRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addPlan() {
        var planName = "testPlan";
        var planDescription = "testDescription";
        PlanRequest planRequest = new PlanRequest(planName, planDescription);
        Plan planEntity = new Plan(1, planName, planDescription);
        Plan expectedPlan = new Plan(1, planName, planDescription);

        when(planRepository.save(new Plan(0, planName, planDescription))).thenReturn(Mono.just(planEntity));

        webTestClient.post()
                .uri("/plans")
                .bodyValue(planRequest)
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
                .bodyValue(new PlanRequest(planName, "testDescription"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getPlan() {
        String planName = "testPlan";
        String planDescription = "testDescription";
        Plan expectedPlan = new Plan(1, planName, planDescription);

        when(planRepository.findById(1L)).thenReturn(Mono.just(new Plan(1, planName, planDescription)));

        webTestClient.get()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @Test
    void getPlanNotFound() {
        when(planRepository.findById(1L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updatePlan() {
        String planName = "testPlan";
        String planDescription = "testDescription";
        PlanRequest planRequest = new PlanRequest(planName, planDescription);
        Plan planEntity = new Plan(1, planName, planDescription);
        Plan expectedPlan = new Plan(1, planName, planDescription);

        when(planRepository.findById(1L)).thenReturn(Mono.just(planEntity));
        when(planRepository.save(planEntity)).thenReturn(Mono.just(planEntity));

        webTestClient.put()
                .uri("/plans/1")
                .bodyValue(planRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @Test
    void updatePlanNotFound() {
        when(planRepository.findById(1L)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/plans/1")
                .bodyValue(new PlanRequest("testPlan", "testDescription"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deletePlan() {
        Plan testPlan = new Plan(1,"testPlan", "testDescription");
        when(planRepository.findById(1L)).thenReturn(Mono.just(testPlan));
        when(planRepository.delete(testPlan)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deletePlanNotFound() {
        when(planRepository.findById(1L)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listPlans() {
        String planName = "testPlan";
        String planDescription = "testDescription";
        Plan plan = new Plan(1, planName, planDescription);
        Plan expectedPlan = new Plan(1, planName, planDescription);
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