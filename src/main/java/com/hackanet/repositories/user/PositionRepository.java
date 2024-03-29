package com.hackanet.repositories.user;

import com.hackanet.models.user.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name);
    List<Position> findAllByNameContaining(String name);
}
