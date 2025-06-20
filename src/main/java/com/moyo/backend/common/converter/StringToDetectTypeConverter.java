package com.moyo.backend.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.moyo.backend.githubFollow.domain.DetectType;

@Component
public class StringToDetectTypeConverter implements Converter<String, DetectType> {

	@Override
	public DetectType convert(String source) {
		return DetectType.fromValue(source);
	}
}
