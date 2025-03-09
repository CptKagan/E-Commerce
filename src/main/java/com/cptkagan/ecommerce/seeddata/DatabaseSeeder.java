package com.cptkagan.ecommerce.seeddata;

import com.cptkagan.ecommerce.enums.UserRoles;
import com.cptkagan.ecommerce.models.*;
import com.cptkagan.ecommerce.repositories.AdminRepository;
import com.cptkagan.ecommerce.repositories.BuyerRepository;
import com.cptkagan.ecommerce.repositories.ProductRepository;
import com.cptkagan.ecommerce.repositories.SellerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final SellerRepository sellerRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public DatabaseSeeder(SellerRepository sellerRepository, BuyerRepository buyerRepository, 
                          ProductRepository productRepository, PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.sellerRepository = sellerRepository;
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        seedSellersAndProducts();
        seedBuyers();
        seedAdmins();
    }

    private void seedSellersAndProducts() {
        if (sellerRepository.count() == 0) {
            Seller seller1 = new Seller();
            seller1.setFirstName("John");
            seller1.setLastName("Doe");
            seller1.setGender(true);
            seller1.setEmail("mkagankayabasi@gmail.com");
            seller1.setUserName("seller1");
            seller1.setPassword(passwordEncoder.encode("password"));
            seller1.setRole(com.cptkagan.ecommerce.enums.UserRoles.ROLE_SELLER);
            seller1.setPhoneNumber("1234567890");
            seller1.setAddress("123 Main St");
            seller1.setCompanyName("Doe Electronics");
            seller1.setTaxNumber("TAX12345");
            seller1.setCompanyDescription("Best electronics in town.");
            seller1.setCreatedAt(LocalDateTime.now());
            seller1.setIsActivated(true);

            Seller seller2 = new Seller();
            seller2.setFirstName("Jane");
            seller2.setLastName("Smith");
            seller2.setGender(false);
            seller2.setEmail("flowerybeggar@gmail.com");
            seller2.setUserName("seller2");
            seller2.setPassword(passwordEncoder.encode("password"));
            seller2.setRole(com.cptkagan.ecommerce.enums.UserRoles.ROLE_SELLER);
            seller2.setPhoneNumber("0987654321");
            seller2.setAddress("456 Market St");
            seller2.setCompanyName("Smith Supplies");
            seller2.setTaxNumber("TAX67890");
            seller2.setCompanyDescription("Quality office supplies.");
            seller2.setCreatedAt(LocalDateTime.now());
            // seller2.setIsActivated(true);

            sellerRepository.saveAll(List.of(seller1, seller2));

            createProductsForSeller(seller1);
            createProductsForSeller(seller2);

            System.out.println("Sellers and products seeded.");
        }
    }

    private void createProductsForSeller(Seller seller) {
        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setName("Product " + i + " - " + seller.getCompanyName());
            product.setDescription("This is product " + i + " from " + seller.getCompanyName());
            product.setPrice(10 + (random.nextDouble() * 90)); // Random price between 10-100
            product.setStockQuantity(random.nextInt(50) + 10); // Stock between 10-50
            product.setCategory(i % 2 == 0 ? "Electronics" : "Office Supplies");
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            product.setSeller(seller);

            productRepository.save(product);
        }
    }

    private void seedBuyers() {
        if (buyerRepository.count() == 0) {
            Buyer buyer1 = new Buyer();
            buyer1.setFirstName("Yasin");
            buyer1.setLastName("Yılmaz");
            buyer1.setGender(false);
            buyer1.setEmail("mkagankayabasi@gmail.com");
            buyer1.setUserName("buyer1");
            buyer1.setPassword(passwordEncoder.encode("password"));
            buyer1.setRole(com.cptkagan.ecommerce.enums.UserRoles.ROLE_BUYER);
            buyer1.setPhoneNumber("5551112222");
            buyer1.setAddress("789 Oak St");
            buyer1.setCreatedAt(LocalDateTime.now());
            buyer1.setIsActivated(true);

            Buyer buyer2 = new Buyer();
            buyer2.setFirstName("Bob");
            buyer2.setLastName("Williams");
            buyer2.setGender(true);
            buyer2.setEmail("flowerybeggar@gmail.com");
            buyer2.setUserName("buyer2");
            buyer2.setPassword(passwordEncoder.encode("password"));
            buyer2.setRole(com.cptkagan.ecommerce.enums.UserRoles.ROLE_BUYER);
            buyer2.setPhoneNumber("5553334444");
            buyer2.setAddress("321 Pine St");
            buyer2.setCreatedAt(LocalDateTime.now());
            buyer2.setIsActivated(true);

            buyerRepository.saveAll(List.of(buyer1, buyer2));

            System.out.println("Buyers seeded.");
        }
    }

    private void seedAdmins(){
        if(adminRepository.count() == 0){
            Admin admin1 = new Admin();
            admin1.setFirstName("Admin1");
            admin1.setLastName("Admin1");
            admin1.setGender(true);
            admin1.setEmail("mkagankayabasi3@gmail.com");
            admin1.setUserName("admin1");
            admin1.setPassword(passwordEncoder.encode("password"));
            admin1.setRole(UserRoles.ROLE_ADMIN);
            admin1.setPhoneNumber("5444235336");
            admin1.setAddress("321 Pine St.");
            admin1.setCreatedAt(LocalDateTime.now());
            admin1.setIsActivated(true);

            Admin admin2 = new Admin();
            admin2.setFirstName("Admin2");
            admin2.setLastName("Admin2");
            admin2.setGender(true);
            admin2.setEmail("oyunlaraverbunu3152@gmail.com");
            admin2.setUserName("admin2");
            admin2.setPassword(passwordEncoder.encode("password"));
            admin2.setRole(UserRoles.ROLE_ADMIN);
            admin2.setPhoneNumber("5444235336");
            admin2.setAddress("321 Pine St.");
            admin2.setCreatedAt(LocalDateTime.now());
            admin2.setIsActivated(true);

            adminRepository.saveAll(List.of(admin1, admin2));

            System.out.println("Admins are seeded.");
        }
    }
}