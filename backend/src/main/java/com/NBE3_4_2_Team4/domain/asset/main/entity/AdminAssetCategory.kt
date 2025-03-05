package com.NBE3_4_2_Team4.domain.asset.main.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class AdminAssetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @Column(nullable = false)
    var isDisabled : Boolean = false

    //TODO: 디폴트 파라미터 추가
    constructor(name: String) {
        this.name = name
    }
}