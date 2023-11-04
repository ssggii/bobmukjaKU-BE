package bobmukjaku.bobmukjakuDemo.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Autowired
    private ResourceLoader resourceLoader;

    //파이어베이스 Admin sdk 초기화
    @PostConstruct
    public void initFirebaseAdminSdk() {

        try {
            Resource resource = resourceLoader.getResource("classpath:serviceAccountKey.json");
            InputStream inputStream = resource.getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setDatabaseUrl("https://bobmukjaku-default-rtdb.firebaseio.com/")
                    .setDatabaseAuthVariableOverride(null)
                    .build();

            FirebaseApp.initializeApp(options);
            FirebaseDatabase.getInstance().getReference("/").removeValue((error, ref) -> {

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
