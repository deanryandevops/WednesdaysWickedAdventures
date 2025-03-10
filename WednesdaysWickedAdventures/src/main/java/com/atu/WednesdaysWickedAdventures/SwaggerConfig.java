package com.atu.WednesdaysWickedAdventures;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * <p>
 * This class uses the {@link OpenAPIDefinition} annotation to configure the OpenAPI specification
 * for the Wednesday's Wicked Adventures API. It provides metadata such as title, version,
 * description, contact information, license, and server URLs.
 * </p>
 * <p>
 * The class is annotated with {@link Configuration} to indicate that it is a Spring configuration class.
 * This enables Spring to process the annotations and configure the OpenAPI documentation accordingly.
 * </p>
 * <p>
 * The {@link OpenAPIDefinition} annotation defines the overall metadata for the API.
 * The {@link Info} annotation within {@link OpenAPIDefinition} provides details about the API,
 * including its title, version, description, contact, and license.
 * </p>
 * <p>
 * The {@link Contact} annotation specifies the contact information for the API's maintainer.
 * The {@link License} annotation defines the license under which the API is distributed.
 * </p>
 * <p>
 * The {@link Server} annotations define the URLs for the API's servers, including local development
 * and production servers.
 * </p>
 * <p>
 * No additional code is needed within this class if you are solely relying on {@link OpenAPIDefinition}
 * and auto-configuration. Spring Boot and the OpenAPI dependencies will handle the rest.
 * </p>
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Wednesday's Wicked Adventures API",
        version = "1.0",
        description = "Online booking system for a range of horror themed adventure parks(School Project)",
        contact = @Contact(
            name = "Mathieu Bizumuremyi",
            url = "https://github.com/l00188517",
            email = "L00188517@atu.ie"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://github.com/apache/.github/blob/main/LICENSE")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local development server"),
        @Server(url = "https://production.example.com", description = "Production server")
    }
)
public class SwaggerConfig {

    // No additional code is needed here as we are relying on @OpenAPIDefinition
    // and auto-configuration. Spring Boot and the OpenAPI dependencies will handle the rest.

}