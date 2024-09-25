package co.kr.compig.global.validator.annotaion;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import co.kr.compig.global.validator.CheckPhoneNum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CheckPhoneNum.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidPhoneNum {
	String message() default "전화번호가 유효하지 않습니다. 번호만 입력해주세요.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
