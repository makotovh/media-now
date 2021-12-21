package com.makotovh.medianow.model;

import javax.validation.constraints.NotBlank;

public record PlanUpdateRequest(@NotBlank String name, String description) {
}
