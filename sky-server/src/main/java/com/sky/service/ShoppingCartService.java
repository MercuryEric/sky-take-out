package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import org.springframework.stereotype.Service;


public interface ShoppingCartService {

    /**
     * 购物车添加
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
