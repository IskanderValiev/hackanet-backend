package com.hackanet.services;

import com.hackanet.json.dto.CompanyOwnerTokenDto;
import com.hackanet.json.forms.CompanyCreateForm;
import com.hackanet.json.forms.CompanySearchForm;
import com.hackanet.json.forms.CompanyUpdateForm;
import com.hackanet.models.Company;
import com.hackanet.models.User;

import java.util.List;

public interface CompanyService extends ManageableService<Company>, FormValidatorService {
    CompanyOwnerTokenDto registerCompany(CompanyCreateForm form);
    Company updateApprovedStatus(Long id, Boolean approved);
    List<Company> getCompaniesList(CompanySearchForm form);
    Company createCompanyByName(String name, String country, String city);
    Company getByAdmin(User admin);
}
