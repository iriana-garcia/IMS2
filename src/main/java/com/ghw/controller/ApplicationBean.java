package com.ghw.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.ConfigurationSystem;
import com.ghw.model.Thresholds;
import com.ghw.service.ConfigurationGeneralService;
import com.ghw.service.ConfigurationSystemService;
import com.ghw.service.ThresholdsService;

/**
 * Store the LDAP connection in Application scope
 * 
 * @author ifernandez
 * 
 */
@Component
@Scope(value = "singleton")
public class ApplicationBean implements Serializable {

	ConfigurationGeneral configuration;

	private List<Thresholds> thresholds;

	private ConfigurationSystem systemConf;

	@Autowired
	private ConfigurationGeneralService service;

	@Autowired
	private ThresholdsService thresholdsService;

	@Autowired
	private ConfigurationSystemService configurationSystemService;

	public ConfigurationGeneral getConfiguration() {
		if (configuration == null) {
			configuration = service.getDataConfiguration();
		}
		return configuration;
	}

	public void setConfiguration(ConfigurationGeneral configuration) {
		this.configuration = configuration;
	}

	public List<Thresholds> getThresholds() {
		if (thresholds == null) {
			thresholds = thresholdsService.getData();
		}
		return thresholds;
	}

	public void setThresholds(List<Thresholds> thresholds) {
		this.thresholds = thresholds;
	}

	public ConfigurationSystem getSystemConf() {
		if (systemConf == null) {
			List<ConfigurationSystem> list = configurationSystemService
					.getData();
			systemConf = list != null && list.size() > 0 ? list.get(0) : null;
		}
		return systemConf;
	}

	public void setSystemConf(ConfigurationSystem systemConf) {
		this.systemConf = systemConf;
	}

}
