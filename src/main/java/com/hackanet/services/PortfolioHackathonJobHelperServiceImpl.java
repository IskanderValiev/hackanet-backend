package com.hackanet.services;

import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.Portfolio;
import com.hackanet.services.hackathon.HackathonJobDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/27/20
 */
@Service
public class PortfolioHackathonJobHelperServiceImpl implements PortfolioHackathonJobHelperService {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private HackathonJobDescriptionService hackathonJobDescriptionService;

    @Override
    public void addHackathonJob(Long userId, Hackathon hackathon) {
        Portfolio portfolio = portfolioService.getByUserId(userId);
        hackathonJobDescriptionService.createEmptyDescriptionWithHackathon(portfolio, hackathon);
    }
}
