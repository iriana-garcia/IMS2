package com.ghw.service;

import com.ghw.model.ServerExisting;

public interface ServerExistingService extends Service<ServerExisting, String> {

	public boolean validateIfExecute();
}