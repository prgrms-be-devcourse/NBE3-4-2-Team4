package com.NBE3_4_2_Team4.domain.asset.factory;

import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class AssetServiceFactory {
    private final Map<String, AssetService> serviceMap;
    private final static String SUFFIX = "Service";

    @Autowired
    public AssetServiceFactory(List<AssetService> services) {
        serviceMap = new HashMap<>();
        for (AssetService service : services) {
            String name = AopProxyUtils.ultimateTargetClass(service).getSimpleName();

            if (!isValidName(name))
                throw new IllegalArgumentException("잘못된 이름 형식의 AssetService 입니다: %s".formatted(name));

            String key = getKeyString(name);
            serviceMap.put(key, service);
        }
    }


    public AssetService getService(String type) {
        AssetService assetService = serviceMap.get(type);
        if (assetService == null) throw new IllegalArgumentException("%s인 AssetService 이름이 존재하지 않습니다".formatted(type) + type);
        return assetService;
    }

    public int getSize() {
        return serviceMap.size();
    }

    // Service를 포함하지 않은 이름
    private String getKeyString(String name) {
        return name.substring(0, name.length() - SUFFIX.length());
    }

    // "key"+"Service" 형식의 이름인지 확인하는 메소드
    private boolean isValidName(String name) {
        return name.endsWith(SUFFIX);
    }
}
