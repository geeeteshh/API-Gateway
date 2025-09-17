package com.example.product_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductsController {

    // This is a public endpoint
    @GetMapping("/public")
    public String getPublicProducts() {
        return "This is a public list of products. Anyone can access it!";
    }

    // This is a secured endpoint that requires authentication
    @GetMapping("/secured")
    public String getSecuredProducts() {
        return "This is a secured list of products. Only authenticated users can see this.";
    }

    @GetMapping("/user")
    public String getUserProducts() {
        return "This is a user-specific list of products. Only users with the 'ROLE_USER' role can see this.";
    }

    @GetMapping("/admin")
    public String getAdminProducts() {
        return "This is a admin-specific list of products. Only admin with the 'ROLE_ADMIN' role can see this.";
    }

    @GetMapping("/huhh")
    public String gethuhh() {
        return "This is a admin-specific list of products. Only admin with the 'ROLE_ADMIN' role can see this.";
    }
}
