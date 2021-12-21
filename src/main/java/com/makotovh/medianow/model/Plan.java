package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;

public record Plan(@Id String code, String name, String description) {
}
