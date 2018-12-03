package com.esignlive.copytool.service;

import com.esignlive.copytool.data.UserData;
import com.silanis.esl.sdk.EslClient;

public class EndpointService {
	private static EndpointService endpointService;

	private EndpointService() {
	}

	public static EndpointService getInstance() {
		return endpointService == null ? new EndpointService() : endpointService;
	}

	public void testConnection() throws Exception {
		UserData.sourceEslClient = new EslClient(UserData.sourceApiKey, UserData.sourceApiUrl);
		UserData.destinationEslClient = new EslClient(UserData.destinationApiKey, UserData.destinationApiUrl);

		try {
			UserData.sourceEslClient.getSystemService().getApplicationVersion();
			UserData.destinationEslClient.getSystemService().getApplicationVersion();
		} catch (Exception e) {
			UserData.sourceEslClient = null;
			UserData.destinationEslClient = null;
			throw e;
		}

	}

}
