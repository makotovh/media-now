package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PricePlan(@Id long id,
                        @NotBlank String planCode,
                        @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String countryCode,
                        @NotNull @PositiveOrZero BigDecimal price,
                        @NotNull LocalDate startDate,
                        LocalDate endDate) {

    public boolean isActive() {
        if (endDate == null) {
            return true;
        }
        return LocalDate.now().isBefore(endDate);
    }
}
