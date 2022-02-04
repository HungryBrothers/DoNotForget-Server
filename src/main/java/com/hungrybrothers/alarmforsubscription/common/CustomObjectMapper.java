package com.hungrybrothers.alarmforsubscription.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper extends ObjectMapper {
	public CustomObjectMapper() {
		registerModule(new JavaTimeModule());
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
