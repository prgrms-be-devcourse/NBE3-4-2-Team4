package com.NBE3_4_2_Team4.domain.product.genFile.controller;

import com.NBE3_4_2_Team4.domain.base.genFile.controller.GenFileController;
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product/genFile")
public class ProductGenFileController extends GenFileController<Product, ProductGenFile> {
    public ProductGenFileController(ProductService productService) {
        super(productService);
    }

    @Override
    protected Product findById(long id) {
        return ((ProductService) service).findById(id);
    }

    @Override
    protected ProductGenFile getGenFileFromParent(Product parent, String fileName) {
        return parent.getGenFiles()
                .stream()
                .filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .get();
    }
}
