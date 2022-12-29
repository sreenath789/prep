package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.beans.Admin;
import com.ecom.beans.Category;
import com.ecom.beans.PhysicalCategory;
import com.ecom.repository.AdminRepository;
import com.ecom.service.PhysicalCategoryService;
import com.ecom.service.SubcategoryService;
@Controller
@RequestMapping("/PhysicalCatagory")
public class PhysicalCatagoryController {

	@Autowired
	PhysicalCategoryService PhysicalCategoryServices;
	@Autowired
	SubcategoryService subcatagoryservice;
	@Autowired
	AdminRepository adminRepository;
	@RequestMapping("/catagorylist/{eid}")
	public String addCatagory(Model model,Category catagory,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		List<PhysicalCategory> catagorylist=PhysicalCategoryServices.getAllCategory();
		model.addAttribute("catagorylist", catagorylist);
		model.addAttribute("catagory", catagory);
		return "Physicalcategory";
	}
	@RequestMapping("/savecatagory/{eid}")
	public String saveCatagory(PhysicalCategory catagory,@PathVariable("eid")int eid) {
		
		PhysicalCategoryServices.addCategory(catagory);
		return"redirect:/PhysicalCatagory/catagorylist/"+eid;
	}
	@GetMapping("/deleteCategory/{id}/{eid}")
	public String deleteCategory(@PathVariable("id") int id,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		

		System.out.println("ssss");
		PhysicalCategoryServices.deleteCategoryById(id);
		
		return "redirect:/PhysicalCatagory/catagorylist/"+eid;

	}
	@GetMapping("/editcategory/{id}/{eid}")
	public String EditBycategoryid(Model model, @PathVariable("id") int id,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		PhysicalCategory obj = PhysicalCategoryServices.getCategoryById(id);
		//List<Category> catagory=service.getAllCategoryDetails();
		model.addAttribute("categorieslist", obj);
		//model.addAttribute("catagory", catagory);
		return "EditPhysicalCategory";
	}

}
