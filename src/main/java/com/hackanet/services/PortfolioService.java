package com.hackanet.services;

import com.hackanet.json.forms.PortfolioUpdateForm;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Portfolio;
import com.hackanet.models.User;

public interface PortfolioService extends CrudService<Portfolio> {
    Portfolio getByUserId(Long userId);
    Portfolio update(Long id, User user, PortfolioUpdateForm form);
    Portfolio addHackathonJob(Long userId, Hackathon hackathon);
}
