package cs.project.TextToSpeech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "cs.project.TextToSpeech.infra.repository")

public class TextToSpeechApplication {

	public static void main(String[] args) {
		SpringApplication.run(TextToSpeechApplication.class, args);
	}

}
