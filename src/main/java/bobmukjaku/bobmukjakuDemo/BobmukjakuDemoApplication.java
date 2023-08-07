package bobmukjaku.bobmukjakuDemo;

import bobmukjaku.bobmukjakuDemo.chatting.FireBaseAdminSdk;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
