package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Package;
import com.makotovh.medianow.model.PackageRequest;
import com.makotovh.medianow.service.PackageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/packages")
@AllArgsConstructor
public class PackageResource {

    private final PackageService packageService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<Package> createPackage(@RequestBody @Valid PackageRequest packageToCreate) {
        return packageService.createPackage(packageToCreate);
    }

    @GetMapping("/{id}")
    public Mono<Package> getPackage(@PathVariable long id) {
        return packageService.get(id);
    }

    @PutMapping("/{id}")
    public Mono<Package> updatePackage(@PathVariable long id, @RequestBody @Valid PackageRequest packageToUpdate) {
        return packageService.update(id, packageToUpdate);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePackage(@PathVariable long id) {
        return packageService.delete(id);
    }

    @GetMapping
    public Flux<Package> getPackages() {
        return packageService.getAll();
    }
}
