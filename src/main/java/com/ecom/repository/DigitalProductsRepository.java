package com.ecom.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.beans.DigitalProducts;
import com.ecom.beans.PhysicalProducts;
@Repository
public interface DigitalProductsRepository extends JpaRepository<DigitalProducts, Integer> {
 
@Query(value="select * from digital_products p where p.product_name like %:keyword%", nativeQuery = true)

 	 List<DigitalProducts> findByKeyword(@Param("keyword") String keyword);

@Query(value="SELECT product_id, mrpprice,photos, created, created_by, description, discount_price, images, is_active, product_category, product_code, product_details, product_name, product_subcategory, rating, set_history_path, size, updated, updated_by, vendor\r\n"
		+ "	FROM public.digital_products",nativeQuery = true)
 List<DigitalProducts> findAll();


@Query(value="SELECT product_id, mrpprice, created, created_by, description, discount_price, images, is_active, photos, product_category, product_code, product_details, product_name, product_subcategory, rating, set_history_path, size, updated, updated_by, vendor\r\n"
		+ "	FROM public.digital_products where product_subcategory=?1",nativeQuery = true)
 List<DigitalProducts> findProductsBySubcategories(String id);


@Query(value="SELECT product_id, mrpprice, created, created_by, description, discount_price, images, is_active, photos, product_category, product_code, product_details, product_name, product_subcategory, rating, set_history_path, size, updated, updated_by, vendor\r\n"
		+ " FROM public.digital_products where created=current_date",nativeQuery=true)

List<DigitalProducts> findLatestProducts(LocalDate now);

//List<DigitalProducts> getLatestProducts(LocalDate now);
 
 
  }
