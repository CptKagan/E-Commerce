package com.cptkagan.ecommerce.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.cptkagan.ecommerce.DTOs.BuyerRegisterRequest;
import com.cptkagan.ecommerce.enums.UserRoles;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Buyer extends AbstractUser {
    public Buyer(BuyerRegisterRequest dto, String password) {
        super(dto.getFirstName(), dto.getLastName(), dto.getGender(),
              dto.getEmail(), dto.getUserName(), password, UserRoles.ROLE_BUYER, dto.getPhoneNumber(), null);
    }
}