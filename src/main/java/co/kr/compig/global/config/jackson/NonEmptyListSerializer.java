package co.kr.compig.global.config.jackson;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class NonEmptyListSerializer extends StdSerializer<List<?>> {

	public NonEmptyListSerializer() {
		super(List.class, false);
	}

	@Override
	public void serialize(List<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null || value.isEmpty()) {
			// 리스트가 null 이거나 비어있으면 아무 것도 출력하지 않음
		} else {
			gen.writeObject(value); // 정상적인 리스트 직렬화
		}
	}
}
