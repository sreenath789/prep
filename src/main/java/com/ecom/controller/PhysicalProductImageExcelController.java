package com.ecom.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecom.beans.PhysicalProductImageInExcel;
import com.ecom.beans.PhysicalProducts;
import com.ecom.repository.PhysicalProductImageExcelRepository;
import com.ecom.service.PhysicalProductService;

@Controller
@RequestMapping("excelsheet")
public class PhysicalProductImageExcelController {

	@Autowired
	private PhysicalProductImageExcelRepository pExcelRepository;
	@Autowired
	private PhysicalProductService physicalProductService;

	@RequestMapping("dataFilling")
	@ResponseBody
	public String addData(Model model) throws IOException {
		PhysicalProductImageInExcel pExcel = new PhysicalProductImageInExcel();
		List<PhysicalProducts> productlist = physicalProductService.getAllProduct();
//		for (PhysicalProducts product : productlist) {
//			pExcel.setProductModelNumber(product.getProductModelNumber());
//			pExcel.setProductName(product.getProductName());
//			pExcel.setProductCategory(product.getProductCategory());
//			pExcel.setProductSubCategory(product.getProductSubCategory());
//			pExcel.setVendorId(product.getProductCompany());
//			pExcel.setProductDescription(product.getProductDescription());
//			pExcel.setProductDiscountPrice(product.getProductDiscountPrice());
//			pExcel.setProductMRPPrice(product.getProductMRPPrice());
//			pExcel.setProductSize(product.getProductSize());
//			pExcel.setProductId((int) Math.random() * 90);
//			pExcel.setIsactive('N');
//			pExcel.setProductCode(product.getProductCode());
//			pExcelRepository.save(pExcel);
//		}

		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet sheet = workbook.createSheet("Sony Products");
			HSSFRow rowhead;
			Map<String, Object[]> productsData = new TreeMap<String, Object[]>();
			int h = 2;
			productsData.put("1", new Object[] { "ProductId", "ProductName", "productCategory", "productCode",
					"productCompany", "productDescription", "productDetails", "productDiscountPrice", "productImage",
					"productModelNumber", "productMRPPrice", "productRating", "productShippingInformation", "productSize",
					"productSpecification", "ProductSubCategory", "productVideo", "QRcode", "isactive" });
			List<PhysicalProductImageInExcel> list = pExcelRepository.findAll();
			for (PhysicalProductImageInExcel product : list) {
				for (int i = h; i < list.size(); i++) {
					String j = String.valueOf(i);
					productsData.put(j, new Object[] { product.getProductId(), product.getProductName(),
							product.getProductCategory(), product.getProductCode(), product.getVendorId(),
							product.getProductDescription(), product.getProductDetails(), product.getProductDiscountPrice(),
							product.getProductImage(), product.getProductModelNumber(), product.getProductMRPPrice(),
							product.getProductRating(), product.getProductShippingInformation(), product.getProductSize(),
							product.getProductSpecification(), product.getProductSubCategory(), product.getProductVideo(),
							product.getQRcode(), product.getIsactive() });
					h++;
					i = list.size();
				}
			}
			Set<String> keyid = productsData.keySet();
			int rowid = 0;

			// writing the data into the sheets...

			for (String key : keyid) {

				rowhead = sheet.createRow((short) rowid++);
				Object[] objectArr = productsData.get(key);
				int cellid = 0;

				for (Object obj : objectArr) {
					rowhead.createCell((cellid++)).setCellValue((String) obj);


				}
			}

			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\Aakash\\Desktop\\GFGsheet.xlsx"));

			workbook.write(out);
			out.close();
		}
		return "santhosh";
	}
}
