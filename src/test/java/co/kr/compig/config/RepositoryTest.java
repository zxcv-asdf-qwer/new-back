package co.kr.compig.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@TestPropertySource(
	properties = {
		"spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=root",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.properties.hibernate.show_sql=true",
		"spring.jpa.properties.hibernate.format_sql=true",
		"spring.jpa.properties.hibernate.use_sql_comments=true",
	})
@Import(TestConfig.class)
public @interface RepositoryTest {

}
