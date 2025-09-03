package com.onclass.technology.infrastructure.entrypoints.handler;

import com.onclass.technology.domain.api.TechnologyServicePort;
import com.onclass.technology.domain.enums.TechnicalMessage;
import com.onclass.technology.domain.exceptions.BusinessException;
import com.onclass.technology.domain.exceptions.TechnicalException;
import com.onclass.technology.infrastructure.entrypoints.dto.TechnologyDTO;
import com.onclass.technology.infrastructure.entrypoints.mapper.TechnologyMapper;
import com.onclass.technology.infrastructure.entrypoints.util.APIResponse;
import com.onclass.technology.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.onclass.technology.infrastructure.entrypoints.util.Constants.TECHNOLOGY_ERROR;
import static com.onclass.technology.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyHandlerImpl {

    private final TechnologyServicePort service;
    private final TechnologyMapper mapper;

    // -------- CREATE --------
    public Mono<ServerResponse> create(ServerRequest request) {
        final String messageId = resolveMessageId(request);

        return request.bodyToMono(TechnologyDTO.class)
                .map(mapper::dtoToDomain)
                .flatMap(service::register)
                .doOnSuccess(saved -> log.info("Technology created successfully. messageId={}", messageId))
                .flatMap(saved ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(TechnicalMessage.TECHNOLOGY_CREATED.getMessage()))
                .contextWrite(ctx -> ctx.put(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR + " messageId={}", messageId, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())
                ))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())
                ))
                .onErrorResume(ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())
                ));
    }

    // -------- LIST --------
    public Mono<ServerResponse> list(ServerRequest request) {
        final String messageId = resolveMessageId(request);

        Flux<TechnologyDTO> body = service.list()
                .map(mapper::toDto)
                .contextWrite(ctx -> ctx.put(X_MESSAGE_ID, messageId));

        return ServerResponse.ok().body(body, TechnologyDTO.class)
                .doOnSuccess(resp -> log.info("Technologies listed. messageId={}", messageId))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())
                ))
                .onErrorResume(ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())
                ));
    }

    // -------- FIND BY ID --------
    public Mono<ServerResponse> findById(ServerRequest request) {
        final String messageId = resolveMessageId(request);

        Long id;
        try {
            id = Long.valueOf(request.pathVariable("id"));
        } catch (NumberFormatException e) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    messageId,
                    TechnicalMessage.INVALID_PARAMETERS,
                    List.of(ErrorDTO.builder()
                            .code(TechnicalMessage.INVALID_PARAMETERS.getCode())
                            .message("Invalid id, must be numeric")
                            .param("id")
                            .build())
            );
        }

        return service.findById(id)
                .map(mapper::toDto)
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .bodyValue(APIResponse.builder()
                                .code(TechnicalMessage.ADAPTER_RESPONSE_NOT_FOUND.getCode())
                                .message("Technology not found")
                                .identifier(messageId)
                                .date(Instant.now().toString())
                                .build()))
                .contextWrite(ctx -> ctx.put(X_MESSAGE_ID, messageId))
                .doOnSuccess(resp -> log.info("Technology fetched. id={} messageId={}", id, messageId))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())
                ))
                .onErrorResume(ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())
                ));
    }

    // -------- helpers --------
    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier,
                                                    TechnicalMessage error, List<ErrorDTO> errors) {
        APIResponse apiErrorResponse = APIResponse.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .identifier(identifier)
                .date(Instant.now().toString())
                .errors(errors)
                .build();
        return ServerResponse.status(httpStatus).bodyValue(apiErrorResponse);
    }

    private String resolveMessageId(ServerRequest request) {
        String incoming = request.headers().firstHeader(X_MESSAGE_ID);
        return (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;
    }

}
