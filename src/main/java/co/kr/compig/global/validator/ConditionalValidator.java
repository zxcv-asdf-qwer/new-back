package co.kr.compig.global.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.ObjectUtils;

import co.kr.compig.global.validator.annotaion.Conditional;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConditionalValidator implements ConstraintValidator<Conditional, Object> {

	private String selected;
	private String[] required;
	private String message;
	private String[] values;

	@Override
	public void initialize(Conditional requiredIfChecked) {
		selected = requiredIfChecked.selected();
		required = requiredIfChecked.required();
		message = requiredIfChecked.message();
		values = requiredIfChecked.values();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		boolean valid = true;
		try {
			Object actualValue = BeanUtils.getProperty(value, selected);
			if (Arrays.asList(values).contains(actualValue)) {
				for (String propName : required) {
					Object requiredValue = BeanUtils.getProperty(value, propName);
					valid = requiredValue != null && !ObjectUtils.isEmpty(requiredValue);
					//          System.out.println("value: " + "" + requiredValue);
					if (!valid) {
						context.disableDefaultConstraintViolation();
						context.buildConstraintViolationWithTemplate(message)
							.addPropertyNode(propName)
							.addConstraintViolation();
					}
				}
			}
		} catch (IllegalAccessException e) {
			log.error("Accessor method is not available for class : {}, exception : {}", value.getClass().getName(), e);
			return false;

		} catch (NoSuchMethodException e) {
			log.error("Field or method is not present on class : {}, exception : {}", value.getClass().getName(), e);
			return false;

		} catch (InvocationTargetException e) {
			log.error("An exception occurred while accessing class : {}, exception : {}", value.getClass().getName(),
				e);
			return false;

		}

		return valid;
	}
}
