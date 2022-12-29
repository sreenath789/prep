package com.ecom.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.beans.Customer;
import com.ecom.beans.CustomerCart;
import com.ecom.beans.CustomerOrder;
import com.ecom.beans.PhysicalCategory;
import com.ecom.beans.PhysicalProducts;
import com.ecom.beans.PhysicalSubCategory;
import com.ecom.repository.CustomerCartRepository;
import com.ecom.repository.CustomerOrderRepository;
import com.ecom.repository.PhysicalProductRepository;
import com.ecom.service.CustomerCartService;
import com.ecom.service.CustomerService;
import com.ecom.service.PhysicalCategoryService;
import com.ecom.service.PhysicalProductService;
import com.ecom.service.PhysicalSubCategoryService;
import com.ecom.service.SubcategoryService;

@Controller
@RequestMapping("/cart")
public class CustomerCartController {
	@Autowired
	PhysicalProductService physicalProductService;
	@Autowired
	CustomerOrderRepository customerOrderRepository;
	@Autowired
	CustomerCartService customerCartService;
	@Autowired
	CustomerService customerservice;
	@Autowired
	PhysicalCategoryService PhysicalCategoryServices;
	@Autowired
	SubcategoryService subcatagoryservice;
	@Autowired
	PhysicalProductService physicalproductservice;
	@Autowired
	PhysicalSubCategoryService PhysicalSubCategoryServices;
	@Autowired
	PhysicalProductRepository physicalProductRepository;
	@Autowired
	CustomerCartRepository customerCartRepository;

	@RequestMapping("/cartloginpage")
	public String addCartLogin(Model model, @ModelAttribute(value = "customerObject") Customer customerObject) {
		model.addAttribute("customerObject", customerObject);
		return "front-end-Cartlogin";
	}

	@RequestMapping("/frontendcart")
	public String addCart(Model model, @ModelAttribute(value = "customerObject") Customer customerObject) {
		System.out.println("l");
		model.addAttribute("customerObject", customerObject);
		return "Cartlogin";
	}

	@RequestMapping("/Cartregistration")
	public String addCartRegistration(Model model, @ModelAttribute(value = "customerObject") Customer customerObject) {
		model.addAttribute("customerObject", customerObject);
		return "front-end-Cartregister";
	}

	@RequestMapping(value = "/savecartCustomer", method = RequestMethod.POST)
	public String saveCartRegistration(Model model, @ModelAttribute(value = "customerObject") Customer customerObject) {
		if (customerObject.getConfirmPassword().equals(customerObject.getPassword())) {
			customerObject.setIsActive('y');
			Customer customer = customerservice.addCustomer(customerObject);
			customer.setCreatedBy(customer.getCustomerId());
			customer.setUpdatedBy(customer.getCustomerId());
			customerservice.addCustomer(customer);
			return "redirect:/cart/frontendcart";
		} else {
			model.addAttribute("msg", "password and confirm password should be same ");
			model.addAttribute("customerObject", customerObject);
			return "front-end-Cartregister";
		}
	}

	@GetMapping("/customercartlogin")
	public String loginValidationcart(Model model, @ModelAttribute(value = "customerObject") Customer customerObject,
			HttpServletRequest request) {
		Customer signinObj = customerservice.getCustomer(customerObject.getEmail(), customerObject.getPassword());
		if (signinObj != null) {
			signinObj.setCreated(LocalDate.now());
			signinObj.setUpdated(LocalDate.now());
			Customer customer = customerservice.addCustomer(signinObj);

			return "redirect:/cart/frontendproducts/" + signinObj.getCustomerId();
		}

		else {
			Customer cObject = new Customer();
			model.addAttribute("customerObject", cObject);
			model.addAttribute("msg", "The entered details are wrong.\t Please check your Email and password");
			return "redirect:/cart/frontendcart";
		}
	}

	@RequestMapping("/frontendproducts/{id}")
	public String method(Model model, @PathVariable(value = "id") int id) {

		Customer object = customerservice.getCustomerById(id);
		model.addAttribute("customer", object);
		List<PhysicalProducts> products = physicalProductRepository.findLatestProducts();
		model.addAttribute("product", products);

		List<PhysicalCategory> catagorylist = PhysicalCategoryServices.getAllCategory();
		model.addAttribute("catagorylist", catagorylist);

		List<PhysicalSubCategory> subcatagorylist = PhysicalSubCategoryServices.list();
		model.addAttribute("subcatagorylist", subcatagorylist);
		List<PhysicalProducts> productobject = physicalProductRepository.getActivePhysicalProducts();
		model.addAttribute("productlist", productobject);

		List<CustomerCart> list = customerCartRepository.getAllCartList(id);
		model.addAttribute("addQuatity", list.size());

		return "front-end-products";
	}

	@RequestMapping("/addtocart/{id}/{cid}")
	public String addcart(Model model, @PathVariable(value = "id") int id, CustomerCart cart,
			@PathVariable(value = "cid") int cid) {
		PhysicalProducts product = physicalProductService.getProductById(id);
		List<PhysicalProducts> physicalProducts = physicalProductRepository
				.getPhysicalProductsByModelNumber(product.getProductModelNumber());
		List<PhysicalProducts> productlistforquatity = physicalProductRepository
				.getPhysicalProductsByModelNumberForQuantity(product.getProductModelNumber());
		Customer customer = customerservice.getCustomerById(cid);

		List<CustomerCart> list = customerCartRepository.getCartActiveList(cid);
		List<CustomerCart> customerCartlist = customerCartRepository.getAllcartListforproduct(cid,
				product.getProductModelNumber());
		ArrayList<String> modelNumbers = new ArrayList<>();
		for (CustomerCart c : list) {
			modelNumbers.add(c.getProductModelNumber());

		}
		ArrayList<Integer> cartIds = new ArrayList<>();
		for (CustomerCart c : customerCartlist) {
			cartIds.add(c.getProductId());
		}
		int count=0;
		for (PhysicalProducts products : physicalProducts) {
			if (cartIds.contains(products.getProductId())) {
				count++;
			} 
		}
		if (customerCartlist.size() < productlistforquatity.size()) {
			if (modelNumbers.contains(product.getProductModelNumber())) {

				if (physicalProducts.size() > count) {
					for (PhysicalProducts products : physicalProducts) {
						if (cartIds.contains(products.getProductId())) {
							continue;
						} else {

							CustomerCart data = customerCartRepository.addQuantityofProduct(cid,
									product.getProductModelNumber());
							data.setQuantity(data.getQuantity() + 1);
							data.setTotalprice(data.getProductPrice() * (data.getQuantity()));
							customerCartRepository.save(data);

							cart.setImage(products.getProductImage());
							cart.setCustomerId(cid);
							cart.setProductId(products.getProductId());
							cart.setProductName(products.getProductName());
							cart.setProductModelNumber(products.getProductModelNumber());
							cart.setProductPrice(products.getProductMRPPrice());
							cart.setProductCompany(products.getProductCompany());
							cart.setProductCode(products.getProductCode());
							cart.setIsActive('N');
							cart.setQuantity(1);
							customerCartService.addCart(cart);
							break;

						}
					}
				} else {
					CustomerCart data = customerCartRepository.addQuantityofProduct(cid,
							product.getProductModelNumber());
					data.setQuantity(data.getQuantity() + 1);
					data.setTotalprice(data.getProductPrice() * (data.getQuantity()));
					customerCartRepository.save(data);
					cart.setImage(product.getProductImage());
					cart.setCustomerId(cid);
					cart.setProductId(product.getProductId());
					cart.setProductName(product.getProductName());
					cart.setProductModelNumber(product.getProductModelNumber());
					cart.setProductPrice(product.getProductMRPPrice());
					cart.setProductCompany(product.getProductCompany());
					cart.setProductCode(product.getProductCode());
					cart.setQuantity(1);
					cart.setIsActive('N');
					customerCartService.addCart(cart);
				}
			} else {
				if (physicalProducts.size()!=0) {
					for (PhysicalProducts products : physicalProducts) {

						cart.setImage(products.getProductImage());
						cart.setCustomerId(cid);
						cart.setProductId(products.getProductId());
						cart.setProductName(products.getProductName());
						cart.setProductModelNumber(products.getProductModelNumber());
						cart.setProductPrice(products.getProductMRPPrice());
						cart.setProductCompany(products.getProductCompany());
						cart.setProductCode(products.getProductCode());
						cart.setIsActive('Y');
						cart.setTotalprice(products.getProductMRPPrice());
						cart.setQuantity(1);
						customerCartService.addCart(cart);
						break;

					}
				} else {
					cart.setImage(product.getProductImage());
					cart.setCustomerId(cid);
					cart.setProductId(product.getProductId());
					cart.setProductName(product.getProductName());
					cart.setProductModelNumber(product.getProductModelNumber());
					cart.setProductPrice(product.getProductMRPPrice());
					cart.setProductCompany(product.getProductCompany());
					cart.setProductCode(product.getProductCode());
					cart.setIsActive('Y');
					cart.setTotalprice(product.getProductMRPPrice());
					customerCartService.addCart(cart);
				}

			}
						return "redirect:/cart/frontendproducts/"+cid;

		} else {
			Customer object = customerservice.getCustomerById(cid);
			model.addAttribute("customer", object);
			List<PhysicalProducts> products = physicalProductRepository.findLatestProducts();
			model.addAttribute("product", products);

			List<PhysicalCategory> catagorylist = PhysicalCategoryServices.getAllCategory();
			model.addAttribute("catagorylist", catagorylist);

			List<PhysicalSubCategory> subcatagorylist = PhysicalSubCategoryServices.list();
			model.addAttribute("subcatagorylist", subcatagorylist);
			List<PhysicalProducts> productobject = physicalProductRepository.getActivePhysicalProducts();
			model.addAttribute("productlist", productobject);

			List<CustomerCart> list1 = customerCartRepository.getAllCartList(cid);
			model.addAttribute("addQuatity", list1.size());
			model.addAttribute("msg",
					product.getProductName() + " Only limited products are available " + productlistforquatity.size());
			return "front-end-products";
		}

	}

	@RequestMapping("/cartList/{cid}")
	public String cartList(Model model, CustomerCart cart, @PathVariable("cid") int cid) {
		float totalcartprice = 0;
		List<CustomerCart> list = customerCartRepository.getCartActiveList(cid);
		Customer data = customerservice.getCustomerById(cid);

		// String totalcartprice=customerCartRepository.getcarttotal(cid);
		if (customerCartRepository.getcarttotal(cid) != null) {
			totalcartprice = Float.parseFloat(customerCartRepository.getcarttotal(cid));
		} else {
			totalcartprice = 0;
		}
		model.addAttribute("totalcartprice", totalcartprice);
		model.addAttribute("Cart", list);
		model.addAttribute("customer", data);

		return "cart";
	}

	@RequestMapping("/deleteCart/{id}/{cid}")
	public String deleteCart(Model model, @PathVariable("id") int id, @PathVariable("cid") int cid) {
		CustomerCart cart = customerCartRepository.getById(id);
		List<CustomerCart> QuantityList = customerCartRepository.DeleteQuantityofProduct(cid,
				cart.getProductModelNumber());
		if (QuantityList.size() != 0) {
			for (CustomerCart cartProduct : QuantityList) {
				customerCartService.deleteBydataId(cartProduct.getCartId());
				break;
			}

			cart.setQuantity(cart.getQuantity() - 1);
			cart.setTotalprice(cart.getTotalprice() - cart.getProductPrice());
			customerCartRepository.save(cart);
		} else {
			customerCartRepository.deleteById(id);
		}
		return "redirect:/cart/cartList/" + cid;

	}

	@RequestMapping("/addquatity/{id}/{cid}")
	public String totalprice(Model model, @PathVariable(value = "id") int id, @PathVariable(value = "cid") int cid,
			@RequestParam("quatity") int quantity) {

		int h = 0;
		Customer customer = customerservice.getCustomerById(cid);

		if (quantity > 0) {
			CustomerCart cartId = customerCartService.getCartById(id);
			int q = quantity + cartId.getQuantity();
			List<PhysicalProducts> modelnumberlist = physicalProductRepository
					.getPhysicalProductsByModelNumber(cartId.getProductModelNumber());
			List<CustomerCart> customerCartlist = customerCartRepository.getAllcartListforproduct(cid,
					cartId.getProductModelNumber());
			ArrayList<Integer> cartIds = new ArrayList<>();
			for (CustomerCart c : customerCartlist) {
				cartIds.add(c.getProductId());
			}
			if (q < modelnumberlist.size()) {
				for (PhysicalProducts product : modelnumberlist) {
					for (int i = h; i < quantity; i++) {
						if (cartIds.contains(product.getProductId())) {
							continue;
							
						} else {
							CustomerCart cart = new CustomerCart();
							cart.setImage(product.getProductImage());
							cart.setProductId(product.getProductId());
							cart.setProductName(product.getProductName());
							cart.setProductModelNumber(product.getProductModelNumber());
							cart.setProductPrice(product.getProductMRPPrice());
							cart.setProductCompany(product.getProductCompany());
							cart.setProductCode(product.getProductCode());
							cart.setIsActive('N');
							cart.setCustomerId(cid);
							customerCartService.addCart(cart);
							i = quantity + 1;
							h++;
						}
					}

				}

				model.addAttribute("msg", "only " + " " + modelnumberlist.size() + " are avaliable");

				Double total = cartId.getTotalprice() * q;
				cartId.setTotalprice(total);
				cartId.setQuantity(cartId.getQuantity() + quantity);
				customerCartService.addCart(cartId);
			}
			float totalcartprice = 0;
			List<CustomerCart> list = customerCartRepository.getCartActiveList(customer.getCustomerId());
			Customer data = customerservice.getCustomerById(cid);

			if (customerCartRepository.getcarttotal(cid) != null) {
				totalcartprice = Float.parseFloat(customerCartRepository.getcarttotal(cid));
			} else {
				totalcartprice = 0;
			}
			model.addAttribute("totalcartprice", totalcartprice);
			model.addAttribute("Cart", list);
			model.addAttribute("customer", data);
			model.addAttribute("msg1", "");

			return "cart";

		} else {
			float totalcartprice = 0;
			List<CustomerCart> list = customerCartRepository.getCartActiveList(customer.getCustomerId());
			Customer data = customerservice.getCustomerById(cid);

			if (customerCartRepository.getcarttotal(cid) != null) {
				totalcartprice = Float.parseFloat(customerCartRepository.getcarttotal(cid));
			} else {
				totalcartprice = 0;
			}
			model.addAttribute("totalcartprice", totalcartprice);
			model.addAttribute("Cart", list);
			model.addAttribute("customer", data);
			model.addAttribute("msg2", "Invailed quantity entered");
			return "cart";

		}
	}

}
