package com.NBE3_4_2_Team4.domain.asset.main.service;

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryRes;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAssetCategoryService {

    private final AdminAssetCategoryRepository adminAssetCategoryRepository;

    @Transactional
    public AdminAssetCategory create(String name) {
        if (adminAssetCategoryRepository.existsByName(name))
                throw new ServiceException("404-1", "이미 존재하는 이름입니다");

        AdminAssetCategory adminAssetCategory = new AdminAssetCategory(name);
        return adminAssetCategoryRepository.save(adminAssetCategory);
    }

    @Transactional(readOnly = true)
    public List<AdminAssetCategoryRes> findAll() {
        return adminAssetCategoryRepository.findAllByIsDisabledFalse()
                .stream()
                .map(AdminAssetCategoryRes::from)
                .toList();
    }

    @Transactional
    public AdminAssetCategory updateName(long id, String newName) {
        AdminAssetCategory adminAssetCategory = adminAssetCategoryRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 id 입니다"));
        adminAssetCategory.setName(newName);
        return adminAssetCategoryRepository.save(adminAssetCategory);
    }

    @Transactional
    public AdminAssetCategory disable(long id) {
        AdminAssetCategory adminAssetCategory = adminAssetCategoryRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 id 입니다"));
        adminAssetCategory.setDisabled(true);
        return adminAssetCategoryRepository.save(adminAssetCategory);
    }

    public long count() {
        return adminAssetCategoryRepository.count();
    }
}
