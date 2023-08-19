package bobmukjaku.bobmukjakuDemo.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    //파이어베이스 Admin sdk 초기화
    @PostConstruct
    public void initFirebaseAdminSdk() {

        try {
            FileInputStream serviceAccount = new FileInputStream(
                    //파이어베이스에서 발급받은 json파일의 경로를 명시
                    "D:/tools/spring/projects/bobmukjakuDemo/src/main/resources/serviceAccountKey.json"
//                    "D:/home/study/bobmukjaKU-spring/bobmukjaKU-BE/serviceAccountKey.json"
            );

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://bobmukjaku-default-rtdb.firebaseio.com/")
                    .setDatabaseAuthVariableOverride(null)
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
