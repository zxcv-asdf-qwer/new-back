package co.kr.compig.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * JPA 설정 클래스
 */
@EnableJpaAuditing // JPA Auditing 활성화
@Configuration
public class QuerydslConfig {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * JpaQueryFactory 빈 등록
	 *
	 * @return JPAQueryFactory 쿼리 및 DML 절 생성을 위한 팩토리 클래스
	 */
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(new CustomHibernate5Templates(), entityManager);
	}

}
