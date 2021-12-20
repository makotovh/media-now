package com.makotovh.medianow.model;

import javax.validation.constraints.NotBlank;

public record PlanRequest(@NotBlank String name, String description) {
}
