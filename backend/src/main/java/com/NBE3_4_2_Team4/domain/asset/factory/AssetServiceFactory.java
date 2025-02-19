package com.NBE3_4_2_Team4.domain.asset.factory;

import com.NBE3_4_2_Team4.domain.asset.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class AssetServiceFactory {
    private final Map<String, AssetService> serviceMap;

    @Autowired
    public AssetServiceFactory(List<AssetService> services) {
        serviceMap = services.stream()
                .collect(Collectors.toMap(s -> s.getClass().getSimpleName(), Function.identity()));
    }

    public AssetService getService(String type) {
        AssetService assetService = serviceMap.get(type);
        if (assetService == null) throw new IllegalArgumentException("%s인 AssetService 이름이 존재하지 않습니다".formatted(type) + type);
        return assetService;
    }

    public int getSize() {
        return serviceMap.size();
    }
}
