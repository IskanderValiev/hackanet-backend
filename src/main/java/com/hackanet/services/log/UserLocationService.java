package com.hackanet.services.log;

import com.hackanet.utils.FileUtil;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/19/20
 */
@Component
public class UserLocationService {

    @SneakyThrows
    public CityResponse getCountry(String ip) {
        DatabaseReader reader = new DatabaseReader.Builder(FileUtil.getCitiesFile()).build();
        InetAddress ipAddress = InetAddress.getByName(ip);
        return reader.city(ipAddress);
    }
}
