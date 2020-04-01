package com.hackanet.services.log;

import com.hackanet.utils.FileUtil;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
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
            if (isLocal(ip)) {
                throw new GeoIp2Exception("The ip is local");
            }
            DatabaseReader reader = new DatabaseReader.Builder(FileUtil.getCitiesFile()).build();
            InetAddress ipAddress = InetAddress.getByName(ip);
            return reader.city(ipAddress);
        } catch (GeoIp2Exception | IOException e) {
            log.error("Couldn't determine location: " + e.getMessage());
            return null;
        }
    }

    private boolean isLocal(String ip) {
        return ip.startsWith("192.168.") || ip.startsWith("127.0.0.1") || ip.startsWith("localhost");
    }
}
