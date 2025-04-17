package com.cptkagan.ecommerce.DTOs.responseDTO;

import com.cptkagan.ecommerce.models.Seller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponseAdmin {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;
    private String email;
    private String userName;
    private String phoneNumber;
    private String companyName;
    private String companyDescription;
    private String taxNumber;


    public SellerResponseAdmin(Seller seller){
        this.id = seller.getId();
        this.firstName = seller.getFirstName();
        this.lastName = seller.getLastName();
        this.address = seller.getAddress();
        if(seller.getGender()){
            this.gender = "Male";
        }
        else if(!seller.getGender()){
            this.gender = "Female";
        }
        if(seller.getGender() == null){
            this.gender = "Other";
        }

        this.email = seller.getEmail();
        this.userName = seller.getUserName();
        this.phoneNumber = seller.getPhoneNumber();
        this.companyName = seller.getCompanyName();
        this.companyDescription = seller.getCompanyDescription();
        this.taxNumber = seller.getTaxNumber();
    }
}
