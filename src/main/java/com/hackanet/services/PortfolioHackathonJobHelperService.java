package com.hackanet.services;

import com.hackanet.models.hackathon.Hackathon;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/27/20
 */
public interface PortfolioHackathonJobHelperService {
    void addHackathonJob(Long userId, Hackathon hackathon);
}
