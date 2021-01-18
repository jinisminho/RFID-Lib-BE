package capstone.library.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket jsonApi() {
        return new Docket(SWAGGER_2)
//                .securitySchemes(singletonList(new ApiKey("JWT", AUTHORIZATION, HEADER.name())))
//                .securityContexts(singletonList(
//                        SecurityContext.builder()
//                                .securityReferences(
//                                        singletonList(SecurityReference.builder()
//                                                .reference("JWT")
//                                                .scopes(new AuthorizationScope[0])
//                                                .build()
//                                        )
//                                )
//                                .build())
//                )
                .select()
                .apis(RequestHandlerSelectors.basePackage("capstone.library"))
                .paths(PathSelectors.any())
                .build();
    }

}
