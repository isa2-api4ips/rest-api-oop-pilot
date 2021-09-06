package eu.europa.ec.isa2.oop.restapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "eu.europa.ec.isa2.oop.restapi",
        "eu.europa.ec.isa2.oop.dsd",
        "org.springdoc"})
@EnableTransactionManagement(proxyTargetClass = true)
public class DsdMockWebAppConfig  implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.setOrder(HIGHEST_PRECEDENCE)
                .addResourceHandler("/index.html",
                        "/favicon-16x16.png",
                        "/swagger-ui*")
                .addResourceLocations("/swagger/");

        registry.setOrder(HIGHEST_PRECEDENCE)
                .addResourceHandler("/schema/**").addResourceLocations("/schema/"); // angular pages


    }
    /**
     *  Bean needed for initializing org.springdoc (with spring boot this is not needed!)
     *  https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-spring-mvc
     * @return ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper springDocObjectMapper() {
        ObjectMapper responseMapper = new ObjectMapper();
        return responseMapper;
    }
}
