package com.hackanet.json.mappers.activity.log;

import com.hackanet.json.dto.activity.log.UserLocationInfo;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/19/20
 */
@Component
public class UserLocationInfoMapper {

    public UserLocationInfo map(CityResponse cityResponse) {
        UserLocationInfo info = UserLocationInfo.builder()
                .city(cityResponse.getCity().getName())
                .country(cityResponse.getCountry().getName())
                .latitude(cityResponse.getLocation().getLatitude())
                .longitude(cityResponse.getLocation().getLongitude())
                .build();
        return info;
    }
}
