package com.mycompany.security.restfulauth.config;

import com.mycompany.security.restfulauth.security.JwtAuthenticationFilter;
import com.mycompany.security.restfulauth.security.JwtAuthorizationFilter;
import com.mycompany.security.restfulauth.services.MyUserDetailsService;
import com.mycompany.security.restfulauth.services.RestAuthEntryPoint;
import com.mycompany.security.restfulauth.utils.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService myUserDetailsService;

    private RestAuthEntryPoint restAuthEntryPoint;

    private SecurityConfig securityConfig;

    @Autowired
    public WebSecurityConfig(MyUserDetailsService myUserDetailsService, RestAuthEntryPoint restAuthEntryPoint,
                             SecurityConfig securityConfig) {
        this.myUserDetailsService = myUserDetailsService;
        this.restAuthEntryPoint = restAuthEntryPoint;
        this.securityConfig = securityConfig;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(this.securityConfig.getStrength());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.myUserDetailsService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, this.securityConfig.getSignupUri(),
                             this.securityConfig.getLoginUri()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), this.securityConfig))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.securityConfig))
                .exceptionHandling()
                .authenticationEntryPoint(this.restAuthEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(SecurityUtils.HEADER));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
