package com.ghw.report;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.j2ee.servlets.BaseHttpServlet;

import org.apache.commons.lang3.StringUtils;

import com.ghw.filter.FilterBase;
import com.ghw.report.model.PrintFilter;
import com.ghw.report.model.Report;
import com.ghw.report.model.ReportTable;
import com.ghw.report.service.ReportService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

/**
 * Utility class for working with reports.
 * 
 * 
 */
public class ReportApp {

	protected String listMethod = "getData";
	protected String detailMethod = "loadAllById";

	private List<Object[]> parameters = new ArrayList<Object[]>();

	public ReportApp(List<Object[]> parameters) {
		super();
		this.parameters = parameters;
	}

	public ReportApp() {
		super();
	}

	public List<Object[]> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object[]> parameters) {
		this.parameters = parameters;
	}

	private ReportService reportService;

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	/**
	 * get a report from the filter.
	 * 
	 * @param filtro
	 *            {@link PrintFilter}
	 * @return {@link Report}
	 * @throws Exception
	 */
	private Report getReporte(PrintFilter filtro) throws Exception {

		List<Report> reportes = reportService.getData(filtro);

		if (reportes.size() > 0) {
			Report reporte = reportes.get(0);
			return reporte;
		} else
			throw new SystemException("report_error_not_found");
	}

	/**
	 * Gets the list of data required to fill out a report.
	 * 
	 * @param report
	 *            {@link Report}
	 * @param filtro
	 *            {@link FilterBase}
	 * @param detalle
	 *            boolean
	 * @return {@link List}
	 * @throws {@link SystemException}
	 */
	private List getDatos(Report report, FilterBase filter, boolean detail)
			throws SystemException {

		String classService = report.getGroup().getClassService();
		ReflectionUtil reflectionUtil = new ReflectionUtil(classService);

		String method;

		if (StringUtils.isNotBlank(report.getMethod()))
			method = report.getMethod();
		else
			method = (detail) ? detailMethod : listMethod;

		try {
			return reflectionUtil.getList(method, filter);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException("report_error_loading_data");
		}

	}

	/**
	 * gets the JasperPrint and puts it in the session ready to print.
	 * 
	 * @param report
	 *            String
	 * @param datos
	 *            Object[]
	 * @return {@link JasperPrint}
	 * @throws {@link SystemException}
	 */
	public JasperPrint getJasperPrint(String report, Object[] data)
			throws SystemException {

		JasperDesign jd = ReportLoader.getJasperDesign(report);

		ReportBuilder rbu = new ReportBuilder(data);

		JasperPrint jp = rbu.getJasperPrint(jd);

		Context.setSessionAttribute(
				BaseHttpServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jp);

		return jp;
	}

	/**
	 * He gets the JasperPrint and puts it in the session ready to print.
	 * 
	 * @param report
	 *            {@link Report}
	 * @param datos
	 *            Object[]
	 * @return {@link JasperPrint}
	 * @throws {@link SystemException}
	 */
	public JasperPrint getJasperPrint(Report report, Object[] data)
			throws SystemException {

		JasperDesign jd = ReportLoader.getJasperDesign(report);

		ReportBuilder rbu = new ReportBuilder(data, report);
		rbu.setParameters(parameters);

		JasperPrint jp = rbu.getJasperPrint(jd);

		Context.setSessionAttribute(
				BaseHttpServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jp);

		return jp;
	}

	/**
	 * gets the JasperPrint and puts it in the session ready to print.
	 * 
	 * @param filter
	 *            {@link PrintFilter}
	 * @param datos
	 *            Object[]
	 * @return {@link JasperPrint}
	 * @throws Exception
	 * @throws {@link SystemException}
	 */
	public JasperPrint getJasperPrint(PrintFilter filter, Object[] data)
			throws Exception {

		Report reporte = getReporte(filter);

		return getJasperPrint(reporte, data);

	}

	public JasperPrint getJasperPrint(PrintFilter filter, Object[] data,
			String title) throws Exception {

		Report report = getReporte(filter);

		if (StringUtils.isNotBlank(title))
			report.setTitle(Context.getUIMsg(title));

		return getJasperPrint(report, data);

	}

	/**
	 * gets the JasperPrint and puts it in the session ready to print. This
	 * method automatically searches the data of the report.
	 * 
	 * @param report
	 *            {@link Report}
	 * @param detail
	 *            boolean
	 * @return {@link JasperPrint}
	 * @throws {@link SystemException}
	 */
	public JasperPrint getJasperPrint(Report report, boolean detail)
			throws SystemException {

		FilterBase dataFilter = (FilterBase) Context.getBean(report.getGroup()
				.getFilterBean());

		if (dataFilter != null) {

			List datos = getDatos(report, dataFilter, detail);

			return getJasperPrint(report, datos.toArray());
		}

		return null;
	}

	public JasperPrint getJasperPrintTabla(Report report, boolean detail)
			throws SystemException {

		FilterBase dataFilter = (FilterBase) Context.getBean(report.getGroup()
				.getFilterBean());

		if (dataFilter != null) {

			List datos = getDatos(report, dataFilter, detail);

			List<ReportTable> list = new ArrayList<ReportTable>();
			ReportTable table = new ReportTable(datos);
			list.add(table);

			return getJasperPrint(report, list.toArray());
		}

		return null;
	}

	/**
	 * gets the JasperPrint and puts it in the session ready to print. This
	 * method automatically searches the data of the report.
	 * 
	 * @param filter
	 *            {@link PrintFilter}
	 * @return {@link JasperPrint}
	 * @throws Exception
	 * @throws {@link SystemException}
	 */
	public JasperPrint getJasperPrint(PrintFilter filter) throws Exception {

		Report report = getReporte(filter);

		return getJasperPrint(report, filter.getTypeDefault() == 2);
	}

	public JasperPrint getJasperPrintTabla(PrintFilter filter) throws Exception {

		Report report = getReporte(filter);

		return getJasperPrintTabla(report, filter.getTypeDefault() == 2);
	}

}