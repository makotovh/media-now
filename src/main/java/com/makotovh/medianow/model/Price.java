package com.makotovh.medianow.model;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public record Price(@NotNull @PositiveOrZero BigDecimal amount,
                    @NotBlank @Size(min = 3, max = 3) @Pattern(regexp = "[A-Z]{3}") String currencyCode) {
}
