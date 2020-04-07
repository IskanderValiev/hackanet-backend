package com.hackanet.services;

import com.hackanet.models.Position;

import java.util.List;

public interface PositionService extends RetrieveService<Position> {
    Position create(String name);
    Position update(Long id, String name);
    void delete(Long id);
    List<Position> getByNameLike(String name);
}
