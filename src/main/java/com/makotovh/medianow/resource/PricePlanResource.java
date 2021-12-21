package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.PricePlan;
import com.makotovh.medianow.model.PricePlanRequest;
import com.makotovh.medianow.model.PricePlanUpdateRequest;
import com.makotovh.medianow.service.PricePlanService;
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
@RequestMapping("/plans/{plan-code}/price-plans")
@AllArgsConstructor
public class PricePlanResource {

  private final PricePlanService pricePlanService;

  @PostMapping
  @ResponseStatus(CREATED)
  @Operation(summary = "Create a Price Plan")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Price Plan created",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = Long.class)) }),
          @ApiResponse(responseCode = "400", description = "Invalid request",
                  content = @Content),
          @ApiResponse(responseCode = "404", description = "Plan not found",
                  content = @Content),
          @ApiResponse(responseCode = "409", description = "Price Plan already exists",
                  content = @Content)
  })
  public Mono<PricePlan> createPricePlan(
      @PathVariable("plan-code") String planCode, @RequestBody @Valid PricePlanRequest request) {
    return pricePlanService.createPricePlan(planCode, request);
  }

  @GetMapping
  @Operation(summary = "Get the Price plans by plan code")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found the Price Plans",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = Long.class)) }),
          @ApiResponse(responseCode = "404", description = "Plan not found",
                  content = @Content) })
  public Flux<PricePlan> findPricePlanByCode(
      @PathVariable("plan-code") String planCode,
      @RequestParam(value = "showInactive", required = false, defaultValue = "false") Boolean showInactive) {
    return pricePlanService
        .findByPlanCode(planCode)
        .filter(pricePlan -> showInactive || pricePlan.isActive());
  }

  @PutMapping("/country/{country-code}")
  @Operation(summary = "Update price of Price plan")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Price Plan updated",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = Long.class)) }),
          @ApiResponse(responseCode = "404", description = "Plan not found",
                  content = @Content) })
  public Mono<PricePlan> updatePricePlan(
      @PathVariable("plan-code") String planCode,
      @PathVariable("country-code") String countryCode,
      @RequestBody @Valid PricePlanUpdateRequest request) {
    return pricePlanService.updatePricePlan(planCode, countryCode, request);
  }

  @GetMapping("/country/{country-code}")
  @Operation(summary = "List all Price Plans by Plan and Country")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found the Price Plans",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = Long.class)) }),
          @ApiResponse(responseCode = "404", description = "Plan not found",
                  content = @Content) })
  public Flux<PricePlan> getPricePlanByCountry(
      @PathVariable("plan-code") String planCode,
      @PathVariable("country-code") String countryCode,
      @RequestParam(value = "showInactive", required = false, defaultValue = "false") Boolean showInactive) {
    return pricePlanService.findByPlanCodeAndCountryCode(planCode, countryCode)
            .filter(pricePlan -> showInactive || pricePlan.isActive());
  }
}
