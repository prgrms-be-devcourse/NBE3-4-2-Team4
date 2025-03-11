package com.NBE3_4_2_Team4.domain.product.product.entity

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile
import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.*

@Entity
class Product : GenFileParent<ProductGenFile, Product> {

    @Column(nullable = false)
    lateinit var name: String                                       // 상품명

    @Column(nullable = false)
    var price: Int = 0                                              // 상품 가격

    @Column(columnDefinition = "TEXT")
    lateinit var description: String                                // 상품 설명

    @Column(nullable = false)
    lateinit var imageUrl: String                                   // 상품 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var category: ProductCategory                          // 상품 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var saleState: ProductSaleState                        // 상품 판매 상태

    @OneToMany(mappedBy = "product")
    val productOrders: MutableList<ProductOrder> = mutableListOf()  // 주문 리스트

    constructor() : super(ProductGenFile::class.java)

    constructor(
        name: String,
        price: Int,
        description: String,
        imageUrl: String,
        category: ProductCategory,
        saleState: ProductSaleState,
        productOrders: MutableList<ProductOrder> = mutableListOf()
    ) : super(ProductGenFile::class.java) {
        this.name = name
        this.price = price
        this.description = description
        this.imageUrl = imageUrl
        this.category = category
        this.saleState = saleState
        this.productOrders.addAll(productOrders)
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun updatePrice(price: Int) {
        this.price = price
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun updateImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun updateCategory(category: ProductCategory) {
        this.category = category
    }

    fun updateSaleState(saleState: ProductSaleState) {
        this.saleState = saleState
    }

    override fun modify(content: String) {
        updateDescription(content)
    }

    override val content: String
        get() = this.description

    override fun checkActorCanMakeNewGenFile(actor: Member) {
        Optional.of(
            getCheckActorCanMakeNewGenFileRs(actor)
        )
            .filter { obj: RsData<Empty> -> obj.isFail }
            .ifPresent { rsData: RsData<Empty> ->
                throw ServiceException(rsData.resultCode, rsData.msg)
            }
    }

    override fun getCheckActorCanMakeNewGenFileRs(actor: Member): RsData<Empty> {
        if (actor == null) return RsData("401-1", "로그인 후 이용해주세요.")
        if (actor.role == Member.Role.ADMIN) return RsData.OK
        return RsData("403-1", "작성자만 파일을 업로드할 수 있습니다.")
    }
}