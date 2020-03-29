package com.hackanet.json.mappers;

import com.hackanet.json.dto.ReportDto;
import com.hackanet.models.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/10/20
 */
@Component
public class ReportMapper implements Mapper<Report, ReportDto> {

    @Autowired
    private UserSimpleMapper userMapper;

    @Override
    public ReportDto map(Report from) {
        if (from == null) {
            return null;
        }
        ReportDto reportDto = ReportDto
                .builder()
                .id(from.getId())
                .reportStatus(from.getStatus())
                .type(from.getType())
                .entityId(from.getEntityId())
                .user(userMapper.map(from.getUser()))
                .build();
        return reportDto;
    }
}
