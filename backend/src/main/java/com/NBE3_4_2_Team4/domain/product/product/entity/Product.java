package com.NBE3_4_2_Team4.domain.product.product.entity;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile;
import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class Product extends GenFileParent<ProductGenFile> {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;                        // 상품 아이디

    @Column(nullable = false)
    private String name;                    // 상품명

    @Column(nullable = false)
    private int price;                      // 상품 가격

    @Column(columnDefinition = "text")
    private String description;             // 상품 설명

    @Column(nullable = false)
    private String imageUrl;                // 상품 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductCategory category;       // 상품 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductSaleState saleState;     // 상품 판매 상태

    @OneToMany(mappedBy = "product")        // 주문 리스트
    private List<ProductOrder> productOrders;

    public Product() {
        super(ProductGenFile.class);
    }

    public Product(String name, int price, String description, String imageUrl, ProductCategory category, ProductSaleState saleState, List<ProductOrder> productOrders) {
        super(ProductGenFile.class);
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.saleState = saleState;
        this.productOrders = productOrders;
    }


    public void updateName(String name) {
        this.name = name;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateCategory(ProductCategory category) {
        this.category = category;
    }

    public void updateSaleState(ProductSaleState saleState) {
        this.saleState = saleState;
    }

    @Override
    public void modify(String description) {
        updateDescription(description);
    }

    @Override
    public String getContent() {
        return this.description;
    }

    @Override
    public void checkActorCanMakeNewGenFile(Member actor) {
        Optional.of(
                        getCheckActorCanMakeNewGenFileRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getResultCode(), rsData.getMsg());
                });
    }

    @Override
    protected RsData<Empty> getCheckActorCanMakeNewGenFileRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");
        if (actor.getRole() == Member.Role.ADMIN) return RsData.OK;
        return new RsData<>("403-1", "관리자만 파일을 업로드할 수 있습니다.");
    }
}
