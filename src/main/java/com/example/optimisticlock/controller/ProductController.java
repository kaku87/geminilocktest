package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.Product;
import com.example.optimisticlock.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
// 製品エンティティ向けのRESTコントローラ。CrudControllerのデフォルト実装を利用する。
public class ProductController extends GlobalExceptionHandler implements CrudController<Product> {

    @Autowired
    // Product専用のサービス。実装はSpringにより注入される。
    private ProductService productService;

    @GetMapping("/{tenantId}/{id}")
    /**
     * テナントIDと製品IDを指定して単一の製品を取得する。
     * @param tenantId テナント識別子
     * @param id 製品ID
     * @return 該当する製品エンティティ
     */
    public Product getProduct(@PathVariable String tenantId, @PathVariable Long id) {
        return productService.getProduct(id, tenantId);
    }



    @Override
    /**
     * CrudControllerの基底実装が利用するサービスインスタンスを返却する。
     * @return ProductServiceのインスタンス
     */
    public ProductService getService() {
        return productService;
    }
}
