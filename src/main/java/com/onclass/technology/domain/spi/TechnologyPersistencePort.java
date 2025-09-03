package com.onclass.technology.domain.spi;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyPersistencePort {
    Mono<Boolean> existsByName(String name);
    Mono<Technology> save(Technology technology);
    Flux<Technology> findAll();
    Mono<Technology> findById(Long id);
}
