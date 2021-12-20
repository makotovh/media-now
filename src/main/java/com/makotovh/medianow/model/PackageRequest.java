package com.makotovh.medianow.model;

import javax.validation.constraints.NotBlank;

public record PackageRequest(@NotBlank String name) {
}
