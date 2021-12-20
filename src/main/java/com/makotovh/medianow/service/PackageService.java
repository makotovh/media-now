package com.makotovh.medianow.service;

import com.makotovh.medianow.exception.PackageNotFoundException;
import com.makotovh.medianow.model.Package;
import com.makotovh.medianow.model.PackageEntity;
import com.makotovh.medianow.model.PackageRequest;
import com.makotovh.medianow.repository.PackageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PackageService {
    private final PackageRepository packageRepository;

    public Mono<Package> createPackage(PackageRequest newPackage) {
        return packageRepository.save(new PackageEntity(newPackage.name(), 0))
                .map(packageEntity -> new Package(packageEntity.name(), packageEntity.id()));
    }

    public Mono<Package> get(long id) {
        return packageRepository.findById(id)
                .switchIfEmpty(Mono.error(new PackageNotFoundException(id)))
                .map(packageEntity -> new Package(packageEntity.name(), packageEntity.id()));
    }

    public Mono<Package> update(long id, PackageRequest packageToUpdate) {
        return packageRepository.findById(id)
                .switchIfEmpty(Mono.error(new PackageNotFoundException(id)))
                .map(packageEntity -> new PackageEntity(packageToUpdate.name(), packageEntity.id()))
                .flatMap(packageRepository::save)
                .map(packageEntity -> new Package(packageEntity.name(), packageEntity.id()));
    }

    public Mono<Void> delete(long id) {
        return packageRepository.findById(id)
                .switchIfEmpty(Mono.error(new PackageNotFoundException(id)))
                .flatMap(packageRepository::delete);
    }

    public Flux<Package> getAll() {
        return packageRepository.findAll()
                .map(packageEntity -> new Package(packageEntity.name(), packageEntity.id()));
    }
}