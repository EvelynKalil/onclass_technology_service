package com.onclass.technology.infrastructure.adapters.persistenceadapter;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.spi.TechnologyPersistencePort;
import com.onclass.technology.infrastructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.onclass.technology.infrastructure.adapters.persistenceadapter.repository.TechnologyRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {
    private final TechnologyRepository repository;
    private final TechnologyEntityMapper mapper;

    @Override
    public Mono<Technology> save(Technology technology) {
        return repository.save(mapper.toEntity(technology))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Flux<Technology> findAll() {
        return repository.findAll().map(mapper::toModel);
    }

    @Override
    public Mono<Technology> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }
}
