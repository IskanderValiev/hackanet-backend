package com.hackanet.services;

import com.hackanet.models.JobOffer;
import com.hackanet.models.user.User;

import java.util.List;

public interface JobOfferService extends RetrieveService<JobOffer> {
    JobOffer create(User admin, Long userId);
    List<JobOffer> getByUserId(Long userId);
    JobOffer deleteForUser(User user, Long id);
    void delete(User user, Long id);
    void accept(User user, Long id);
}
