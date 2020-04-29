package com.hackanet.json.mappers;

import com.hackanet.json.dto.CompanyDto;
import com.hackanet.models.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Service
public class CompanyMapper implements Mapper<Company, CompanyDto> {

    @Autowired
    private SkillMapper skillMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public CompanyDto map(Company from) {
        if (from == null) {
            return null;
        }
        return CompanyDto.builder()
                .id(from.getId())
                .companyType(from.getType())
                .name(from.getName())
                .description(from.getDescription())
                .country(from.getCountry())
                .city(from.getCity())
                .technologies(skillMapper.map(from.getTechnologies()))
                .logo(fileInfoMapper.map(from.getLogo()))
                .approved(Boolean.TRUE.equals(from.getApproved()))
                .build();
    }
}
