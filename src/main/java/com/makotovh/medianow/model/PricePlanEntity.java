package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table("price_plan")
public record PricePlanEntity(@Id long id,
                              @NotBlank String planCode,
                              @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String countryCode,
                              @NotNull BigDecimal priceAmount,
                              @NotNull String priceCurrency,
                              @NotNull LocalDate startDate,
                              LocalDate endDate) {
}
