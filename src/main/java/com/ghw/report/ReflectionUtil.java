package com.ghw.report;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import com.ghw.filter.FilterBase;
import com.ghw.util.ApplicationContextProvider;

public class ReflectionUtil {

	private String clazz;
	private String method;

	public ReflectionUtil(String clazz) {
		setClazz(clazz);
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@SuppressWarnings("unchecked")
	public List getList(String method, FilterBase filter) throws Exception {

		// Class c = Class.forName(getClazz());
		List datos;

		Object o = new ApplicationContextProvider().getApplicationContext()
				.getBean(clazz);
		Class c = o.getClass();

		if (filter != null) {

			Integer fr = filter.getFirstRow();
			Integer nr = filter.getNumberOfRows();

			filter.setFirstRow(0);
			filter.setNumberOfRows(0);

			Method m = c.getMethod(method, FilterBase.class);

			datos = (List) ReflectionUtils.invokeMethod(m, o, filter);

			filter.setFirstRow(fr);
			filter.setNumberOfRows(nr);

		} else {
			Method m = c.getMethod(method);
			datos = (List) ReflectionUtils.invokeMethod(m, o);
		}

		return datos;
	}
}
