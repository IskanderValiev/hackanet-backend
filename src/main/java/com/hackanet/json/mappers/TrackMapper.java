package com.hackanet.json.mappers;

import com.hackanet.json.dto.TrackDto;
import com.hackanet.models.Track;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/20/20
 */
@Component
public class TrackMapper implements Mapper<Track, TrackDto> {

    @Override
    public TrackDto map(Track from) {
        if (from == null) {
            return null;
        }
        return TrackDto.builder()
                .id(from.getId())
                .name(from.getName())
                .description(from.getDescription())
                .build();
    }
}
