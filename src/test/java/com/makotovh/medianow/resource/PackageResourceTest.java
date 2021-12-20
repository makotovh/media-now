package com.makotovh.medianow.resource;

import com.makotovh.medianow.model.Package;
import com.makotovh.medianow.model.PackageEntity;
import com.makotovh.medianow.model.PackageRequest;
import com.makotovh.medianow.repository.PackageRepository;
import com.makotovh.medianow.service.PackageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(PackageResource.class)
@Import({PackageService.class})
class PackageResourceTest {

    @MockBean
    private PackageRepository packageRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addPackage() {
        String packageName = "testPackage";
        PackageRequest packageRequest = new PackageRequest(packageName);
        PackageEntity packageEntity = new PackageEntity(packageName, 1);
        Package expectedPackage = new Package(packageName, 1);

        when(packageRepository.save(new PackageEntity(packageName, 0))).thenReturn(Mono.just(packageEntity));

        webTestClient.post()
                .uri("/packages")
                .bodyValue(packageRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Package.class)
                .isEqualTo(expectedPackage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldValidatePackage(String packageName) {
        webTestClient.post()
                .uri("/packages")
                .bodyValue(new PackageRequest(packageName))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getPackage() {
        String packageName = "testPackage";
        Package expectedPackage = new Package(packageName, 1);

        when(packageRepository.findById(1l)).thenReturn(Mono.just(new PackageEntity(packageName, 1)));

        webTestClient.get()
                .uri("/packages/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Package.class)
                .isEqualTo(expectedPackage);
    }

    @Test
    void getPackageNotFound() {
        when(packageRepository.findById(1l)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/packages/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updatePackage() {
        String packageName = "testPackage";
        PackageRequest packageRequest = new PackageRequest(packageName);
        PackageEntity packageEntity = new PackageEntity(packageName, 1);
        Package expectedPackage = new Package(packageName, 1);

        when(packageRepository.findById(1l)).thenReturn(Mono.just(packageEntity));
        when(packageRepository.save(packageEntity)).thenReturn(Mono.just(packageEntity));

        webTestClient.put()
                .uri("/packages/1")
                .bodyValue(packageRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Package.class)
                .isEqualTo(expectedPackage);
    }

    @Test
    void updatePackageNotFound() {
        when(packageRepository.findById(1l)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/packages/1")
                .bodyValue(new PackageRequest("testPackage"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deletePackage() {
        PackageEntity testPackage = new PackageEntity("testPackage", 1);
        when(packageRepository.findById(1l)).thenReturn(Mono.just(testPackage));
        when(packageRepository.delete(testPackage)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/packages/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deletePackageNotFound() {
        when(packageRepository.findById(1l)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/packages/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listPackages() {
        String packageName = "testPackage";
        PackageEntity packageEntity = new PackageEntity(packageName, 1);
        Package expectedPackage = new Package(packageName, 1);
        List<PackageEntity> packageEntities = new ArrayList<>();
        packageEntities.add(packageEntity);
        when(packageRepository.findAll()).thenReturn(Flux.fromIterable(packageEntities));

        webTestClient.get()
                .uri("/packages")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Package.class)
                .isEqualTo(Collections.singletonList(expectedPackage));
    }
}