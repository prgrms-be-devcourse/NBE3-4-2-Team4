package com.NBE3_4_2_Team4.domain.product.genFile.controller;


import com.NBE3_4_2_Team4.domain.base.genFile.controller.ApiGenFileController;
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{parentId}/genFiles")
public class ApiProductGenFileController extends ApiGenFileController<Product, ProductGenFile> {
    public ApiProductGenFileController(ProductService productService) {
        super(productService);
    }

    @Override
    protected Product findById(long id) {
        return ((ProductService) service).findById(id);
    }
}
