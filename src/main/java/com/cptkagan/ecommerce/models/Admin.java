package com.cptkagan.ecommerce.models;

import com.cptkagan.ecommerce.enums.UserRoles;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ecommerceadmins")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends AbstractUser{
    public Admin(String firstName, String lastName, Boolean gender, String email, String userName, String password, String phoneNumber){
        super(firstName, lastName, gender, email, userName, password, UserRoles.ROLE_ADMIN, phoneNumber, null);
    }
}
