package com.makotovh.medianow.repository;

import com.makotovh.medianow.model.PackageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends ReactiveCrudRepository<PackageEntity, Long> {
}
