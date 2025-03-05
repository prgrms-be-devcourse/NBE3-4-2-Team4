package com.NBE3_4_2_Team4.domain.asset.main.repository;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminAssetCategoryRepository extends JpaRepository<AdminAssetCategory, Long> {
    public boolean existsByName(String name);
    public List<AdminAssetCategory> findAllByIsDisabledFalse();
    public Optional<AdminAssetCategory> findByName(String name);
}
