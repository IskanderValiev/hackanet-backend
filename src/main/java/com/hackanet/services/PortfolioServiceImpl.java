package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.PortfolioUpdateForm;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.Portfolio;
import com.hackanet.models.User;
import com.hackanet.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.hackanet.security.utils.SecurityUtils.checkPortfolioAccess;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JobExperienceService jobExperienceService;
    @Autowired
    private HackathonJobDescriptionService hackathonJobDescriptionService;

    /**
     * The method provides functionality of getting portfolio by user id.
     *
     * If portfolio with user id passed to the method does not exists,
     * a new portfolio for the user is created.
     *
     * @param userId - id of user
     * */
    @Override
    public Portfolio getByUserId(Long userId) {
        Optional<Portfolio> byUserId = portfolioRepository.findByUserId(userId);
        if (byUserId.isPresent())
            return byUserId.get();

        Portfolio portfolio = Portfolio.builder()
                .user(userService.get(userId))
                .build();

        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio update(Long id, User user, PortfolioUpdateForm form) {
        Portfolio portfolio = get(id);
        checkPortfolioAccess(portfolio, user);

        List<Long> jobExperience = form.getJobExperience();
        if (jobExperience != null && !jobExperience.isEmpty()) {
            portfolio.setJobExperience(jobExperienceService.getByIdsIn(jobExperience));
        }
        portfolioRepository.save(portfolio);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio addHackathonJob(Long userId, Hackathon hackathon) {
        Portfolio byUserId = getByUserId(userId);
        hackathonJobDescriptionService.createEmptyDescriptionWithHackathon(byUserId, hackathon);
        return new Portfolio();
    }

    @Override
    public Portfolio get(Long id) {
        return portfolioRepository.findById(id).orElseThrow(() -> new NotFoundException("Portfolio with id = " + id + " not found"));
    }
}
