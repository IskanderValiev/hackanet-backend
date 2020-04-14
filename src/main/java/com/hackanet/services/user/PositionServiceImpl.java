package com.hackanet.services.user;

import com.hackanet.exceptions.AlreadyExistException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.user.Position;
import com.hackanet.repositories.user.PositionRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/7/20
 */
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Override
    public Position create(String name) {
        checkIfExists(name);
        Position position = Position.builder()
                .name(StringUtils.capitalize(name.toLowerCase()))
                .build();
        return positionRepository.save(position);
    }

    @Override
    public Position update(Long id, String name) {
        checkIfExists(name);
        Position position = get(id);
        position.setName(StringUtils.capitalize(name.toLowerCase()));
        return positionRepository.save(position);
    }

    @Override
    public void delete(Long id) {
        Position position = get(id);
        positionRepository.delete(position);
    }

    @Override
    public List<Position> getByNameLike(String name) {
        String capitalizedName = StringUtils.capitalize(name.toLowerCase());
        return positionRepository.findAllByNameContaining(capitalizedName);
    }

    @Override
    public Position get(Long id) {
        if (id == null) {
            return null;
        }
        return positionRepository.findById(id)
                .orElseThrow(() -> NotFoundException.throwNFE(Position.class, "id", id));
    }

    @Override
    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    private void checkIfExists(String name) {
        getByName(name)
                .ifPresent((position) -> AlreadyExistException.throwException(position.getClass(), "name", name));
    }

    private Optional<Position> getByName(String name) {
        return positionRepository.findByName(StringUtils.capitalize(name.toLowerCase()));
    }
}
