package com.polstat.parkir.auth;
import com.polstat.parkir.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired private JwtFilter jwtTokenFilter;
    private final CustomUserDetailsService userDetailsService;
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register", "/login", "/docs/**").permitAll()
                        // Endpoint khusus admin
                        .requestMatchers("/profile/{userId}", "/change-password/{userId}",
                                "/parkir/transaksi/**", "/search/kendaraan/**",
                                "/search/transaksi/**", "/search/users/**",
                                "/parkir/kendaraan/{id}"
                                )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                "/profile","/change-password","/parkir/{nama}","/parkir/summary"
                        )
                        .hasAnyRole("ADMIN", "DOSEN", "MAHASISWA", "KARYAWAN", "UMUM")
                        .requestMatchers(HttpMethod.POST, "/parkir/lokasi").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/parkir/lokasi/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/parkir/lokasi/{id}").hasRole("ADMIN")
                        // Endpoint untuk user yang sudah login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
