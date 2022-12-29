package com.ecom.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecom.beans.PhysicalProducts;
@Repository
public interface PhysicalProductRepository extends JpaRepository<PhysicalProducts, Integer> {
	
	@Query("Select p from PhysicalProducts p where p.created=current_date and (lower(p.isactive)='y' or lower(p.isactive)='Y')")
	List<PhysicalProducts> findLatestProducts();
	@Query("select al from PhysicalProducts al where al.productModelNumber=?1 and  al.isactive='N'")
	List<PhysicalProducts> getPhysicalProductsByModelNumber(String model);
	@Query("select al from PhysicalProducts al where al.productModelNumber=?1 and  (al.isactive='N' or al.isactive='Y')")
	List<PhysicalProducts> getPhysicalProductsByModelNumberForQuantity(String model);
	@Query("select al from PhysicalProducts al where lower(al.isactive)='y' or lower(al.isactive)='Y'")
	List<PhysicalProducts> getActivePhysicalProducts();
	
	@Query(value="SELECT product_id, product_sub_category, qrcode, created, createdby, isactive, product_category, product_code, product_company, product_description, product_details, product_discount_price, product_image, productmrpprice, product_model_number, product_name, product_rating, product_shipping_information, product_size, product_specification, product_video, updated, updatedby\r\n"
			+ "	FROM public.physical_products\r\n"
			+ "	where product_id!=?1 and (product_company=?2 or product_discount_price=?3) ",nativeQuery = true)
	List<PhysicalProducts> compareProducts(int id,String brand,double price);
	@Query("Select isactive from PhysicalProducts p where lower(p.isactive)=lower(?1)")
	List<Character> getlist(char isactive);
	@Query("select al from PhysicalProducts al where lower(al.isactive)='Y' or lower(al.isactive)='N'")
	List<PhysicalProducts> leftProducts();
	@Query("select al from PhysicalProducts al where lower(al.productModelNumber)=lower(?1)")
	List<PhysicalProducts> getPhysicalProductsByModelNumberForadding(String model);
	@Query("select al from PhysicalProducts al where lower(al.productModelNumber)=lower(?1) and al.isactive='Y'")
	List<PhysicalProducts> getPhysicalProductsForadding(String model);
	@Query("select sum(productMRPPrice) from PhysicalProducts al where al.isactive='S'")
	public String findAllorderproductsPrice();
	@Query(value = "Select p from PhysicalProducts p where p.created=?1 and al.isactive='Y'",nativeQuery=true)
	List<PhysicalProducts> findLatestProducts(LocalDate current_date);
	
	@Query(value = "SELECT product_id, product_sub_category, qrcode, created, createdby, isactive, product_category, product_code, product_company, product_description, product_details, product_discount_price, product_image, productmrpprice, product_model_number, product_name, product_rating, product_shipping_information, product_size, product_specification, product_video, updated, updatedby\r\n"
			+ "	FROM public.physical_products where product_sub_category=?1 and isactive='Y'" ,nativeQuery=true)
	List<PhysicalProducts> findProductsBySubcategories(String id);
	@Query("select al from PhysicalProducts al where lower(al.productModelNumber)=lower(?1) and al.isactive='Y'")
	PhysicalProducts getPhysicalProductsForaddingMultipleImages(String model);
}
