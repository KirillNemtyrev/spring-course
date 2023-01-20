package com.community.server.repository;

import com.community.server.entity.BackPackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BackPackRepository extends JpaRepository<BackPackEntity, Long> {
    List<BackPackEntity> findByUserId(Long id);
    Optional<BackPackEntity> findByUserIdAndCompanyId(Long user, Long company);
}
