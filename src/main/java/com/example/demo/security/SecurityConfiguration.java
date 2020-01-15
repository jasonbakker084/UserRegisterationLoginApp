package com.example.demo.security;


import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

// The WebSecurityConfig class is annotated with @EnableWebSecurity to enable Spring Securityâ€™s web security support and provide the Spring MVC integration.
// It also extends WebSecurityConfigurerAdapter and overrides a couple of its methods to set some specifics of the web security configuration.

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServiceImpl userService;

//    @Autowired
//    private UserDetailsService customUserDetailsService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .and()
                .withUser("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN");
    }


// Hereâ€™s a security configuration that ensures that only authenticated users can see the assigned webpages.

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .headers()
                .frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/successForgotPassword",
                        "/confirm-reset",
                        "/reset-password",
                        "/resetPassword",
                        "/login",
                        "/register",
                        "/successResetPassword",
                        "/assets/**",
                        "templates/**",
                        "/confirm",
                        "/resources/**",
                        "/registration**",
                        "/js/**",
                        "/css/**",
                        "/images/**",
                        "/favicon.ico",
                        "/webjars/**",
                        "/forgot-password").permitAll()
                .antMatchers(
                        "/home/coffeelib/posts",
                        "/api/**",
                        "/welcome/**",
                        "/winewheel/**",
                        "/teawheel/**",
                        "/beerwheel/**",
                        "/whiskeywheel/**",
                        "/library/**").hasAnyRole("ADMIN", "USER")

                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/welcome")
                .failureUrl("/login?error")
//                .successHandler(myAuthenticationSuccessHandler)
//                .failureHandler(authenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .rememberMe()
                //.key("my-secure-key")
                .rememberMeCookieName("my-remember-me-cookie")
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(24 * 60 * 60)
                .and()
                .exceptionHandling()
                .and()
                .cors()
                .and().csrf().disable();

    }
// Implementation of PasswordEncoder that uses the BCrypt strong hashing function.
// Clients can optionally supply a "strength" (a.k.a. log rounds in BCrypt) and a SecureRandom instance.
// The larger the strength parameter the more work will have to be done (exponentially) to hash the passwords.

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

// The abstraction used by PersistentTokenBasedRememberMeServices to store the persistent login tokens for a user.

    PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }

}