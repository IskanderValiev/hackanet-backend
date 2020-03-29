package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.SponsorCreateForm;
import com.hackanet.json.forms.SponsorUpdateForm;
import com.hackanet.models.FileInfo;
import com.hackanet.models.User;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.Sponsor;
import com.hackanet.repositories.SponsorRepository;
import com.hackanet.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/23/20
 */
@Service
public class SponsorServiceImpl implements SponsorService {

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private FileInfoService fileInfoService;

    @Override
    public Sponsor create(User user, SponsorCreateForm form) {
        SecurityUtils.isAdmin(user);
        Hackathon hackathon = hackathonService.get(form.getHackathonId());
        FileInfo fileInfo = fileInfoService.get(form.getLogoId());
        Sponsor sponsor = Sponsor.builder()
                .hackathon(hackathon)
                .logo(fileInfo)
                .name(form.getName())
                .link(form.getLink())
                .build();
        return sponsorRepository.save(sponsor);
    }

    @Override
    public Sponsor update(User user, Long id, SponsorUpdateForm form) {
        Sponsor sponsor = checkAccess(user, id);

        sponsor = Sponsor.builder()
                .name(form.getName())
                .logo(fileInfoService.get(form.getLogoId()))
                .link(form.getLink())
                .build();
        return sponsorRepository.save(sponsor);
    }

    @Override
    public void delete(User user, Long id) {
        Sponsor sponsor = checkAccess(user, id);
        sponsorRepository.delete(sponsor);
    }

    @Override
    public List<Sponsor> getByHackathonId(Long hackathonId) {
        return sponsorRepository.findAllByHackathonId(hackathonId);
    }

    @Override
    public Sponsor get(Long id) {
        return sponsorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sponsor with id = " + id + " not found"));
    }

    private Sponsor checkAccess(User user, Long id) {
        SecurityUtils.isAdmin(user);
        Sponsor sponsor = get(id);
        SecurityUtils.checkHackathonAccess(sponsor.getHackathon(), user);
        return sponsor;
    }
}
