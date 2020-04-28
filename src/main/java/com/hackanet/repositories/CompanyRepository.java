package com.hackanet.repositories;

import com.hackanet.models.Company;
import com.hackanet.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByAdmin(User user);
    List<Company> findAllByNameLike(String name);
}
