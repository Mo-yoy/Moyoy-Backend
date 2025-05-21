package com.moyo.backend.follow.converter;

import com.moyo.backend.follow.domain.DetectType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDetectTypeConverter implements Converter<String, DetectType> {

    @Override
    public DetectType convert(String source) {
        return DetectType.fromValue(source);
    }
}