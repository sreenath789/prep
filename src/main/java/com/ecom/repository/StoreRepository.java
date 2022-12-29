package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.beans.Store;

public interface StoreRepository extends JpaRepository<Store,Integer>{

	List<Store> getAllByStoreID(int storeId);

 

}
