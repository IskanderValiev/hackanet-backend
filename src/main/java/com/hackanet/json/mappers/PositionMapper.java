package com.hackanet.json.mappers;

import com.hackanet.json.dto.PositionDto;
import com.hackanet.models.user.Position;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@Component
public class PositionMapper implements Mapper<Position, PositionDto> {

    @Override
    public PositionDto map(Position from) {
        if (from == null) {
            return null;
        }
        return PositionDto.builder()
                .id(from.getId())
                .name(from.getName())
                .build();
    }
}
