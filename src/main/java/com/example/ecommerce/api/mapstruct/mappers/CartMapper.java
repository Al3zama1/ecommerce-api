package com.example.ecommerce.api.mapstruct.mappers;

import com.example.ecommerce.api.entity.Cart;
import com.example.ecommerce.api.mapstruct.dto.cart.CartItemDto;
import org.mapstruct.Mapper;


@Mapper
public interface CartMapper {

    CartItemDto mapCartItemToDto(Cart cart);

}
