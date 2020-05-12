package net.portfo.security;

import net.portfo.enums.RoleEnum;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import static net.portfo.enums.RoleEnum.ROLE_ADMIN;

/**
 * Created by chesterjavier on 5/12/20.
 */
@Configuration
@EnableOAuth2Sso
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .cors()
                .and().authorizeRequests()
                .antMatchers("/**").access(hasRole(ROLE_ADMIN))
                .and()
                .authorizeRequests().anyRequest().access(hasRole(ROLE_ADMIN))
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    private String hasRole(RoleEnum role) {
        return "hasRole('" + role.getRole() + "')";
    }
}
