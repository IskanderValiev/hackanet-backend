package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.HackathonCreateForm;
import com.hackanet.json.forms.HackathonUpdateForm;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.User;
import com.hackanet.repositories.HackathonRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

import static com.hackanet.security.utils.SecurityUtils.checkHackathonAccess;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/19/19
 */
@Service
public class HackathonServiceImpl implements HackathonService {

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private SkillService skillService;

    @Override
    public List<Hackathon> getAll() {
        return hackathonRepository.findAll();
    }

    /**
     *
     * The method is used to add new hackathon.
     *
     * Before the request reaches the controller, ability of creating a new hackathon is checked by
     * @PreAuthorize
     *
     * @throws BadRequestException
     *      if start date is after end date
     * */
    @Override
    public Hackathon save(User user, HackathonCreateForm form) {
        Date start = form.getStart();
        Date end = form.getEnd();
        if (start.after(end))
            throw new BadRequestException("Start date is after end date");
        Hackathon hackathon = Hackathon.builder()
                .name(form.getName().trim())
                .nameLc(form.getName().trim().toLowerCase())
                .startDate(start)
                .endDate(end)
                .owner(user)
                .logo(fileInfoService.get(form.getLogoId()))
                .description(form.getDescription().trim())
                .country(StringUtils.capitalize(form.getCountry()))
                .city(StringUtils.capitalize(form.getCity()))
                .requiredSkills(skillService.getByIdsIn(form.getRequiredSkills()))
                .build();
        hackathon = hackathonRepository.save(hackathon);
        return hackathon;
    }

    @Override
    public Hackathon get(Long id) {
        return hackathonRepository.findById(id).orElseThrow(() -> NotFoundException.forHackathon(id));
    }

    /**
     * Updates information about hackathon.
     * If string fields are not empty and are not null, fields will be updated
     *
     * @throws com.hackanet.exceptions.ForbiddenException
     *      if user is not an owner of the hackathon
     *
     * */
    @Override
    public Hackathon update(Long id, User user, HackathonUpdateForm form) {
        Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);

        Date start = form.getStart();
        Date end = form.getEnd();
        if (start != null && end != null) {
            if (start.after(end)) {
                throw new BadRequestException("Start date is after end date");
            }
        }
        if (form.getLogo() != null) {
            FileInfo file = fileInfoService.get(form.getLogo());
            hackathon.setLogo(file);
        }
        if (start != null) {
            if (start.before(new Date(System.currentTimeMillis()))) {
                throw new BadRequestException("Start date is in the past");
            }
            hackathon.setStartDate(start);
        }
        if (end != null)
            hackathon.setEndDate(end);

        String name = form.getName();
        if (!StringUtils.isBlank(name)) {
            hackathon.setName(name.trim());
            hackathon.setNameLc(form.getName().trim().toLowerCase());
        }

        String description = form.getDescription();
        if (!StringUtils.isBlank(description))
            hackathon.setDescription(description.trim());

        String country = StringUtils.capitalize(form.getCountry());
        if (!StringUtils.isBlank(country))
            hackathon.setCountry(country);

        String city = StringUtils.capitalize(form.getCity());
        if (!StringUtils.isBlank(city))
            hackathon.setCity(city);

        List<Long> requiredSkills = form.getRequiredSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            hackathon.setRequiredSkills(skillService.getByIdsIn(requiredSkills));
        }
        hackathon = hackathonRepository.save(hackathon);
        return hackathon;
    }

    @Override
    public void delete(Long id, User user) {
        Hackathon hackathon = get(id);
        checkHackathonAccess(hackathon, user);
        hackathonRepository.delete(hackathon);
    }
}
