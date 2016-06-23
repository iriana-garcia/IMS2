package com.ghw.report;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.ghw.report.model.Report;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

/**
 * Utility class to load reports
 * 
 * 
 */
public class ReportLoader {

	private static String ext = ".jrxml";

	/**
	 * Gets the path to the folder on the reports Seek first constant context
	 * reports_path if not found returns /secure/reports/
	 * 
	 * @return String
	 */
	public static String getAppReportsPath() {

		String path = Context.getServletContext().getInitParameter(
				"reports_path");

		if (path != null && path.length() > 0)
			return path;

		return "/secure/reports/";
	}

	/**
	 * Gets the true path to reports
	 * 
	 * @return String
	 */
	public static String getReportsPath() {
		String path = Context.getRealPath().concat(getAppReportsPath());
		return path;
	}

	/**
	 * Gets the path of a report
	 * 
	 * @param report
	 *            {@link Report}
	 * @return {@link String}
	 */
	public static String getReportPath(String report) {
		return getReportsPath().concat(report).concat(ext);
	}

	// /**
	// * Obtiene la ruta de un reporte
	// *
	// * @param reporte
	// * {@link Report}
	// * @return {@link String}
	// */
	// public static String getReportPathConfigurado(String reporte) {
	// return Context.getRealPath().concat("/reportes/").concat(reporte)
	// .concat(ext);
	// }

	/**
	 * Gets the path of a report
	 * 
	 * @param reporte
	 *            {@link Report}
	 * @return {@link String}
	 */
	public static String getReportPath(String report, String realPath) {
		return realPath.concat("/secure/reports/").concat(report).concat(ext);
	}

	/**
	 * Gets the path of a report
	 * 
	 * @param report
	 *            {@link Report}
	 * @return {@link String}
	 */
	public static String getReportPath(Report report) {
		return getReportPath(report.getJrxml());
	}

	//
	// public static String getReportPathConfigurados(Report report) {
	// return getReportPathConfigurado(report.getJrxml());
	// }

	/**
	 * Upload a design from the name of the report
	 * 
	 * @param report
	 *            {@link String}
	 * @return {@link JasperDesign}
	 * @throws {@link SystemException}
	 */
	public static JasperDesign getJasperDesign(String report, String realPath)
			throws SystemException {

		try {

			JasperDesign jd = JRXmlLoader.load(getReportPath(report, realPath));

			return jd;

		} catch (Exception e) {
			throw new SystemException(
					"Report Loader getJasperDesign(String report, String realPath) error_report_loading");
		}
	}

	public static JasperDesign getJasperDesign(String report)
			throws SystemException {

		try {

			JasperDesign jd = JRXmlLoader.load(getReportPath(report));

			return jd;

		} catch (Exception e) {
			throw new SystemException(
					"Report loader getJasperDesign(String report) error_report_loading");
		}
	}

	/**
	 * Upload a design from a class report
	 * 
	 * @param report
	 *            : Report
	 * @return JasperDesign
	 * @throws SystemException
	 */
	public static JasperDesign getJasperDesign(Report report)
			throws SystemException {
		return getJasperDesign(report.getJrxml());
	}

	/**
	 * Upload a design from a class report
	 * 
	 * @param reporte
	 *            : Reporte
	 * @return JasperDesign
	 * @throws SystemException
	 */
	public static JasperDesign getJasperDesign(Report report, String realPath)
			throws SystemException {
		return getJasperDesign(report.getJrxml(), realPath);
	}

	/**
	 * Loads a report from the report name
	 * 
	 * @param report
	 *            : String report name
	 * @return JasperReport
	 * @throws SystemException
	 */
	public static JasperReport getJasperReport(String reportName)
			throws SystemException {
		try {

			JasperReport jr = JasperCompileManager
					.compileReport(getReportPath(reportName));

			return jr;

		} catch (Exception e) {
			throw new SystemException(
					"Report Loader getJasperReport(String reportName) error_report_loading");
		}
	}

	/**
	 * Loads a report from a report
	 * 
	 * @param report
	 *            : Report
	 * @return JasperReport
	 * @throws SystemException
	 */
	public static JasperReport getJasperReport(Report report)
			throws SystemException {
		return getJasperReport(report.getJrxml());
	}

	/**
	 * Loads a report from a JasperDesign
	 * 
	 * @param design
	 *            : JasperDesign
	 * @return JasperReport
	 * @throws SystemException
	 */
	public static JasperReport getJasperReport(JasperDesign design)
			throws SystemException {
		try {

			JasperReport jr = JasperCompileManager.compileReport(design);
			return jr;
		} catch (Exception e) {
			throw new SystemException(
					"Report Loader getJasperReport(JasperDesign design) error_report_loading");
		}
	}
}
