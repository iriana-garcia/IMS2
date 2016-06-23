package com.ghw.controller;

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;

import org.springframework.stereotype.Component;

import com.ghw.model.LogSystem;

public class ExporterUtil {
	
	private String title;

	public ExporterUtil() {
		build();

	}

	private void build() {

		TextColumnBuilder<String> itemColumn = col.column("Item", "item",
				type.stringType());

		TextColumnBuilder<Integer> quantityColumn = col.column("Quantity",
				"quantity", type.integerType());

		TextColumnBuilder<BigDecimal> priceColumn = col.column("Unit price",
				"unitprice", type.bigDecimalType());

		try {

			report().setTemplate(Templates.reportTemplate)

			.columns(

			itemColumn, quantityColumn, priceColumn)

			.title(Templates.createTitleComponent(title))

			.pageFooter(Templates.footerComponent)

			.setDataSource(createDataSource())

			.setVirtualizer(new JRFileVirtualizer(2))

			.show();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	private JRBeanCollectionDataSource createDataSourceCollection()
			throws Exception {

		List<LogSystem> list = new ArrayList<LogSystem>();
		
		JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(list);

		return data;

	}

	// @Autowired
	// private LogSystemService logSystemService;
	// @Autowired
	// private LogSystemFilter filter;

	private JRDataSource createDataSource() throws Exception {

		DRDataSource dataSource = new DRDataSource("item", "quantity",
				"unitprice");

		for (int i = 0; i < 30; i++) {

			// for (int i = 0; i < 100000; i++) {

			dataSource.add("Book", (int) (Math.random() * 10) + 1,
					new BigDecimal(Math.random() * 100 + 1));

		}

		return dataSource;

	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static void main(String[] args) {

		new ExporterUtil();

	}

}