package Project.KodoKeliasLasProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/recipes/new", "/recipes/*/comments").authenticated()
                .requestMatchers(
                        "/",
                        "/main",
                        "/recipes",
                        "/recipes/all",
                        "/login",
                        "/register",
                        "/images/**",
                        "/uploads/**",
                        "/h2-console/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/recipes/*").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/recipes", true)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/recipes")
                    .permitAll()
            )
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
