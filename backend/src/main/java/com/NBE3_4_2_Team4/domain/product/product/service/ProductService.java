package com.NBE3_4_2_Team4.domain.product.product.service;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto.GetItems> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductResponseDto.GetItems(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()))
                .toList();

    }

    @Transactional(readOnly = true)
    public PageDto<ProductResponseDto.GetItems> getProducts(int page, int pageSize) {

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.findAll(pageRequest);

        return new PageDto<>(products.map(product -> new ProductResponseDto.GetItems(
                product,
                makeFullCategory(product),
                product.getSaleState().getName()
        )));
    }

    private String makeFullCategory(Product product) {

        StringBuilder sb = new StringBuilder();

        ProductCategory category = product.getCategory();

        // 상위 카테고리가 없을 때까지 카테고리 추가
        while (category != null) {
            sb.insert(0, category.getName() + "/");
            category = category.getParent();
        }

        // 마지막 '/' 문자 제거
        if (!sb.isEmpty() && sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
