package com.makotovh.medianow.model;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PricePlanRequest(@NotBlank String name,
                               String description,
                               @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String countryCode,
                               @NotNull @PositiveOrZero BigDecimal price,
                               LocalDate startDate) {
}
