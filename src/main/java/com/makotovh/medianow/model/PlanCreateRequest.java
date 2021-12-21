package com.makotovh.medianow.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record PlanCreateRequest(@NotBlank @Size(min = 4, max = 20) @Pattern(regexp = "^[A-Z][A-Z\\-0-9]+[A-Z0-9]$") String code,
                                @NotBlank String name, String description) {
}
