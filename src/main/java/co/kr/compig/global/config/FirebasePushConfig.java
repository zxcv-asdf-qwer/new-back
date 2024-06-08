package co.kr.compig.global.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebasePushConfig {

	@Value("${api.fcm.user.serviceKeyPath}")
	private String serviceKeyPath;
	@Value("${api.fcm.user.projectId}")
	private String projectId;

	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(serviceKeyPath).getInputStream());
		FirebaseOptions firebaseOptions = FirebaseOptions
			.builder()
			.setCredentials(googleCredentials)
			.build();
		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, projectId);
		return FirebaseMessaging.getInstance(app);
	}

}
