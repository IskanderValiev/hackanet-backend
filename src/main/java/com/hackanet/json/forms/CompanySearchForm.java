package com.hackanet.json.forms;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hackanet.models.enums.CompanyType;
import lombok.Data;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/7/19
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanySearchForm {
    private String name;
    private String country;
    private String city;
    private List<Long> technologies;
    private CompanyType companyType;
    private Integer limit;
    private Integer page;
}
