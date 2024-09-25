package co.kr.compig.global.validator;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import co.kr.compig.global.utils.MessageUtil;
import co.kr.compig.global.validator.annotaion.ValidPhoneNum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckPhoneNum implements ConstraintValidator<ValidPhoneNum, String> {

	private ValidPhoneNum validPhoneNum;

	private final MessageUtil messageUtil;

	@Override
	public void initialize(ValidPhoneNum validHscode) {
		this.validPhoneNum = validHscode;
	}

	@Override //true 값이 유효, false 요효성 검사 실패
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		if (isValidPhoneNumber(value)) {
			return false;
		}

		context.buildConstraintViolationWithTemplate(messageUtil.getMessage(validPhoneNum.message()))
			.addConstraintViolation()
			.disableDefaultConstraintViolation();
		return false;
	}

	private boolean isValidPhoneNumber(String phoneNumber) {
		String data = RegExUtils.replaceAll(phoneNumber, "[^0-9]", "");

		return data.length() >= 6 && data.length() <= 10;
	}
}
