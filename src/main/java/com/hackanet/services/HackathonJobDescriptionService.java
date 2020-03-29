package com.hackanet.services;

import com.hackanet.json.forms.HackathonJobDescriptionCreateForm;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.HackathonJobDescription;
import com.hackanet.models.Portfolio;

public interface HackathonJobDescriptionService {
    HackathonJobDescription createForPortfolio(HackathonJobDescriptionCreateForm form);
    HackathonJobDescription createEmptyDescriptionWithHackathon(Portfolio portfolio, Hackathon hackathon);
}
