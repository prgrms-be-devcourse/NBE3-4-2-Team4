package com.NBE3_4_2_Team4.domain.product.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.controller.GenFileController
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile
import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/product/genFile")
class ProductGenFileController(productService: ProductService) :
    GenFileController<Product, ProductGenFile>(productService) {
    override fun findById(id: Long): Product {
        return (service as ProductService).findById(id)
    }

    override fun getGenFileFromParent(parent: Product, fileName: String): ProductGenFile {
        return parent.genFiles
            .stream()
            .filter { it.fileName == fileName }
            .findFirst()
            .get()
    }
}
