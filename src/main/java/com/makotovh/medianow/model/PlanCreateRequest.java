package com.makotovh.medianow.model;

import javax.validation.constraints.NotBlank;

public record PlanCreateRequest(@NotBlank String code, @NotBlank String name, String description) {
}
