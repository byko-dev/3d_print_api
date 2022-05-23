package com.byko.api_3d_printing.configuration;

import com.byko.api_3d_printing.configuration.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MongoUserDetails mongoUserDetails;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(mongoUserDetails);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.cors();

        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.authorizeRequests()
                // Our public endpoints
                .antMatchers(HttpMethod.GET,
                        "/project/conversation",
                        "/project/data",
                        "/download",
                        "/activity",
                        "/images",
                        "/image").permitAll()

                .antMatchers(HttpMethod.POST,
                "/create/project",
                        "/send/response",
                        "/login"
                ).permitAll()

                //Only admin endpoints
                .antMatchers("/send/response/admin",
                        "/change/project/status",
                        "/change/password",
                        "/remove/project",
                        "/projects/list",
                        "/image/update",
                        "/image/delete",
                        "/image/add",
                        "/token/valid").hasRole("ADMIN")
                .anyRequest().authenticated();

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        // disable page caching
        httpSecurity.headers().cacheControl();
    }
}
