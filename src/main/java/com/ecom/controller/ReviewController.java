package com.ecom.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ecom.beans.Customer;
import com.ecom.beans.CustomerCart;
import com.ecom.beans.DigitalProducts;
import com.ecom.beans.PhysicalProducts;
import com.ecom.beans.PhysicalReview;
import com.ecom.beans.Review;
import com.ecom.repository.DigitalProductsRepository;
import com.ecom.repository.PhysicalReviewRepository;
import com.ecom.repository.ReviewRepository;
import com.ecom.service.CustomerService;
import com.ecom.service.PhysicalProductService;
import com.ecom.service.ReviewService;

@Controller
@RequestMapping("/Reviews")
public class ReviewController {
	@Autowired
	ReviewService ReviewService;
	@Autowired
	PhysicalProductService physicalproductservice;
	@Autowired
	CustomerService customerservice;
	@Autowired
	ReviewRepository ReviewRepository;

	@Autowired
	PhysicalReviewRepository physicalReviewRepository;

	@Autowired
	DigitalProductsRepository digitalProductsRepository;

	@RequestMapping("/addReview/{pid}")
	public String AddReview(Model model, PhysicalReview reviewObject, @PathVariable("pid") int pid) {
		PhysicalProducts productlist = physicalproductservice.getProductById(pid);

		reviewObject.setIsActive('Y');
		reviewObject.setProductId(reviewObject.getProductId());
		model.addAttribute("reviewObject", reviewObject);
		model.addAttribute("productlist", productlist);
		return "add-review";
	}

	@RequestMapping("/saveReviews/{pid}")
	public String SaveReviews(Model model, PhysicalReview Object, @PathVariable("pid") int pid) {

		PhysicalProducts physicalProducts = physicalproductservice.getProductById(pid);
		Object.setCreated(LocalDateTime.now());
		Object.setIsActive('Y');
		Object.setProductId(pid);
		// ReviewService.addReviewDetails(Object);
		physicalReviewRepository.save(Object);
		return "redirect:/Reviews/viewreviews/" + physicalProducts.getProductId();
	}

	@RequestMapping("/viewreviews/{pid}")
	public String viewReviews(Model model, @PathVariable("pid") int pid) {
		List<Review> Review = ReviewRepository.findAllRatingsInDescOrder();

		List<PhysicalReview> list = physicalReviewRepository.getPhysicalReviewByProductId(pid);

		model.addAttribute("Review", list);
		return "view-review";
	}

	@RequestMapping("/addDigitalReview/{pid}")
	public String AddDigitalReview(Model model, Review reviewObject, @PathVariable("pid") int pid) {
		DigitalProducts productlist = digitalProductsRepository.getById(pid);

		reviewObject.setIsActive('Y');
		reviewObject.setProductId(productlist.getProductId());
		model.addAttribute("reviewObject", reviewObject);
		model.addAttribute("productlist", productlist);
		return "add-digital-review";
	}

	@RequestMapping("/saveDigitalReviews/{pid}")
	public String SaveDigitalReviews(Model model, Review Object, @PathVariable("pid") int pid) {
		DigitalProducts digitalProducts = digitalProductsRepository.getById(pid);
		Object.setCreated(LocalDateTime.now());
		Object.setIsActive('Y');
		Object.setProductId(pid);

		ReviewService.addReviewDetails(Object);
		return "redirect:/Reviews/viewdigitalreviews/" + digitalProducts.getProductId();
	}

	@RequestMapping("/viewdigitalreviews/{pid}")
	public String viewDigitalReviews(Model model, @PathVariable("pid") int pid) {
		// DigitalProducts digitalProducts =digitalProductsRepository.getById(pid);

		List<Review> Review = ReviewRepository.findAllRatingsInDescOrder();

		List<Review> list = ReviewRepository.getReviewByProductId(pid);

		model.addAttribute("Review", list);

		return "view-digital-review";
	}

}
