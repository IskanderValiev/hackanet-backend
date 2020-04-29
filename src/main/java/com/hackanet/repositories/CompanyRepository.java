package com.hackanet.repositories;

import com.hackanet.models.Company;
import com.hackanet.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByAdmin(User user);

    @Query("SELECT c FROM Company c WHERE (c.approved = TRUE or c.type = 'ADDED_BY_NAME') and lower(c.name) like %:name% ")
    List<Company> findAllByNameLike(@Param("name") String name);
}
