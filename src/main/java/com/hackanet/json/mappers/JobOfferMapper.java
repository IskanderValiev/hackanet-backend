package com.hackanet.json.mappers;

import com.hackanet.json.dto.JobOfferDto;
import com.hackanet.models.JobOffer;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/28/19
 */
@Component
public class JobOfferMapper implements Mapper<JobOffer, JobOfferDto> {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public JobOfferDto map(JobOffer from) {
        if (from == null) {
            return null;
        }
        return JobOfferDto.builder()
                .id(from.getId())
                .time(DateTimeUtil.localDateTimeToLong(from.getTime()))
                .company(companyMapper.map(from.getCompany()))
                .userSimple(userSimpleMapper.map(from.getUser()))
                .build();
    }
}
