package com.ecom.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.beans.BilingDetails;
import com.ecom.beans.Coupon;
import com.ecom.beans.Customer;
import com.ecom.beans.CustomerAddress;
import com.ecom.beans.CustomerCart;
import com.ecom.beans.CustomerOrder;
import com.ecom.beans.OrderByCustomer;
import com.ecom.beans.PhysicalProducts;
import com.ecom.beans.Store;
import com.ecom.repository.BillingRepository;
import com.ecom.repository.CouponRepository;
import com.ecom.repository.CustomerAddressRepository;
import com.ecom.repository.CustomerCartRepository;
import com.ecom.repository.CustomerOrderRepository;
import com.ecom.repository.CustomerRepository;
import com.ecom.repository.OrderByCustomerRepository;
import com.ecom.service.BillingService;
import com.ecom.service.CustomerCartService;
import com.ecom.service.CustomerService;
import com.ecom.service.EmailSenderService;
import com.ecom.service.PhysicalProductService;
import com.ecom.service.StoreService;

@Controller
@RequestMapping("/Bill")
public class BillingController {

	@Autowired
	BillingService billingService;
	@Autowired
	CustomerCartService customerCartService;
	@Autowired
	CouponRepository couponRepository;
	@Autowired
	BillingRepository billingRepository;
	@Autowired
	StoreService storeService;
	@Autowired
	CustomerService customerService;
	@Autowired
	CustomerCartRepository customerCartRepository;
	@Autowired
	CustomerOrderRepository customerOrderRepository;
	@Autowired
	PhysicalProductService physicalProductService;
	@Autowired
	OrderByCustomerRepository orderByCustomerRepository;
	@Autowired
	EmailSenderService emailSenderService;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	CustomerAddressRepository customerAddressRepository;

	@RequestMapping("/addressList/{cid}")
	public String addressingList(@PathVariable("cid") int cid, Model model) {
		Customer customer = customerRepository.getById(cid);
		List<CustomerAddress> customerAddress = customerAddressRepository.getCustomerAddress(cid);
		model.addAttribute("address", customerAddress);
		model.addAttribute("customer", customer);
		return "Customer-Address-List";
	}

	@RequestMapping(value = "/indexBill/{cid}/{caid}")
	public String getIndex(Model model, @PathVariable(value = "cid") int cid, @PathVariable("caid") int caid) {
		Customer customerobject = customerService.getCustomerById(cid);
		List<CustomerCart> cartlist = customerCartRepository.getCartActiveList(cid);
		CustomerAddress customerAddress = customerAddressRepository.getById(caid);
		BilingDetails bilingDetails = new BilingDetails();
		bilingDetails.setBillingId((int) Math.random());
		bilingDetails.setAddressId(customerAddress.getAddressId());
		bilingDetails.setCustomerId(cid);
		bilingDetails.setAddressLine1(customerAddress.getAddressLine1());
		bilingDetails.setAddressLine2(customerAddress.getAddressLine2());
		bilingDetails.setCity(customerAddress.getCity());
		bilingDetails.setCountry(customerAddress.getCountry());
		bilingDetails.setFirstName(customerobject.getFirstName());
		bilingDetails.setLastName(customerobject.getLastName());
		bilingDetails.setPinCode(customerAddress.getPinCode());
		bilingDetails.setIsActive('Y');
		BilingDetails bill = billingRepository.save(bilingDetails);
		if (cartlist.size() != 0) {
			float total = Float.parseFloat(customerCartRepository.getcarttotal(cid));
			List<Coupon> coupon = couponRepository.findAll();
			
			model.addAttribute("customer", customerobject);
			model.addAttribute("objBilling", bill);
			model.addAttribute("total", total);
			model.addAttribute("cartlist", cartlist);

			if (coupon.size() != 0) {
				model.addAttribute("coupon", coupon);

				return "checkout";
			} else {

				model.addAttribute("msg1", "No Coupon are Avaliable");
				return "checkout";
			}
		} else {
			float totalcartprice = 0;
			List<CustomerCart> list = customerCartRepository.getCartActiveList(cid);
			Customer data = customerService.getCustomerById(cid);
			if (customerCartRepository.getcarttotal(cid) != null) {
				totalcartprice = Float.parseFloat(customerCartRepository.getcarttotal(cid));
			} else {
				totalcartprice = 0;
			}
			model.addAttribute("totalcartprice", totalcartprice);
			model.addAttribute("Cart", list);
			model.addAttribute("customer", data);
			model.addAttribute("msg", "No Products in Cart");

			return "cart";
		}
	}

	@RequestMapping(value = "/AddBill/{cid}/{total}")
	public String addBill(Model model, BilingDetails bilingDetails, @PathVariable(value = "cid") int cid,
			@PathVariable("total")float total,@RequestParam("paymentMethod") String paymentMethod) {

		int min1 = 1000000;
		int max2 = 9999999;
		int random_int1 = (int) Math.floor(Math.random() * (max2 - min1 + 1) + min1);
		int min = 10000;
		int max = 99999;
		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
		CustomerOrder order = new CustomerOrder();
		Customer customerobject = customerService.getCustomerById(cid);
		bilingDetails.setCustomerId(cid);
		bilingDetails.setIsActive('Y');
		BilingDetails billingobject = billingService.addBilingName(bilingDetails);
		order.setOrderId((int) Math.random());
		order.setOrderDate(LocalDate.now());
		order.setOrderTime(LocalTime.now());
		order.setIncrDate(LocalDate.now().plusDays(6));
		order.setCarrierId(random_int1);
		order.setCustomerId(cid);
		order.setOrderNumber(random_int);
		order.setOrderDate(LocalDate.now());
		order.setOrderTime(LocalTime.now());
		order.setBillingId(billingobject.getBillingId());
		order.setPaymentMethod(paymentMethod);
		order.setStatus("Delivared");
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
		StringBuilder sb = new StringBuilder(17);

		for (int i = 0; i < 16; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		String str = sb.toString();
		order.setTranscationId(str);
		order.setTotalPrice(total);
		order.setCustomerId(cid);

		CustomerOrder orderobject = customerOrderRepository.save(order);

		List<CustomerCart> cartlist1 = customerCartRepository.getAllCartList(cid);
		for (CustomerCart cart : cartlist1) {

			cart.setIsActive('B');
			customerCartService.addCart(cart);
		}
		model.addAttribute("totalcartprice", customerCartRepository.getBilltotal(cid));
//		String s1 = "Your Multikart.in Order #" + orderobject.getOrderNumber() + " of Item";
//		String s2 = "Hello " + customerobject.getUserName() + " Thank you for shopping with us. "
//				+ " Order Confirmation order#" + orderobject.getOrderNumber() + "";
//
//		emailSenderService.sendSimpleEmail(customerobject.getEmail(), s2, s1);

		List<CustomerCart> quantity = customerCartRepository.getBillOrderList(cid);
		for (CustomerCart products : quantity) {
			List<OrderByCustomer> object1 = orderByCustomerRepository.ordered(cid, products.getProductModelNumber(),
					orderobject.getOrderId());
			if (object1.size() == 0) {
				OrderByCustomer orderByCustomer = new OrderByCustomer();
				orderByCustomer.setOBCId((int) Math.random());
				orderByCustomer.setOrderId(orderobject.getOrderId());
				orderByCustomer.setCustomerId(cid);
				orderByCustomer.setImage(products.getImage());
				orderByCustomer.setCustomerId(cid);
				orderByCustomer.setProductId(products.getProductId());
				orderByCustomer.setProductName(products.getProductName());
				orderByCustomer.setProductModelNumber(products.getProductModelNumber());
				orderByCustomer.setProductPrice(products.getProductPrice());
				orderByCustomer.setProductCompany(products.getProductCompany());
				orderByCustomer.setProductCode(products.getProductCode());
				orderByCustomer.setIsActive('Y');
				orderByCustomer.setTotalprice(products.getProductPrice());
				orderByCustomer.setQuantity(1);
				orderByCustomerRepository.save(orderByCustomer);

			} else {

				for (OrderByCustomer orderByCustomer1 : object1) {
					OrderByCustomer obj = orderByCustomerRepository.getById(orderByCustomer1.getOBCId());
					obj.setQuantity(obj.getQuantity() + 1);
					obj.setTotalprice(obj.getProductPrice()+obj.getProductPrice());
					
					orderByCustomerRepository.save(obj);

				}

			}
		}
		List<CustomerCart> cartlist = customerCartRepository.getBillOrderList(cid);
		for (CustomerCart cart : cartlist) {
			PhysicalProducts product = physicalProductService.getProductById(cart.getProductId());
			product.setIsactive('S');
			physicalProductService.addProduct(product);
			cart.setIsActive('O');
			customerCartService.addCart(cart);
		}
		List<OrderByCustomer> orderDownload = orderByCustomerRepository.orderedProduct(orderobject.getOrderId(), cid);
		model.addAttribute("totalcartprice", customerCartRepository.getcarttotal(cid));
		model.addAttribute("Cart", orderDownload);

		model.addAttribute("orderobject", orderobject);
		model.addAttribute("transcationId", orderobject.getTranscationId());
		model.addAttribute("billingobject", billingobject);
		model.addAttribute("customerobject", customerobject);
		return "order-success";
	}

	@RequestMapping("/tracking/{cid}/{tid}/{bid}")
	public String tracking(Model model, @PathVariable("cid") int cid, @PathVariable("tid") int tid,
			@PathVariable("bid") int bid) {
		// List<CustomerCart> list = customerCartService.getAllCart();
		Customer customerobject = customerService.getCustomerById(cid);
		// model.addAttribute("Tra", list);
		model.addAttribute("customerobject", customerobject);
		CustomerOrder customerOrder = customerOrderRepository.getById(tid);
		model.addAttribute("order", customerOrder);

		model.addAttribute("Track", customerOrder);

		List<Store> storeobject = storeService.getAllStores();
		model.addAttribute("storeList", storeobject);

		StringBuffer obj = new StringBuffer();
		int i = 1;

		for (Store a : storeobject) {
			System.out.println(a.getLatitude() + "hi" + a.getLongitude());
			// obj.append("['" + a.getBranchName() + "'," + a.getLatitude() + "," +
			// a.getLongitude() + "," + i + "],");
			obj.append("[\"" + a.getStoreName() + "\"," + a.getLatitude() + "," + a.getLongitude() + "," + 1 + "],");
			i++;
		}

		model.addAttribute("branchesGMap", obj.toString());

		BilingDetails bilingDetails = billingRepository.getById(bid);
		model.addAttribute("Track", bilingDetails);
		return "order-tracking";
	}

	@RequestMapping("/applycoupon/{cpnId}/{cid}/{caid}")
	public String applyCoupon(Model model,@PathVariable("cid") int cid,
			@PathVariable("cpnId") int cpnId,@PathVariable("caid")int caid) {

		Customer customerobject = customerService.getCustomerById(cid);
		List<CustomerCart> cartlist = customerCartRepository.getCartActiveList(cid);
		BilingDetails bilingDetails =billingRepository.getById(caid);
		float total = Float.parseFloat(customerCartRepository.getcarttotal(cid));
		Coupon coupon = couponRepository.getById(cpnId);
		float cpnprice =  (float) (coupon.getDiscount() / 100);
		total = total - cpnprice * total;
		model.addAttribute("customer", customerobject);
		model.addAttribute("objBilling", bilingDetails);
		model.addAttribute("total", total);
		model.addAttribute("cartlist", cartlist);
		model.addAttribute("coupon", coupon);

		return "coupon-checkout";
	}

	@RequestMapping("/addAddress/{cid}")
	public String addAddress(@PathVariable("cid") int cid, Model model) {
		Customer customer = customerRepository.getById(cid);
		CustomerAddress address = new CustomerAddress();
		model.addAttribute("address", address);
		model.addAttribute("customer", customer);
		return "customer-address";
	}

	@RequestMapping("/saveAddress/{cid}")
	public String saveAddress(@PathVariable("cid") int cid, Model model, CustomerAddress address) {
		address.setIsActive('Y');
		address.setCustomerId(cid);
		customerAddressRepository.save(address);
		Customer customer = customerRepository.getById(cid);
		List<CustomerAddress> customerAddress = customerAddressRepository.getCustomerAddress(cid);
		model.addAttribute("address", customerAddress);
		model.addAttribute("customer", customer);
		return "Customer-Address-List";

	}
	@RequestMapping("/deleteAddress/{cid}/{caid}")
	public String deleteAddress(@PathVariable("cid")int cid,@PathVariable("caid")int caid,Model model) {
		customerAddressRepository.deleteById(caid);
		Customer customer = customerRepository.getById(cid);
		List<CustomerAddress> customerAddress = customerAddressRepository.getCustomerAddress(cid);
		model.addAttribute("address", customerAddress);
		model.addAttribute("customer", customer);
		model.addAttribute("msg1", "Delete address Successfully");
		return "Customer-Address-List";
	}
	
}
