package com.NBE3_4_2_Team4.domain.asset.factory

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType.Companion.fromValue
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AssetServiceFactory (
    private val services: List<AssetService>
) {
    private val serviceMap: EnumMap<AssetType, AssetService> = EnumMap(AssetType::class.java)

    init {
        for (service in services) {
            val name = AopProxyUtils.ultimateTargetClass(service).simpleName

            require(isValidName(name)) { "잘못된 이름 형식의 AssetService 입니다: %s".format(name) }

            val key = getKeyString(name)
            serviceMap[fromValue(key)] = service
        }
    }


    fun getService(type: AssetType): AssetService {
        val assetService = serviceMap[type]
        requireNotNull(assetService) { "%s인 AssetService 이름이 존재하지 않습니다".format(type) }
        return assetService
    }

    val size: Int
        get() = serviceMap.size

    // Service를 포함하지 않은 이름
    private fun getKeyString(name: String): String {
        return name.substring(0, name.length - SUFFIX.length).uppercase(Locale.getDefault())
    }

    // "key"+"Service" 형식의 이름인지 확인하는 메소드
    private fun isValidName(name: String): Boolean {
        return name.endsWith(SUFFIX)
    }

    companion object {
        private const val SUFFIX = "Service"
    }
}
