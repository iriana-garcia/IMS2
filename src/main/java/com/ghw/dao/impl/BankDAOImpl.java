package com.ghw.dao.impl;

import java.util.List;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import com.ghw.dao.BankDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.Bank;

@Repository("bankDAO")
public class BankDAOImpl extends GenericHibernateDAO<Bank, String> implements
		BankDAO {
}