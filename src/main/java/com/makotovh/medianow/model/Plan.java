package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;

public record Plan(@Id long id, String code, String name, String description) {
}
