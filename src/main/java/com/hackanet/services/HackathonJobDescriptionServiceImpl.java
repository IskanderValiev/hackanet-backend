package com.hackanet.services;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.json.forms.HackathonJobDescriptionCreateForm;
import com.hackanet.models.*;
import com.hackanet.repositories.HackathonJobDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/8/19
 */
@Service
public class HackathonJobDescriptionServiceImpl implements HackathonJobDescriptionService {

    @Autowired
    private HackathonJobDescriptionRepository hackathonJobDescriptionRepository;
    @Autowired
    private HackathonService hackathonService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private PortfolioService portfolioService;

    @Override
    public HackathonJobDescription createForPortfolio(HackathonJobDescriptionCreateForm form) {
        Hackathon hackathon = hackathonService.get(form.getHackathonId());
        Team team = teamService.get(form.getTeamId());

        Long userId = form.getUserId();
        List<Long> collect = team.getParticipants().stream().map(User::getId).filter(id -> Objects.equals(id, userId)).collect(Collectors.toList());
        if (collect.isEmpty())
            throw new BadRequestException("The user is not in the team");

        HackathonJobDescription hackathonJobDescription = HackathonJobDescription.builder()
                .description(form.getDescription())
                .portfolio(portfolioService.getByUserId(userId))
                .hackathon(hackathon)
                .team(team)
                .build();

        return hackathonJobDescriptionRepository.save(hackathonJobDescription);
    }

    @Override
    public HackathonJobDescription createEmptyDescriptionWithHackathon(Portfolio portfolio, Hackathon hackathon) {
        HackathonJobDescription jobDescription = HackathonJobDescription.builder()
                .hackathon(hackathon)
                .portfolio(portfolio)
                .build();

        return hackathonJobDescriptionRepository.save(jobDescription);
    }
}
