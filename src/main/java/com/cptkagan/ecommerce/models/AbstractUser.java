package com.cptkagan.ecommerce.models;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;

import com.cptkagan.ecommerce.enums.UserRoles;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String firstName;

    private String lastName;

    private Boolean gender; // 1 for male, 0 for female, null for other or not specified

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String userName;

    private String password;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT", length = 500)
    private String address;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    Boolean isActivated = false;


    public AbstractUser(String firstName, String lastName, Boolean gender, String email,
                        String userName, String password, UserRoles role, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.userName = userName;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.role = role;
        this.phoneNumber = phoneNumber;
        if(address != null) {
            this.address = address;
        }
        else{
            this.address = "";
        }
    }

    public Boolean isEligibleForLogin(){ // Check if account is Activated or not (Login Workflow)
        return this.getIsActivated();
    }
}
