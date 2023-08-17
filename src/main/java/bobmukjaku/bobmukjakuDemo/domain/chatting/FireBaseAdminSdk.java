package bobmukjaku.bobmukjakuDemo.domain.chatting;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FireBaseAdminSdk {

    //파이어베이스 Admin sdk 초기화(파이어베이스에 read,write하기 위해서)
    public static void initFirebaseAdminSdk(){

        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(
                    //파이어베이스에서 발급받은 json파일의 경로를 명시
                    //"D:/tools/spring/projects/bobmukjakuDemo/src/main/resources/static/firebaseJson"
                    "D:/home/study/bobmukjaKU-spring/bobmukjaKU-BE/firebase.json"
            );

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        FirebaseOptions options;
        try {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://bobmukjaku-default-rtdb.firebaseio.com/")
                    .setDatabaseAuthVariableOverride(null)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FirebaseApp.initializeApp(options);
    }
}
