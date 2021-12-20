package com.makotovh.medianow.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("package")
public record PackageEntity(String name, @Id long id) {

}
