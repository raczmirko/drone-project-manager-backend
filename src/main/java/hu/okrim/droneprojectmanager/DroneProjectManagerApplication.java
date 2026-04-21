package hu.okrim.droneprojectmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DroneProjectManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DroneProjectManagerApplication.class, args);
	}

}
