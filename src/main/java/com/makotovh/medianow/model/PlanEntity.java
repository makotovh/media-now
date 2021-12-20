package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("Plan")
public record PlanEntity(String name, @Id long id) {

}
