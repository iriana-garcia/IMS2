package com.ghw.dao;

import java.util.List;

import com.ghw.model.RoutingNumbers;

public interface RoutingNumbersDAO extends GenericDAO<RoutingNumbers, String> {

	public boolean validateIfExistRoutingNumber(String routingNumber);

	public void updateNeedUpdatedFalse(List<RoutingNumbers> routingNumbers);

	public List<RoutingNumbers> getDataActiveOracle();
}
