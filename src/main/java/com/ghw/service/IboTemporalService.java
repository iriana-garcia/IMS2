package com.ghw.service;

import com.ghw.model.IboTemporal;
import com.ghw.model.UserUtil;

public interface IboTemporalService extends Service<IboTemporal, String> {

	public boolean validateIfExistsAd(UserUtil userUtil) throws Exception;

	public IboTemporal changeState(IboTemporal entity) throws Exception;
}