package co.com.manager.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.manager.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+Handler$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
}
