package site.ch00kh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PrestudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrestudyApplication.class, args);
	}

}
