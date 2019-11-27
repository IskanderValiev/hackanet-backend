package com.hackanet.services;

import com.google.common.collect.Sets;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.PartnerCreateForm;
import com.hackanet.models.Partner;
import com.hackanet.repositories.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/27/19
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private FileInfoService fileInfoService;

    @Override
    public Partner create(PartnerCreateForm form) {
        Partner partner = Partner.builder()
                .link(form.getLink())
                .name(form.getName())
                .logo(fileInfoService.get(form.getLogoId()))
                .build();
        return partnerRepository.save(partner);
    }

    @Override
    public Set<Partner> getByIds(Set<Long> ids) {
        Set<Partner> partners = partnerRepository.findByIdIn(ids);
        return partners == null ? Sets.newHashSet() : partners;
    }

    @Override
    public Partner get(Long id) {
        return partnerRepository.findById(id).orElseThrow(() -> new NotFoundException("Partner with id=" + id + " not found"));
    }

    @Override
    public List<Partner> getAll() {
        return partnerRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        partnerRepository.deleteById(id);
    }
}
