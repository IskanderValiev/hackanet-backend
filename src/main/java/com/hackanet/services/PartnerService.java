package com.hackanet.services;

import com.hackanet.json.forms.PartnerCreateForm;
import com.hackanet.models.Partner;

import java.util.Set;

public interface PartnerService extends CrudService<Partner> {
    Partner create(PartnerCreateForm form);
    Set<Partner> getByIds(Set<Long> ids);
    void delete(Long id);
}
