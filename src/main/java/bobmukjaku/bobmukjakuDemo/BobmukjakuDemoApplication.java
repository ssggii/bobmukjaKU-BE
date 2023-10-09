package bobmukjaku.bobmukjakuDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BobmukjakuDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BobmukjakuDemoApplication.class, args);
	}
}
