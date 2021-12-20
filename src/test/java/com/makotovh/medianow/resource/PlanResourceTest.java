package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanEntity;
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
    private PlanRepository PlanRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addPlan() {
        String PlanName = "testPlan";
        PlanRequest PlanRequest = new PlanRequest(PlanName);
        PlanEntity PlanEntity = new PlanEntity(PlanName, 1);
        Plan expectedPlan = new Plan(PlanName, 1);

        when(PlanRepository.save(new PlanEntity(PlanName, 0))).thenReturn(Mono.just(PlanEntity));

        webTestClient.post()
                .uri("/plans")
                .bodyValue(PlanRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldValidatePlan(String PlanName) {
        webTestClient.post()
                .uri("/plans")
                .bodyValue(new PlanRequest(PlanName))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getPlan() {
        String PlanName = "testPlan";
        Plan expectedPlan = new Plan(PlanName, 1);

        when(PlanRepository.findById(1l)).thenReturn(Mono.just(new PlanEntity(PlanName, 1)));

        webTestClient.get()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @Test
    void getPlanNotFound() {
        when(PlanRepository.findById(1l)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updatePlan() {
        String PlanName = "testPlan";
        PlanRequest PlanRequest = new PlanRequest(PlanName);
        PlanEntity PlanEntity = new PlanEntity(PlanName, 1);
        Plan expectedPlan = new Plan(PlanName, 1);

        when(PlanRepository.findById(1l)).thenReturn(Mono.just(PlanEntity));
        when(PlanRepository.save(PlanEntity)).thenReturn(Mono.just(PlanEntity));

        webTestClient.put()
                .uri("/plans/1")
                .bodyValue(PlanRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Plan.class)
                .isEqualTo(expectedPlan);
    }

    @Test
    void updatePlanNotFound() {
        when(PlanRepository.findById(1l)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/plans/1")
                .bodyValue(new PlanRequest("testPlan"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deletePlan() {
        PlanEntity testPlan = new PlanEntity("testPlan", 1);
        when(PlanRepository.findById(1l)).thenReturn(Mono.just(testPlan));
        when(PlanRepository.delete(testPlan)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deletePlanNotFound() {
        when(PlanRepository.findById(1l)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/plans/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listPlans() {
        String PlanName = "testPlan";
        PlanEntity PlanEntity = new PlanEntity(PlanName, 1);
        Plan expectedPlan = new Plan(PlanName, 1);
        List<PlanEntity> PlanEntities = new ArrayList<>();
        PlanEntities.add(PlanEntity);
        when(PlanRepository.findAll()).thenReturn(Flux.fromIterable(PlanEntities));

        webTestClient.get()
                .uri("/plans")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Plan.class)
                .isEqualTo(Collections.singletonList(expectedPlan));
    }
}