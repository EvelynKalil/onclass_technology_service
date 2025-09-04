package com.onclass.technology.infrastructure.entrypoints;

import com.onclass.technology.infrastructure.entrypoints.handler.TechnologyHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Tag(name = "Tecnologías", description = "Operaciones sobre tecnologías")
@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/technology",
                    produces = {"application/json"},
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "create",
                    operation = @Operation(operationId = "createTechnology", summary = "Registrar tecnología")
            ),
            @RouterOperation(
                    path = "/technologies",
                    produces = {"application/json"},
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "list",
                    operation = @Operation(operationId = "listTechnologies", summary = "Listar tecnologías")
            ),
            @RouterOperation(
                    path = "/technologies/{id}",
                    produces = {"application/json"},
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "findById",
                    operation = @Operation(operationId = "findById", summary = "Buscar tecnología por ID")
            )
    })
    public RouterFunction<ServerResponse> routerFunction(TechnologyHandlerImpl handler) {
        return route(POST("/technology"), handler::create)
                .andRoute(GET("/technologies"), handler::list)
                .andRoute(GET("/technologies/{id}"), handler::findById);
    }
}
