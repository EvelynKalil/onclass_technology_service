package com.onclass.technology.domain.api;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyServicePort {
    Mono<Technology> register(Technology technology);
    Flux<Technology> list();
    Mono<Technology> findById(Long id);
}
