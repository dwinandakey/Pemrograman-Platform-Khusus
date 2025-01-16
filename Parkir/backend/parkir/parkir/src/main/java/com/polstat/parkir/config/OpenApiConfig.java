package com.polstat.parkir.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Polstat Parkir API",
                version = "1.0.0",
                description = """
                        API ini menyediakan layanan untuk mengelola data parkir, kendaraan, lokasi parkir, dan transaksi parkir.
                        Sistem ini dirancang untuk mendukung operasi parkir di Politeknik Statistika STIS.
                        
                        Fitur utama meliputi:
                        - Manajemen lokasi parkir
                        - Manajemen kendaraan yang terdaftar
                        - Transaksi parkir (masuk/keluar)
                        - Statistik dan laporan pendapatan parkir
                        - CRUD data pengguna dengan otentikasi berbasis JWT
                        """,
                contact = @Contact(
                        name = "Dwinanda Muhammad Keyzha",
                        email = "222212576@stis.ac.id",
                        url = "https://github.com/dwinandakey/Pemrograman-Platform-Khusus"
                )
        ),
        servers = @Server(
                description = "Local Development Server",
                url = "http://localhost:8080"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Input token sebagai 'Bearer <token>' di header Authorization untuk mengakses endpoint"
)
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi parkirApi() {
        return GroupedOpenApi.builder()
                .group("Parkir API")
                .pathsToMatch(
                        "/login",
                        "/register",
                        "/docs/**",
                        "/profile/**",
                        "/change-password/**",
                        "/parkir/lokasi/**",
                        "/parkir/transaksi/**",
                        "/parkir/kendaraan/**",
                        "/parkir/masuk",
                        "/parkir/keluar",
                        "/parkir/ketersediaan/**",
                        "/search/kendaraan/**",
                        "/search/transaksi/**",
                        "/search/users/**",
                        "/search/parkir/**"
                )
                .pathsToExclude(
                        "/profile/kendaraan",
                        "/profile/parkir",
                        "/profile/transaksiparkir",
                        "/profile/user"
                )
                .build();
    }
}
