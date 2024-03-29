package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.PortfolioUpdateForm;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.Portfolio;
import com.hackanet.models.user.User;
import com.hackanet.repositories.PortfolioRepository;
import com.hackanet.services.hackathon.HackathonJobDescriptionService;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    //Spring uses a proxy instead of the real object at the injection point. This proxy delays the initialization of the underlying object until it is first used.
    @Lazy
    private HackathonJobDescriptionService hackathonJobDescriptionService;

    /**
     * The method provides functionality of getting portfolio by user id.
     * <p>
     * If portfolio with user id passed to the method does not exists,
     * a new portfolio for the user is created.
     *
     * @param userId - id of user
     */
    @Override
    public Portfolio getByUserId(Long userId) {
        Optional<Portfolio> byUserId = portfolioRepository.findByUserId(userId);
        if (byUserId.isPresent()) {
            return byUserId.get();
        }
        Portfolio portfolio = Portfolio.builder()
                .user(userService.get(userId))
                .build();
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
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
    public Portfolio get(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> NotFoundException.throwNFE(Portfolio.class, "id", id));
    }
}
