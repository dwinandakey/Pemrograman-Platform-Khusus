package com.Mahasiswa.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

/**
 *
 * @author dwinanda
 */

@SpringBootApplication
@Controller
public class MahasiswaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MahasiswaApplication.class, args);
    }
}
