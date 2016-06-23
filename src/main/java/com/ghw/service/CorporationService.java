package com.ghw.service;

import java.util.List;

import com.ghw.model.Corporation;

public interface CorporationService extends Service<Corporation, String> {

	public Corporation getDataByName(String name);

	public Corporation getDataByEin(String ein);

	public List<Corporation> loadAll();

	public List<Corporation> getDataWithAddress(String region);
}