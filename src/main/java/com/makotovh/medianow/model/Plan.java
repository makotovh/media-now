package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;

public record Plan(String name, @Id long id) {
}
