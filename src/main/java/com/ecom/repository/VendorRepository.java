package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecom.beans.Admin;
import com.ecom.beans.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

	@Query(value = "SELECT vendor_id, company_name, created, created_by, email, file,is_active, mobile_number,password, store_location, updated, updated_by, vendor_type\r\n"
			+ "	FROM public.vendor where vendor_type='PhysicalProductsVendor'",nativeQuery = true)
	List<Vendor> findPhysicalproductNames();
	
	@Query(value = "SELECT vendor_id, file,company_name, created, created_by, email, is_active, mobile_number,password, store_location, updated, updated_by, vendor_type\r\n"
			+ "	FROM public.vendor where vendor_type='DigitalProductsVendor'",nativeQuery=true)
	List<Vendor> finddigitalproductNames();
	

	@Query(value="select a1 from vendor a1 WHERE lower(a1.email)=lower(?1)",nativeQuery = true)
			public Vendor findByEmailId(String email);

	@Query(value="SELECT vendor_id, company_name, created, created_by, email, is_active, file,mobile_number, password, store_location, updated, updated_by, vendor_type\r\n"
			+ "	FROM public.vendor\r\n"
			+ "	where email=?1 and password=?2",nativeQuery = true)
	Vendor findByUsernameIgnoreCaseAndPassword(String email, String password);
	
	 
}
