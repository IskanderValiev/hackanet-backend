package com.hackanet.services;

import com.hackanet.json.forms.PortfolioUpdateForm;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.user.Portfolio;
import com.hackanet.models.user.User;

public interface PortfolioService extends RetrieveService<Portfolio> {
    Portfolio getByUserId(Long userId);
    Portfolio update(Long id, User user, PortfolioUpdateForm form);
}
