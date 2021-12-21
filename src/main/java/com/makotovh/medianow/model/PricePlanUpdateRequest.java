package com.makotovh.medianow.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record PricePlanUpdateRequest(@NotNull @Valid Price price) {
}
