package com.ghw.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;

import com.ghw.report.model.Report;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

/**
 * Utility class for the JasperPrint from data
 * 
 * 
 */
public class ReportBuilder {

	private Object[] data;
	private Map params;
	private Report report;

	private List<Object[]> parameters = new ArrayList<Object[]>();

	public Object[] getData() {
		return data;
	}

	public void setData(Object[] data) {
		this.data = data;
	}

	public void setData(List data) {
		this.data = data.toArray();
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public ReportBuilder(Object[] data) {
		setData(data);
	}

	public ReportBuilder(Object[] data, Report reporte) {
		setData(data);
		this.report = reporte;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<Object[]> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object[]> parameters) {
		this.parameters = parameters;
	}

	public ReportBuilder(List data) {
		setData(data);
	}

	@SuppressWarnings("unchecked")
	public HashMap getParamsMap() {

		HashMap params = new HashMap();

		params.put("LOCATION", Context.getRealPath());
		params.put("path",
				Context.getRealPath() + ReportLoader.getAppReportsPath());

		for (Object[] ob : parameters) {
			params.put(ob[0], ob[1]);
		}

		if (getParams() != null)
			params.putAll(getParams());

		return params;
	}

	@SuppressWarnings("unchecked")
	public HashMap getParamsMap(String realPath) {

		HashMap params = new HashMap();
		params.put("LOCATION", realPath);

		if (getParams() != null)
			params.putAll(getParams());

		return params;
	}

	/**
	 * You get ready to print from the design and Class Data Report.
	 * 
	 * @param design
	 *            JasperDesign
	 * @return JasperPrint
	 * @throws SystemException
	 */
	public JasperPrint getJasperPrint(JasperDesign design)
			throws SystemException {

		try {
			JasperPrint jp = JasperFillManager.fillReport(
					ReportLoader.getJasperReport(design), getParamsMap(),
					new JRBeanArrayDataSource(getData()));

			return jp;
		} catch (Exception e) {
			throw new SystemException("getJasperPrint(JasperDesign design) error_report_loading");
		}
	}

	public JasperPrint getJasperPrint(JasperDesign design, String realPath)
			throws SystemException {

		try {
			JasperPrint jp = JasperFillManager.fillReport(
					ReportLoader.getJasperReport(design),
					getParamsMap(realPath),
					new JRBeanArrayDataSource(getData()));

			return jp;
		} catch (Exception e) {
			throw new SystemException("getJasperPrint(JasperDesign design, String realPath) error_report_loading");
		}
	}

	/**
	 * get ready to print from the report object and class data report.
	 * 
	 * @param report
	 *            JasperReport
	 * @return JasperPrint
	 * @throws SystemException
	 */
	public JasperPrint getJasperPrint(JasperReport report)
			throws SystemException {

		try {
			JasperPrint jp = JasperFillManager.fillReport(report,
					getParamsMap(), new JRBeanArrayDataSource(getData()));

			return jp;
		} catch (Exception e) {
			throw new SystemException("getJasperPrint(JasperReport report) error_report_loading");
		}
	}

}
