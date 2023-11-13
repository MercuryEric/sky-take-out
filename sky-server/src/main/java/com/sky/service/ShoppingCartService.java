package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ShoppingCartService {

    /**
     * 购物车添加
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     */
    List<ShoppingCart> showShoppingCart();
    /**
     * 清空购物车
     *
     * @return
     */
    void cleanShoppingCart();
    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     * @return
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
