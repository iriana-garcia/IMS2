package com.ghw.service;

import com.ghw.model.RoutingNumbers;

public interface RoutingNumbersService extends Service<RoutingNumbers, String> {

	public boolean validateIfExistRoutingNumber(String routingNumber);
}