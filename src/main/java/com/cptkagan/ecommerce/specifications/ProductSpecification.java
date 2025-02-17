package com.cptkagan.ecommerce.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.cptkagan.ecommerce.models.Product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

 /**
 * Root is used to access columns of the table as said before. 
 * It works like that:
 * root.get("category") -> SELECT category FROM product
 * root.get("category"), category = "Electronics" -> Select category FROM product WHERE category = 'Electronics'
 
 * CriteriaQuery:
 * Defines what we are selecting (Product? other things?)
 * ORDER BY, GROUP BY, DISTINCT, HAVING, JOIN, etc.
 * Modify the query before execution
 
 * CriteriaBuilder:
 * Generates SQL conditions like WHERE, AND, OR
 * .equal -> WHERE category = 'Electronics'
 * .greaterThan -> AND / WHERE price > 100
 * .lessThan -> AND / WHERE price < 100
 * .like -> WHERE name LIKE '%Samsung%'
 * Also it can handle Range Queries like BETWEEN
 * query.orderBy(criteriaBuilder.asc(root.get("price"))); -> ORDER BY price ASC
 */

 /**
 * - Root<T> root: Respresents the table (product) in the database. Used access to the columns
 * <p>
 * - CriteriaQuery<?> query: Represents the entire SQL query
 * <p>
 * - CriteriaBuilder criteriaBuilder: Helps build SQL conditions like WHERE, AND, OR
 */

public class ProductSpecification {
    public static Specification<Product> hasCategory(String category){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                if(category == null || category.isEmpty()){
                    return criteriaBuilder.conjunction(); // category sağlanmamışsa kriter yok.
                }
                return criteriaBuilder.equal(root.get("category"), category);
            }
        };
    }

    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                if(minPrice == null && maxPrice == null){
                    return criteriaBuilder.conjunction(); // minPrice ve maxPrice sağlanmamışsa kriter yok.
                }
                else if(minPrice != null && maxPrice != null){
                    return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
                }
                else if(minPrice != null){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
                }
                else{
                    return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
                }
            }
        };
    }

    public static Specification<Product> isInStock(Boolean inStock){
        return new Specification<Product>(){
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                if(inStock == null){
                    return criteriaBuilder.conjunction(); // inStock sağlanmamışsa kriter yok.
                }
                if(inStock){
                    return criteriaBuilder.greaterThan(root.get("stockQuantity"), 0);
                }
                else{
                    return criteriaBuilder.equal(root.get("stockQuantity"), 0);
                }
            }
        };
    }

    public static Specification<Product> hasSeller(Long sellerId){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                if(sellerId == null){
                    return criteriaBuilder.conjunction(); // sellerId sağlanmamışsa kriter yok.
                }
                return criteriaBuilder.equal(root.get("seller").get("id"), sellerId);
            }
        };
    }

    public static Specification<Product> hasName(String name){
        return new Specification<Product>(){
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                if(name == null || name.isEmpty()){
                    return criteriaBuilder.conjunction(); // name sağlanmamışsa kriter yok.
                }
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
        };
    }



}