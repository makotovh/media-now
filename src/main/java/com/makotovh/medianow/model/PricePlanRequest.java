package com.makotovh.medianow.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record PricePlanRequest(@NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String countryCode,
                               @NotNull @Valid Price price) {
}
