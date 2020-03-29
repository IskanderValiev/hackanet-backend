package com.hackanet.services.log;

import com.hackanet.utils.FileUtil;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/19/20
 */
@Component
@Slf4j
public class UserLocationService {

    public CityResponse getCountry(String ip) {
        try {
            DatabaseReader reader = new DatabaseReader.Builder(FileUtil.getCitiesFile()).build();
            InetAddress ipAddress = InetAddress.getByName(ip);
            return reader.city(ipAddress);
        } catch (GeoIp2Exception | IOException e) {
            log.error("Couldn't determine location", e);
            return null;
        }
    }
}
