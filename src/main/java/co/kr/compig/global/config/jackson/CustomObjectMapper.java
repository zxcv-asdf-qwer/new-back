package co.kr.compig.global.config.jackson;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import co.kr.compig.global.code.BaseEnumCode;

public class CustomObjectMapper {

	public static ObjectMapper getObjectMapper() {
		Jackson2ObjectMapperBuilder builder = builder();
		final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
		DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
		builder.serializers(new LocalDateTimeSerializer(DATETIME_FORMATTER));
		builder.deserializers(new LocalDateTimeDeserializer(DATETIME_FORMATTER));
		return builder.build();
	}

	private static Jackson2ObjectMapperBuilder builder() {
		return Jackson2ObjectMapperBuilder
			.json()
			.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
			.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
			.modules(new JavaTimeModule(), new Jdk8Module(), customModule());
	}

	/**
	 * Enum Json Serialize / Deserialize 할 때
	 */
	private static SimpleModule customModule() {
		SimpleModule simpleModule = new SimpleModule();

		// enum 데이터를 객체 형태로 리턴
		simpleModule.addSerializer(Enum.class, new StdSerializer<>(Enum.class) {
			@Override
			public void serialize(Enum value, JsonGenerator gen, SerializerProvider provider) throws IOException {
				if (value instanceof BaseEnumCode) {
					gen.writeStartObject();
					gen.writeStringField("name", value.name());
					gen.writeObjectField("code", ((BaseEnumCode)value).getCode());
					gen.writeObjectField("desc", ((BaseEnumCode)value).getDesc());
					gen.writeEndObject();
				} else {
					gen.writeString(value.name());
				}
			}
		});

		// String 필드에 enum 또는 string 데이터 들어오는 경우 처리
		simpleModule.addDeserializer(String.class, new StdDeserializer<>(String.class) {
			@Override
			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
				final JsonNode jsonNode = p.readValueAsTree();

				return Strings.trimToNull(
					jsonNode.has("name") ? jsonNode.get("code").asText() : jsonNode.asText());
			}
		});

		// enum 필드에 enum 또는 string 데이터가 들어오는 경우 데이터 처리
		simpleModule.setDeserializerModifier(new BeanDeserializerModifier() {
			@Override
			public JsonDeserializer<Enum<?>> modifyEnumDeserializer(DeserializationConfig config,
				final JavaType type,
				BeanDescription beanDesc,
				final JsonDeserializer<?> deserializer) {
				return new JsonDeserializer<>() {
					@Override
					@SuppressWarnings("unchecked")
					public Enum<?> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
						Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>)type.getRawClass();
						final JsonNode jsonNode = p.readValueAsTree();

						// 내부 시스템 api 호출 결과 받은 데이터는 enum 데이터 객체 형태로 들어옴
						// 외부에서 api 호출 시 enum 데이터 코드 값 들어옴
						boolean isEnumData = jsonNode.has("name");

						final String value = isEnumData ? jsonNode.get("code").asText() : jsonNode.asText();

						return Arrays.stream(enumClass.getEnumConstants())
							.filter(e -> {
								if (e instanceof BaseEnumCode) {
									return e.name().equalsIgnoreCase(value) || String.valueOf(
										((BaseEnumCode<?>)e).getCode()).equalsIgnoreCase(value);
								} else {
									return e.name().equalsIgnoreCase(value);
								}
							}).findFirst().orElse(null);// TODO Enum에 맞는 값이 없는 경우 Exception 처리 여부? 우선 null로 리턴
					}
				};
			}
		});

		return simpleModule;
	}
}
