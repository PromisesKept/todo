package todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import todo.Role;
import todo.entity.UserEntity;
import todo.service.UserService;

import static todo.Role.*;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/new", "/user/ok", "/403").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/new", "/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/todo/new", "/todo").authenticated()
                        .requestMatchers("/user/{id}**").authenticated()
                        .requestMatchers("/admin/**", "/user", "/todo").hasRole(ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/user/{id}/edit").hasAnyAuthority(Role.USER.getAuthority())
                        .requestMatchers(HttpMethod.POST, "/user/{id}/edit").hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority(), Role.MODERATOR.getAuthority())
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginProcessingUrl("/login")
                        .successHandler(((request, response, authentication) -> {
                            Long userId = ((UserEntity) authentication.getPrincipal()).getId();
                            response.sendRedirect("/user/" + userId);
                        }))
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/403"));

        return http.build();
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }


}
