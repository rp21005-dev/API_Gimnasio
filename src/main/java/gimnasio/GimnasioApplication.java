package gimnasio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Activa el gestor de caché de Spring
public class GimnasioApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            GimnasioApplication.class,
            args
        );
    }
}