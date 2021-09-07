package eu.europa.ec.isa2.oop.dsd.security;

import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class DSDSWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(DSDSWebSecurityConfig.class);

    @Autowired
    DsdMockProperties dsdProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (!dsdProperties.isOAuthSecurityEnabled()) {
            http.authorizeRequests().anyRequest().permitAll();
            return;
        }

        LOG.info("Configuring HttpSecurity inside DSD Mock.");
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers("/v1/messaging/**").hasAnyAuthority("SCOPE_ROLE_DSD")
                        .mvcMatchers("/dsd-mock/index").permitAll()
                        //.anyRequest().authenticated()
                )
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                        .jwt(
                                jwt -> jwt.decoder(myCustomDecoder())
                        ));
    }

    private JwtDecoder myCustomDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(dsdProperties.getOauthSecurityJwkKeysetUrl()).build();
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        //So Websecurity for disabling security checks and permitAll for if you want to allow all access AFTER authenticating the user

        if (!dsdProperties.isOAuthSecurityEnabled()) {
            webSecurity.ignoring().antMatchers("/**");
            return;
        }
        LOG.info("Configuring WebSecurity inside DSD Mock");
        webSecurity
                .ignoring().antMatchers("/index");
    }
}
