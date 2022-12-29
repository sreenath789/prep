package com.ecom.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties.Storage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.beans.PhysicalProducts;
import com.ecom.beans.Store;
import com.ecom.repository.StoreRepository;
import com.ecom.service.StoreService;

@Controller
@RequestMapping("/store")
public class StoreController {
	
    @Autowired
   //StoreRepository storeRepository;
    StoreService storeService;
     
	@RequestMapping("/indexStore")
	public String getIndex(Model model,@ModelAttribute(value="objStore")  Store store,HttpSession session) {
		model.addAttribute("objStore", store);
		 
		return "Add-Store";
		
	}
	

	@RequestMapping("/AddStore")
	public String addBill(Model model, Store store,HttpSession session) {
		 
    
		Store st=storeService.saveStore(store);
		model.addAttribute("objStore", st);
		return "Add-Store";
	}

	@RequestMapping("/Storelist")
	public String storeList(Model model)
	{
		List<Store> storeobject=storeService.getAllStores();
		model.addAttribute("storeList", storeobject);
		
		 StringBuffer obj = new StringBuffer();
		 int i=1;
			
			for (Store a : storeobject) {
				System.out.println(a.getLatitude() + "hi" + a.getLongitude());
				//obj.append("['" + a.getBranchName() + "'," + a.getLatitude() + "," + a.getLongitude() + "," + i + "],");
				 obj.append("[\"" +a.getStoreName() + "\"," + a.getLatitude() + "," + a.getLongitude() + "," + 1 + "],");
				i++;
			}
			 
	 	model.addAttribute("branchesGMap", obj.toString());
		return "redirect:/";
		
	}
	
	
	@RequestMapping("/storelist")
	public String totalbranchList(Model model) {
		List<Store> storeList = storeService.getAllStores();
		 

		int count =storeList.size();
		List<Store> storeObj = new ArrayList<>();
		for (int i = 0; i < count; i++) {

			int storeid = storeList.get(i).getStoreID();
			 Store store=storeService.getStoreById(storeid);
			Store store1 = storeService.getStoreById( store.getStoreID());
			store1.setStoreName(store1.getStoreName());
			storeObj.add(store1);

		}
		model.addAttribute("brancheList", storeObj);
		return "/";
	}
	
	

}
 
    
    

