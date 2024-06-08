package co.kr.compig.global.utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class KeyGen {

	static int keyCount = 0;
	static String thisTime = "";
	protected static Random randomNumberGenerator = new Random();

	/**
	 * 시간으로 키생성
	 * @return
	 */
	public static String getTimeKey() {
		return getTimeKey(null);
	}

	/**
	 * 시간을 파라미터의 길이로 키생성
	 * @param nsize
	 * @return
	 */
	public static String getTimeKey(int nsize) {
		return getTimeKey(null, nsize);
	}

	/**
	 * 시간에 파라미터를 접두어로 붙여 키생성
	 * @param prefix
	 * @return
	 */
	public static String getTimeKey(String prefix) {
		return getTimeKey(prefix, 0);
	}

	/**
	 * 시간에 파라미터를 접두어로 붙여 파라미터의 길이로 키생성
	 * @param prefix
	 * @param nsize
	 * @return
	 */
	public static synchronized String getTimeKey(String prefix, int nsize) {
		String jobId;
		if (prefix == null)
			prefix = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentTime = new Date();
		jobId = formatter.format(currentTime) + prefix;

		String seq = Integer.toString(incCount(jobId));

		StringBuilder sb = new StringBuilder();
		sb.append(jobId);
		sb.append("0".repeat(Math.max(0, nsize - seq.length())));
		sb.append(seq);

		thisTime = jobId;

		return sb.toString();
	}

	protected static int incCount(String ctime) {
		if (ctime.equals(thisTime)) {
			keyCount++;
		} else {
			keyCount = 1;
			thisTime = ctime;
		}
		return keyCount;

	}

	public static synchronized String getRandomKey(String strDomainName) {
		String dname = strDomainName == null ? "" : "@" + strDomainName;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentTime = new Date();
		String jobId = formatter.format(currentTime);

		return (new StringBuffer(jobId)).append(Math.abs(Integer.MAX_VALUE)).append(dname).toString();
	}

	public static synchronized String getRandomTimeKey() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date currentTime = new Date();
		String jobId = formatter.format(currentTime);
		int iran = (new Random()).nextInt(1000);
		return (new StringBuffer(jobId)).append(iran).toString();
	}

	private static int seq;

	/**
	 * random 한 변수를 얻어온다
	 */
	public static synchronized String getKey() {
		if (seq == 999)
			seq = 0;

		String msg = System.currentTimeMillis() + new DecimalFormat("000").format(seq++);

		char[] seed = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z'};

		BigInteger zero = BigInteger.ZERO;//new BigInteger(0);
		BigInteger length = new BigInteger("" + seed.length);
		BigInteger val = new BigInteger(msg);

		char[] result = new char[30];
		int i = 0;
		while (val.compareTo(zero) > 0) {
			BigInteger mok = val.divide(length);
			BigInteger nam = val.mod(length);
			val = mok;
			result[i++] = seed[nam.intValue()];
		}
		char[] retval = new char[i];
		System.arraycopy(result, 0, retval, 0, i);
		return String.valueOf(retval);
	}

	private static final char[] chars;

	static {
		StringBuilder buffer = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			buffer.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			buffer.append(ch);
		for (char ch = 'A'; ch <= 'Z'; ++ch)
			buffer.append(ch);
		chars = buffer.toString().toCharArray();
	}

	private static final char[] chars2;

	static {
		StringBuilder buffer = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			buffer.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			buffer.append(ch);
		for (char ch = 'A'; ch <= 'Z'; ++ch)
			buffer.append(ch);
		buffer.append("!@#$%^*-_=+");
		chars2 = buffer.toString().toCharArray();
	}

	public static String random(int length) {
		if (length < 1)
			throw new IllegalArgumentException("length < 1: " + length);

		StringBuilder randomString = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < length; i++) {
			randomString.append(chars[random.nextInt(chars.length)]);
		}
		return randomString.toString();
	}
}
