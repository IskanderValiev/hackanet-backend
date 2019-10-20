package com.hackanet.json.forms;

import com.hackanet.json.dto.CountryCodeDto;
import com.hackanet.json.mappers.Mapper;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Component
public class CountryCodeMapper implements Mapper<CountryCode, CountryCodeDto> {

    @Override
    public CountryCodeDto map(CountryCode from) {
        return CountryCodeDto.builder()
                .name(from.getName())
                .code(from.getNumeric())
                .alpha2(from.getAlpha2())
                .alpha3(from.getAlpha3())
                .build();
    }
}
