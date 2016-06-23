package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.ServerExistingDAO;
import com.ghw.model.ServerExisting;
import com.ghw.service.ServerExistingService;
import com.ghw.util.IpServer;
import com.ghw.util.RestUtil;

@Service("serverExistingService")
@Transactional(readOnly = false)
public class ServerExistingServiceImpl extends
		ServiceImpl<ServerExisting, String, ServerExistingDAO> implements
		ServerExistingService {

	private ServerExistingDAO dao;

	@Autowired
	private RestUtil restUtil;

	@Autowired
	public void setDAO(ServerExistingDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public boolean validateIfExecute() {

		// by default is true
		boolean execute = true;

		String host = IpServer.ipServer();

		// search if the actual server is config
		ServerExisting serverExisting = dao.getDataByHost(host);

		// if not exist don't execute
		if (serverExisting == null)
			return false;

		// if is the principal EXECUTE
		if (serverExisting.getPriority().equals(1))
			return true;

		// if is not a principal server search if the server with mayor priority
		// are running (at least one)
		List<ServerExisting> listMayorPriority = dao
				.getPrincipalHost(serverExisting.getPriority());
		for (ServerExisting se : listMayorPriority) {
			// call the webservice, is the server with mayor priority is running
			// dont execute this server
			if (restUtil.isRunning(se.getUrlWebservice()))
				return false;
		}

		return execute;
	}

}