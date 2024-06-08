package co.kr.compig.global.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public class CalculateUtil {
	public static int calculateAgeFromJumin(String jumin1, String jumin2) {
		if (StringUtils.isEmpty(jumin1)) {
			return 0;
		}

		// 주민등록번호를 통해 입력 받은 날짜
		int year = Integer.parseInt(jumin1.substring(0, 2));
		int month = Integer.parseInt(jumin1.substring(2, 4));
		int day = Integer.parseInt(jumin1.substring(4, 6));

		// 오늘 날짜
		LocalDate today = LocalDate.now();
		int todayYear = today.getYear();
		int todayMonth = today.getMonthValue();
		int todayDay = today.getDayOfMonth();

		// 주민등록번호 뒷자리로 몇년대인지
		String gender = jumin2.substring(0, 1);
		if (gender.equals("1") || gender.equals("2")) {
			year += 1900;
		} else if (gender.equals("3") || gender.equals("4")) {
			year += 2000;
		} else if (gender.equals("0") || gender.equals("9")) {
			year += 1800;
		}

		// 올해 - 태어난년도
		int americanAge = todayYear - year;

		// 생일이 안지났으면 - 1
		if (month > todayMonth) {
			americanAge--;
		} else if (month == todayMonth) {
			if (day > todayDay) {
				americanAge--;
			}
		}

		return americanAge;
	}

	public static int calculateAgeFromBirth(String birthDay) {
		if (StringUtils.isEmpty(birthDay)) {
			return 0;
		}
		// 년,월,일 자르기
		int birth_year = Integer.parseInt(StringUtils.substring(birthDay, 0, 2));
		int birth_month = Integer.parseInt(StringUtils.substring(birthDay, 2, 4));
		int birth_day = Integer.parseInt(StringUtils.substring(birthDay, 4, 6));
		Calendar current = Calendar.getInstance();
		// 9로 시작할 경우 1900년대, 그 외의 경우 2000년대로 변경
		if (birth_year < 10) {
			birth_year += 2000;
		} else {
			birth_year += 1900;
		}

		// 현재년, 월, 일 get
		int current_year = current.get(Calendar.YEAR);
		int current_month = current.get(Calendar.MONTH) + 1;
		int current_day = current.get(Calendar.DAY_OF_MONTH);
		int age = current_year - birth_year;
		// 만나이
		if (birth_month * 100 + birth_day > current_month * 100 + current_day) {
			age--;
		}
		return age;
	}

	public static Integer calculateYearsFromStartYear(Integer startYear) {
		if (startYear == null || startYear == 0) {
			return null;
		}
		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();

		return currentYear - startYear;
	}

	public Long calculateDaysBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return ChronoUnit.DAYS.between(startDateTime, endDateTime);
	}
}
