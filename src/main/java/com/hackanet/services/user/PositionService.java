package com.hackanet.services.user;

import com.hackanet.models.user.Position;
import com.hackanet.services.RetrieveService;

import java.util.List;

public interface PositionService extends RetrieveService<Position> {
    Position create(String name);
    Position update(Long id, String name);
    void delete(Long id);
    List<Position> getByNameLike(String name);
}
