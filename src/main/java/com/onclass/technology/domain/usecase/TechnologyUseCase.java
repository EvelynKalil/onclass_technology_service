package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.api.TechnologyServicePort;
import com.onclass.technology.domain.constants.Constants;
import com.onclass.technology.domain.enums.TechnicalMessage;
import com.onclass.technology.domain.exceptions.BusinessException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.spi.TechnologyPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort persistence;

    public TechnologyUseCase(TechnologyPersistencePort persistence) {
        this.persistence = persistence;
    }

    @Override
    public Mono<Technology> register(Technology technology) {
        // Required fields
        if (technology.getName() == null || technology.getName().isBlank()) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NAME_REQUIRED));
        }
        if (technology.getDescription() == null || technology.getDescription().isBlank()) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_DESCRIPTION_REQUIRED));
        }

        // Length constraints
        if (technology.getName().length() > Constants.TECHNOLOGY_NAME_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NAME_TOO_LONG));
        }
        if (technology.getDescription().length() > Constants.TECHNOLOGY_DESCRIPTION_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_DESCRIPTION_TOO_LONG));
        }

        // Unique name
        return persistence.existsByName(technology.getName())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS))
                        : persistence.save(technology)
                );
    }

    @Override
    public Flux<Technology> list() {
        return persistence.findAll();
    }

    @Override
    public Mono<Technology> findById(Long id) {
        return persistence.findById(id);
    }
}
