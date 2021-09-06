package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.AuthRoleType;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.OAuthNationalBrokerOpaqueTokenIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class NationalBrokerWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(NationalBrokerWebSecurityConfig.class);

    @Autowired
    OAuthNationalBrokerOpaqueTokenIntrospector oAuthNationalBrokerOpaqueTokenIntrospector;

    @Autowired
    NationalBrokerProperties nationalBrokerProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (!nationalBrokerProperties.isOAuthSecurityEnabled()) {
            http.authorizeRequests().anyRequest().permitAll();
            return;
        }
        LOG.info("Configuring HttpSecurity inside NATIONAL BROKER.");
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                .mvcMatchers("/oauth/token",
                        // add swagger pages!
                        "/swagger-ui.html", "/swagger-ui/**", "v3/api-docs/**",
                        "/messaging-webhook/**")
                .permitAll()
        ).authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers("/dsd-lcm/**", "/dsd/**").hasAnyAuthority(AuthRoleType.ROLE_UPDATE_DSD.name(), AuthRoleType.ROLE_SUPER_USER.name())
                .antMatchers("/roa/**").hasAnyAuthority(AuthRoleType.ROLE_UPDATE_ROA.name(), AuthRoleType.ROLE_SUPER_USER.name())
                .anyRequest().authenticated()
        ).oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                .opaqueToken(opaqueTokenConfigurer -> opaqueTokenConfigurer.introspector(oAuthNationalBrokerOpaqueTokenIntrospector)));

/*        // for the pilot project allow anonymous webhook invocation!
        List<String> webhookPaths = nationalBrokerProperties.getOauthPathExceptions();
        webhookPaths.forEach(path -> {
            try {
                http.authorizeRequests(authorizeRequests -> authorizeRequests
                        .mvcMatchers(path).permitAll());
            } catch (Exception e) {
                LOG.error("Error occurred while setting webhook authorization exception for path [{}]! Error: [{}].", path, e.getMessage());
            }
        });
*/
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        //So Websecurity for disabling security checks and permitAll for if you want to allow all access AFTER authenticating the user

        if (!nationalBrokerProperties.isOAuthSecurityEnabled()) {
            webSecurity.ignoring().antMatchers("/**");
            return;
        }
        LOG.info("Configuring WebSecurity inside National Broker");
        webSecurity
                .ignoring().antMatchers(
                "/oauth/token"
                , "/test"
                , "/swagger-ui"
                ,"/messaging-webhook/**"
        ).antMatchers(HttpMethod.POST
                ,"/messaging-webhook/**"
        );
    }
}
