package com.moyo.backend.common.converter;

import com.moyo.backend.githubFollow.domain.DetectType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDetectTypeConverter implements Converter<String, DetectType> {

    @Override
    public DetectType convert(String source) {
        return DetectType.fromValue(source);
    }
}