package com.ecom.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.beans.Admin;
import com.ecom.beans.Customer;
import com.ecom.beans.CustomerAddress;
import com.ecom.beans.CustomerOrder;
import com.ecom.beans.PhysicalProducts;
import com.ecom.repository.AdminRepository;
import com.ecom.repository.CustomerOrderRepository;
import com.ecom.repository.PhysicalProductRepository;
import com.ecom.service.AdminService;
import com.ecom.service.CustomerAddressService;
import com.ecom.service.CustomerOrderService;
import com.ecom.service.CustomerService;
import com.ecom.service.PhysicalProductService;
import com.ecom.utilities.Utilities;

@Controller
@RequestMapping("/emp")
public class AdminController {

	@Autowired
	AdminService adminService;
	@Autowired
	CustomerService customerService;
	@Autowired
	CustomerAddressService customerAddressService;
	@Autowired
	PhysicalProductService physicalProductService;

	@Autowired
	AdminRepository adminRepository;
	@Autowired
	CustomerOrderRepository customerOrderRepository;
	@Autowired
	PhysicalProductRepository physicalProductRepository;

	@RequestMapping(value = "/back-end")
	public String getBackEndIndex(Model model, @ModelAttribute(value = "employee") Admin admin) {

		return "back-end-index-login";

	}

	@RequestMapping(value = "/back-end-Reg")
	public String getBackEndReg(Model model, @ModelAttribute(value = "employee") Admin admin) {

		return "back-end-Register";

	}

	@RequestMapping(value = "/saveEmployee", method = RequestMethod.POST)
	public String addEmployee(Model model, @ModelAttribute(value = "employee") Admin admin, HttpSession session) {

		Admin adminObj = adminService.findByEmailId(admin.getEmployeeMail());

		if (adminObj != null) {
			session.setAttribute("adminObj", adminObj);
			String errorMessage = "User Already Exists!";
			model.addAttribute("errorMessage", errorMessage);

			return "back-end-Register";
		} else {

			String strEncPassword = Utilities.getEncryptSecurePassword(admin.getEmployeePassword(), "Ecom");
			admin.setEmployeePassword(strEncPassword);

			admin.setIsActive('Y');
			admin.setCreated(LocalDate.now());
			admin.setUpdated(LocalDate.now());
			admin.setCreatedBy(admin.getEmployeeId());
			admin.setUpdatedBy(admin.getEmployeeId());
			Admin adminobject = adminService.addEmployeeDetails(admin);

			return "redirect:/emp/employee/" + adminobject.getEmployeeId();

		}
	}

	@RequestMapping(value = "/newemployeelogin")

	public String newemployeelogin(Model model, Admin admin, HttpServletRequest request, HttpSession session1) {

		Admin adminobject = adminService.getEmployeeDetails(admin.getEmployeeMail(), admin.getEmployeePassword());
		if (adminobject != null) {

			return "redirect:/emp/employee/" + adminobject.getEmployeeId();
		} else {
			Admin adminobjectone = new Admin();
			System.out.println(adminobjectone);
			String errorMessage = "Invalid Credentials!";
			model.addAttribute("errorMessage", errorMessage);
			return "back-end-index-login";
		}
	}

	@RequestMapping("/employee/{eid}")
	public String employee(Model model, @PathVariable("eid") int eid) {
		Admin adminobject = adminRepository.getById(eid);
		model.addAttribute("admin", adminobject);
		int sum = 0;
		if (adminRepository.findAllPhysicalproductsPrice() != null) {
			Double count = Double.valueOf(adminRepository.findAllPhysicalproductsPrice());
			model.addAttribute("count", count);
		} else {
			model.addAttribute("count", sum);
		}
		if (adminRepository.findAllDigitalproductsPrice() != null) {
			Double DigitalCount = Double.valueOf(adminRepository.findAllDigitalproductsPrice());
			model.addAttribute("DigitalCount", DigitalCount);
		} else {
			model.addAttribute("DigitalCount", sum);
		}
		if (physicalProductRepository.findAllorderproductsPrice() != null) {
			Double cartcount = Double.valueOf(physicalProductRepository.findAllorderproductsPrice());
			model.addAttribute("cartcount", cartcount);
		} else {
			model.addAttribute("cartcount", sum);
		}
		if (adminRepository.findAllLatestproductsPrice() != null) {
			Double latestproductsprice = Double.valueOf(adminRepository.findAllLatestproductsPrice());
			model.addAttribute("latestproductsprice", latestproductsprice);
		} else {
			model.addAttribute("latestproductsprice", sum);
		}
		List<CustomerOrder> orderobj = customerOrderRepository.findAll();
		model.addAttribute("orderobj", orderobj);
		List<CustomerOrder> ordersobj = customerOrderRepository.findLatestOrders();
		model.addAttribute("ordersobj", ordersobj);
		List<Admin> adminobj = adminRepository.findAll();
		model.addAttribute("adminobj", adminobj);

		return "back-end-dashboard";
	}

	@RequestMapping("/EditProfile/{id}")
	public String EditEmployee(Model model, @PathVariable("id") int employeeId) {

		Admin admin = adminRepository.getById(employeeId);
		model.addAttribute("admin", admin);

		return "edit-profile";
	}

	@RequestMapping("/profile")
	public String profile(Model model, @RequestParam("file") MultipartFile file,
			@ModelAttribute(value = "Adminobject") Admin admin) throws Exception {

		if (file.getOriginalFilename() == "") {

			admin.setImage(admin.getImage());
		} else {

			admin.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
		}
		admin.setIsActive('Y');
		admin.setCreated(LocalDate.now());
		admin.setUpdated(LocalDate.now());
		Admin adminobject = adminService.addEmployeeDetails(admin);
		model.addAttribute("adminobject", adminobject);

		return "redirect:/emp/employeeProfile/" + adminobject.getEmployeeId();
	}

	@RequestMapping("/employeeProfile/{eid}")
	public String employeeProfile(Model model, @PathVariable("eid") int eid) {
		Admin adminobject = adminRepository.getById(eid);
		model.addAttribute("admin", adminobject);

		return "backend-profile";
	}

	@GetMapping("/delete-profile/{id}")
	public String deleteProfile(Model model, @PathVariable("id") int id) {

		Admin admin = adminService.getEmployeeDetailsById(id);
		admin.setIsActive('N');
		adminService.addEmployeeDetails(admin);

		return "redirect:/emp/back-end";

	}

	@GetMapping("/create")
	public String createUser() {
		return "back-end-create-user";
	}

	@GetMapping(value = "/adminlist/{eid}")
	public String getAllAdmins(Model model, HttpSession session, @PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);

		List<Admin> list = adminService.getAllEmployeeDetails();

		List<Admin> adminlist = new ArrayList<>();
		if (list.size() != 0) {
			for (Admin admins : list) {
				if (admins.getIsActive() == 'Y' || admins.getIsActive() == 'y') {
					adminlist.add(admins);
				}
			}
		}
		model.addAttribute("adminlist", adminlist);

		return "admin-list";

	}

	@GetMapping(value = "/userlist/{eid}")
	public String getAllUsers(Model model, HttpSession session, @PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		List<Customer> list = customerService.getAllCustomerRegistration();
		List<Customer> userlist = new ArrayList<>();
//		if(list.size()!=0) {
//		for (Customer customer : list) {
//			if (customer.getIsActive() == 'Y' || customer.getIsActive() == 'y') {
//				userlist.add(customer);
//			}
//		}
//		}
		model.addAttribute("userlist", list);

//		Admin sessionAdmin = (Admin) session.getAttribute("adminObj");
//		if (sessionAdmin == null) {
//			Admin admin = adminService.getEmployeeDetailsById(1);
//			model.addAttribute("admin", admin);
//		} else {
//			Admin admin = adminService.getEmployeeDetailsById(sessionAdmin.getEmployeeId());
//			model.addAttribute("admin", admin);
//		}

		return "user-list";
	}

	@GetMapping("/delete-admin/{id}/{eid}")
	public String deleteAdmin(Model model, @PathVariable("id") int id, @PathVariable("eid") int eid) {
		Admin admin1 = adminRepository.getById(eid);
		Admin admin = adminService.getEmployeeDetailsById(id);
		admin.setIsActive('N');

		adminService.addEmployeeDetails(admin);

		return "redirect:/emp/adminlist/" + admin1.getEmployeeId();

	}

	@GetMapping("/delete-user/{id}/{eid}")
	public String deleteUser(Model model, @PathVariable("id") int id, @PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		Customer customer = customerService.getCustomerById(id);
		customer.setIsActive('N');
		customerService.addCustomer(customer);
		return "redirect:/emp/userlist/" + eid;

	}

	@RequestMapping("/user-profile/{id}/{eid}")
	public String viewUserProfile(Model model, @PathVariable("id") int customerid, HttpSession session,
			@PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		Customer customerobject = customerService.getCustomerById(customerid);
		CustomerAddress customeraddress = customerAddressService.getcustomeraddressById(customerid);
		List<CustomerOrder> customerOrder = customerOrderRepository.getData(customerid);
		model.addAttribute("customerOrder", customerOrder);
		model.addAttribute("customerobject", customerobject);
		model.addAttribute("customeraddress", customeraddress);
		return "user-list-profile";
	}

	@RequestMapping("/productdetails/{id}")
	public String getProductdetailsByProductId(Model model, @PathVariable("id") int id, HttpSession session) {
		PhysicalProducts productlist = physicalProductService.getProductById(id);
		model.addAttribute("productlist", productlist);
		return "front-end-product-detail";
	}

	@RequestMapping("/aboutus")
	public String getAboutUs(Model model) {

		List<Admin> adminlist = adminService.getAllRoles();
		model.addAttribute("adminlist", adminlist);

		List<Admin> adminDesigner = adminService.getAllDesigners();
		model.addAttribute("adminDesigner", adminDesigner);

		return "about-us";
	}

	@RequestMapping("/back-end-profile/{id}")
	public String getProfile(Model model, @PathVariable("id") int id,
			@ModelAttribute(value = "Adminobject") Admin admin) {

		Admin adminobject = adminService.getEmployeeDetailsById(id);
		model.addAttribute("adminobject", adminobject);
		return "backend-profile";
	}
	@RequestMapping("/OrderDetails/{eid}")
	public String orderDetails(@PathVariable("eid")int eid,Model model) {
		Admin admin = adminService.getEmployeeDetailsById(eid);
		List<CustomerOrder> orderobj = customerOrderRepository.findAll();
		model.addAttribute("orderobj", orderobj);

		return "Orderdetails";
	}
	

}
