package com.undec.acreditacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point. Lives in the infrastructure boundary; the domain and
 * application layers contain no Spring imports.
 */
@SpringBootApplication
public class AcreditacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcreditacionApplication.class, args);
    }
}
