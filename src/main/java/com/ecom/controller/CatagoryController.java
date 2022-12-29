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
import com.ecom.repository.AdminRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.SubcategoryService;

@Controller
@RequestMapping("/catagory")
public class CatagoryController {

	@Autowired
	CategoryService catagoryservice;
	@Autowired
	SubcategoryService subcatagoryservice;
	@Autowired
	AdminRepository adminRepository;

	@RequestMapping("/catagorylist/{eid}")
	public String addCatagory(Model model, Category catagory,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		List<Category> catagorylist = catagoryservice.getAllCategory();
		model.addAttribute("catagorylist", catagorylist);
		model.addAttribute("catagory", catagory);
		return "category";
	}

	@RequestMapping("/savecatagory/{eid}")
	public String saveCatagory(Category catagory,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		catagoryservice.addCategory(catagory);
		return "redirect:/catagory/catagorylist/"+eid;
	}

	@GetMapping("/deleteCategory/{id}/{eid}")
	public String deleteCategory(Model model, @PathVariable("id") int id,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		model.addAttribute("admin", admin);

		System.out.println("ssss");
		catagoryservice.deleteCategoryById(id);

		return "redirect:/catagory/catagorylist/"+eid;

	}

	@GetMapping("/editcategory/{id}/{eid}")
	public String EditBycategoryid(Model model, @PathVariable("id") int id,@PathVariable("eid")int eid) {
		Admin admin=adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		Category obj = catagoryservice.getCategoryById(id);
		
		model.addAttribute("categorieslist", obj);
		
		return "EditCategory";
	}

}
