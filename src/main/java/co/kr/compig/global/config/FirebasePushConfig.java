package co.kr.compig.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebasePushConfig {

	// @Value("${api.fcm.user.serviceKeyPath}")
	// private String serviceKeyPath;
	// @Value("${api.fcm.user.projectId}")
	// private String projectId;
	//
	// @Bean
	// public FirebaseMessaging firebaseMessaging() throws IOException {
	// 	GoogleCredentials googleCredentials = GoogleCredentials
	// 		.fromStream(new ClassPathResource(serviceKeyPath).getInputStream());
	// 	FirebaseOptions firebaseOptions = FirebaseOptions
	// 		.builder()
	// 		.setCredentials(googleCredentials)
	// 		.build();
	// 	FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, projectId);
	// 	return FirebaseMessaging.getInstance(app);
	// }

}
