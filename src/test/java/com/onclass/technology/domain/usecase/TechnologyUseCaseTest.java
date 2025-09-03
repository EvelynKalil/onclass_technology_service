package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.enums.TechnicalMessage;
import com.onclass.technology.domain.exceptions.BusinessException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TechnologyUseCaseTest {

    private TechnologyPersistencePort persistence;
    private TechnologyUseCase useCase;

    @BeforeEach
    void setUp() {
        persistence = Mockito.mock(TechnologyPersistencePort.class);
        useCase = new TechnologyUseCase(persistence);
    }

    @Test
    void shouldFailWhenNameIsNull() {
        Technology tech = new Technology(null, null, "desc");

        StepVerifier.create(useCase.register(tech))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_NAME_REQUIRED)
                .verify();
    }

    @Test
    void shouldFailWhenDescriptionIsNull() {
        Technology tech = new Technology(null, "Java", null);

        StepVerifier.create(useCase.register(tech))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_DESCRIPTION_REQUIRED)
                .verify();
    }

    @Test
    void shouldFailWhenNameTooLong() {
        String longName = "a".repeat(51);
        Technology tech = new Technology(null, longName, "desc");

        StepVerifier.create(useCase.register(tech))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_NAME_TOO_LONG)
                .verify();
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        String longDesc = "a".repeat(91);
        Technology tech = new Technology(null, "Java", longDesc);

        StepVerifier.create(useCase.register(tech))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_DESCRIPTION_TOO_LONG)
                .verify();
    }

    @Test
    void shouldFailWhenTechnologyAlreadyExists() {
        Technology tech = new Technology(null, "Java", "desc");
        when(persistence.existsByName("Java")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.register(tech))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)
                .verify();

        verify(persistence, never()).save(any());
    }

    @Test
    void shouldSaveSuccessfullyWhenValid() {
        Technology tech = new Technology(null, "Java", "desc");
        Technology saved = new Technology(1L, "Java", "desc");

        when(persistence.existsByName("Java")).thenReturn(Mono.just(false));
        when(persistence.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.register(tech))
                .expectNext(saved)
                .verifyComplete();

        verify(persistence).save(any());
    }

    @Test
    void shouldListTechnologies() {
        when(persistence.findAll()).thenReturn(Flux.just(
                new Technology(1L, "Java", "desc"),
                new Technology(2L, "Spring", "desc2")
        ));

        StepVerifier.create(useCase.list())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldFindById() {
        Technology tech = new Technology(1L, "Java", "desc");
        when(persistence.findById(1L)).thenReturn(Mono.just(tech));

        StepVerifier.create(useCase.findById(1L))
                .expectNext(tech)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {
        when(persistence.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findById(99L))
                .verifyComplete();
    }
}
