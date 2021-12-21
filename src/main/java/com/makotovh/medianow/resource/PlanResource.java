package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Plan;
import com.makotovh.medianow.model.PlanCreateRequest;
import com.makotovh.medianow.model.PlanUpdateRequest;
import com.makotovh.medianow.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/plans")
@AllArgsConstructor
public class PlanResource {

    private final PlanService planService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create a Plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plan created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Plan already exists",
                    content = @Content)
    })
    public Mono<Plan> createPlan(@RequestBody @Valid PlanCreateRequest planToCreate) {
        return planService.createPlan(planToCreate);
    }

    @GetMapping("/{plan-code}")
    @Operation(summary = "Get plan by plan code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found plan",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                    content = @Content) })
    public Mono<Plan> getPlan(@PathVariable("plan-code") String planCode) {
        return planService.get(planCode);
    }

    @PutMapping("/{plan-code}")
    @Operation(summary = "Update plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                    content = @Content) })
    public Mono<Plan> updatePlan(@PathVariable("plan-code") String planCode, @RequestBody @Valid PlanUpdateRequest planToUpdate) {
        return planService.update(planCode, planToUpdate);
    }

    @DeleteMapping("/{plan-code}")
    @Operation(summary = "Delete plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Plan has Price Plans",
            content = @Content) })
    public Mono<Void> deletePlan(@PathVariable("plan-code") String planCode) {
        return planService.delete(planCode);
    }

    @GetMapping
    @Operation(summary = "List all plans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plans",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)) })})
    public Flux<Plan> getPlans() {
        return planService.getAll();
    }
}
