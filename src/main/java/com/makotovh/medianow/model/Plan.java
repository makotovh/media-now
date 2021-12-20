package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;

public record Plan(@Id long id, String name, String description) {
}
