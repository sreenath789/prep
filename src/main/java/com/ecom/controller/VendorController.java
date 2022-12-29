package com.ecom.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ecom.beans.Admin;
import com.ecom.beans.DigitalProducts;
import com.ecom.beans.Vendor;
import com.ecom.repository.AdminRepository;
import com.ecom.repository.VendorRepository;
import com.ecom.service.AdminService;
import com.ecom.service.VendorService;
import com.google.zxing.WriterException;

@Controller
@RequestMapping("/vendor")
public class VendorController {

	@Autowired
	VendorService vendorService;

	@Autowired
	AdminService adminService;

	@Autowired
	VendorRepository vendorRepository;
	@Autowired
	AdminRepository adminRepository;

	@RequestMapping(value = "/create-vendor/{eid}")
	public String getVendors(Model model, HttpSession session, @PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);

		Vendor vendor = new Vendor();

		model.addAttribute("vendor", vendor);
		return "vendor-Register";

	}

	@RequestMapping(value = "/saveVendor/{eid}", method = RequestMethod.POST)
	public String saveVendor(Model model, @ModelAttribute(value = "vendorObj") Vendor vendor, HttpSession session,
			@PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);

		vendor.setCreatedBy(admin.getEmployeeId());
		vendor.setUpdatedBy(admin.getEmployeeId());

		vendor.setIsActive('Y');
		vendor.setCreated(LocalDate.now());
		vendor.setUpdated(LocalDate.now());
		vendorService.addVendor(vendor);
		return "redirect:/vendor/vendor-list/" + eid;
	}

	@GetMapping(value = "/vendor-list/{eid}")
	public String getAllVendors(Model model, HttpSession session, @PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);
		List<Vendor> list = vendorService.getAllVendors();

		List<Vendor> vendorlist = new ArrayList<>();
		for (Vendor vendor : list) {
			if (vendor.getIsActive() == 'Y' || vendor.getIsActive() == 'y') {
				vendorlist.add(vendor);
			}
		}
		model.addAttribute("vendorlist", vendorlist);

		return "vendor-list";
	}

	@GetMapping("/delete-vendor/{id}/{eid}")
	public String deleteVendor(Model model, @PathVariable("id") int id, @PathVariable("eid") int eid) {
		Admin admin = adminRepository.getById(eid);
		model.addAttribute("admin", admin);

		Vendor vendor = vendorService.getVendorById(id);
		vendor.setIsActive('N');
		vendorService.addVendor(vendor);
		return "redirect:/vendor/vendor-list/" + eid;

	}

	@GetMapping("/edit-vendor/{id}")
	public String getVendorById(Model model, @PathVariable("id") int id) {

		Vendor vendorObj = vendorService.getVendorById(id);
		model.addAttribute("vendorObj", vendorObj);
		return "edit-vendor";
	}

	@RequestMapping(value = "/saveCompany", method = RequestMethod.POST)
	public ModelAndView addEmployee(Model model, Vendor vendor, HttpSession session) {

		Vendor adminObj = vendorService.findByEmailId(vendor.getEmail());

		if (adminObj != null) {
			ModelAndView modelAndView = new ModelAndView("redirect:/vendor/vendor-Register");
			String errorMessage = "User Already Exists";
			modelAndView.addObject("errorMessage", errorMessage);
			modelAndView.addObject("employee", vendor);

			return modelAndView;
		} else {

			vendor.setIsActive('y');
			vendor.setCreated(LocalDate.now());
			vendor.setUpdated(LocalDate.now());
			ModelAndView modelAndView = new ModelAndView("Vendor-Register");
			Vendor adminobject = vendorService.addVendorDetails(vendor);
			session.setAttribute("name", vendor.getCompanyName());
			session.setAttribute("role", vendor.getVendorType());

			model.addAttribute("admin", adminobject);
			return modelAndView;

		}
	}

	@RequestMapping(value = "/vendor-login")
	public String getVendorIndex(Model model, Vendor vendorobj) {

		model.addAttribute("vendorobj", vendorobj);
		return "vendor-login";

	}

	@RequestMapping(value = "/newvendorlogin", method = RequestMethod.POST)
	public String newvendorlogin(Model model, Vendor vendor, HttpServletRequest request, HttpSession session) {

		Vendor vendorobject = vendorService.getVendorDetails(vendor.getEmail(), vendor.getPassword());
		if (vendorobject != null) {

			model.addAttribute("vendor", vendorobject);

			return "redirect:/vendor/login/" + vendorobject.getVendorId();
		} else {

			String errorMessage = "Invalid Credentials!";
			model.addAttribute("errorMessage", errorMessage);

			return "redirect:/vendor/vendor-login";
		}
	}

	@RequestMapping(value = "/upload/{id}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

	public String uploadExcelsheet(Model model, @PathVariable(value = "id") int id, Vendor vendorobj,
			@RequestParam("files") MultipartFile vendor, HttpSession session) throws IOException, WriterException {
		StringBuilder filejoin = new StringBuilder();

		Vendor obj = vendorService.getVendorById(id);
		String uploadDir = "C:\\Users\\Aakash\\Desktop\\santosh_job_practice\\MergeEcomm\\src\\main\\resources\\static\\ExcelFiles\\"
				+ id + "\\";

		filejoin.append(vendor.getOriginalFilename() + ",");
		String fileName = StringUtils.cleanPath(vendor.getOriginalFilename());
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try (InputStream inputstream = vendor.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputstream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new IOException("Could not save image file: " + fileName, ioe);
		}
		obj.setFile("../../ExcelFiles/" + id + "/" + vendor.getOriginalFilename());
		obj.setCreated(LocalDate.now());
		obj.setUpdated(LocalDate.now());

		Vendor physicalproduct = vendorRepository.save(obj);
		model.addAttribute("vendorobj", physicalproduct);

		return "redirect:/vendor/upload/"+physicalproduct.getVendorId();
	}

	@RequestMapping("/login/{id}")
	public String add(@PathVariable("id") int id, Model model, HttpSession session) {

		Vendor vendorobject = vendorService.getVendorById(id);
		model.addAttribute("vendor", vendorobject);
		return "excelsheet-upload";
	}

	@RequestMapping("/vendorExcelupload/{id}")
	public String VendorUplodingForm(Model model, @PathVariable(value = "id") int id) {
		Vendor vendorobj = vendorService.getVendorById(id);
		model.addAttribute("vendorobj", vendorobj);
		return "excelUpload";
	}

}
