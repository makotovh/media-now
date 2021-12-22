package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record PricePlan(@Id long id,
                        @NotBlank String planCode,
                        @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String countryCode,
                        @NotNull @Valid Price price,
                        @NotNull LocalDate startDate,
                        LocalDate endDate) {

    public boolean isActive() {
        if (endDate == null) {
            return true;
        }
        return LocalDate.now().isBefore(endDate);
    }

    public boolean isStartOrEndInYear(int year) {
        if (startDate.getYear() == year) {
            return true;
        }
        if (endDate == null) {
            return false;
        }
        return endDate.getYear() == year;
    }
}
