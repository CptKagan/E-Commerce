package com.cptkagan.ecommerce.models;

import java.util.List;

import com.cptkagan.ecommerce.DTOs.requestDTO.SellerRegisterRequest;
import com.cptkagan.ecommerce.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Seller extends AbstractUser{
    private String companyName;

    private String taxNumber;

    @Column(columnDefinition = "TEXT", length = 500)
    private String companyDescription;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Product> products;

    private Boolean isApprovedByAdmin = false;

    public Seller(SellerRegisterRequest dto, String password) {
        super(dto.getFirstName(), dto.getLastName(), dto.getGender(),
              dto.getEmail(), dto.getUserName(), password, UserRoles.ROLE_SELLER, dto.getPhoneNumber(),
              dto.getAddress());
              this.companyDescription = dto.getCompanyDescription();
              this.companyName = dto.getCompanyName();
              this.taxNumber = dto.getTaxNumber();
    }

    @Override
    public Boolean isEligibleForLogin(){ // Check if account is Activated & Approved by an Admin or not (Login Workflow)
        return this.getIsActivated() && this.getIsApprovedByAdmin();
    }

}
