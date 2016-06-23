package com.ghw.dao;

import java.util.Date;
import java.util.List;

import com.ghw.model.UpdatedDate;

public interface UpdatedDateDAO extends GenericDAO<UpdatedDate, Integer> {

	public int update(String field, Date value);

	public List<UpdatedDate> getDataPendingUpdate(Date date);
}