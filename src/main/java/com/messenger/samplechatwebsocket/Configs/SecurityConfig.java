package com.messenger.samplechatwebsocket.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(){
        return new MyUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }



@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//                .authorizeHttpRequests(auth -> auth
//                    .anyRequest().authenticated());
    http

            .csrf(AbstractHttpConfigurer::disable)
//            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
//            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions
//                    .sameOrigin().disable())) // для H2 Console
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(auth -> auth

//                    .requestMatchers("/", "/auth.html", "/api/register", "/api/login").permitAll()
//                    .requestMatchers("/css/**", "/js/**", "/webjars/**", "/images/**").permitAll()
//                    .requestMatchers("/websocket/**", "/sockjs/**", "/ws/**", "/app/**", "/topic/**").permitAll()
//                    .requestMatchers("/admin/h2", "/admin/h2/").permitAll()
//                    .requestMatchers("/admin/h2/**").permitAll()
//                    .requestMatchers("/h2-console", "/h2-console/").permitAll()
//                    .requestMatchers("/h2-console/**").permitAll()
//
//                    .requestMatchers("/index.html").authenticated()
//                    .anyRequest().authenticated()
                            .requestMatchers("/", "/auth.html", "/api/register", "/api/login").permitAll()
                            .requestMatchers("/css/**", "/js/**", "/webjars/**", "/images/**").permitAll()
                            .requestMatchers("/admin/h2", "/admin/h2/").permitAll()
                            .requestMatchers("/h2-console", "/h2-console/").permitAll()
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/index.html").authenticated()
                            .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/auth.html")
                    .defaultSuccessUrl("/index.html", true)

                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/auth.html")
                    .permitAll()
            );

    return http.build();
}

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(5);
    }
}
