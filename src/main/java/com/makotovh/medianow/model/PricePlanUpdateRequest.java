package com.makotovh.medianow.model;

import javax.validation.constraints.NotBlank;

public record PricePlanUpdateRequest(@NotBlank String name,
                                     String description) {
}
