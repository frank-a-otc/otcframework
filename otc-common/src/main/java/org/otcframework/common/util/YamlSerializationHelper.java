package org.otcframework.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;

public class YamlSerializationHelper {

	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper;

	static {
		YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
		objectMapper = new ObjectMapper(yamlFactory);
	}

	private YamlSerializationHelper() {}

	public static <T> String serialize(T t) throws JsonProcessingException {
		return objectMapper.writeValueAsString(t);
	}
	
	public static <T> T deserialize(String fileName, Class<T> cls) throws IOException {
		File file = new File(fileName);
		return deserialize(file, cls);
	}
	
	public static <T> T deserialize(File file, Class<T> cls) throws IOException {
		return objectMapper.readValue(file, cls);
	}
}
