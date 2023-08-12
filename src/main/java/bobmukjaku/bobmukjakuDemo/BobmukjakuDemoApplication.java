package bobmukjaku.bobmukjakuDemo;

import bobmukjaku.bobmukjakuDemo.domain.chatting.FireBaseAdminSdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
public class BobmukjakuDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BobmukjakuDemoApplication.class, args);

		//파이어베이스 admin sdk 초기화(파이어베이스에 read, write하기 위해)
		FireBaseAdminSdk.initFirebaseAdminSdk();
	}

}
