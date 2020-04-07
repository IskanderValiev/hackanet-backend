package com.hackanet.services;

import com.hackanet.json.dto.VkUniversityResponse;

public interface UniversityService {
    VkUniversityResponse.VkUniversityListDto getUniversity(String query);
}
