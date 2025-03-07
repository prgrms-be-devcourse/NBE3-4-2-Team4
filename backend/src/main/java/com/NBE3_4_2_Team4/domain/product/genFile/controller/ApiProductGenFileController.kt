package com.NBE3_4_2_Team4.domain.product.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.controller.ApiGenFileController
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile
import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/products/{parentId}/genFiles")
class ApiProductGenFileController(productService: ProductService) :
    ApiGenFileController<Product, ProductGenFile>(productService) {
    override fun findById(id: Long): Product {
        return (service as ProductService).findById(id)
    }
}
