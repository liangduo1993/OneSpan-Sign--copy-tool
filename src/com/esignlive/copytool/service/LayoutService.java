package com.esignlive.copytool.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.view.Process4;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.DocumentVo;
import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.SenderVo;
import com.esignlive.copytool.vo.AccountVo.SenderStatus;

public class LayoutService {
	private static LayoutService endpointService;

	private LayoutService() {
	}

	public static LayoutService getInstance() {
		return endpointService == null ? new LayoutService() : endpointService;
	}

	/**
	 * 
	 * @param String
	 *            upload original documents //to do
	 * @param String
	 *            specify partial Layout IDs //to do
	 */
	public Map<String, Boolean> copyLayout(Process4 view) {
		Map<String, Boolean> result = new LinkedHashMap<>();

		// build error msg
		StringBuilder errorMsg = new StringBuilder(200);

		// deal with copy Layout
		for (LayoutVo LayoutVo : UserData.oldEnvLayouts.values()) {
			if (LayoutVo.getIsCopy()) {
				String oldLayoutID = LayoutVo.getLayoutId();
				String oldSenderEmail = LayoutVo.getOldEnvSenderEmail();
				AccountVo newSender = UserData.newSenderMap.get(oldSenderEmail) == null ? UserData.destinationCredential
						: UserData.newSenderMap.get(oldSenderEmail);

				boolean copySuccess = false;
				try {
					String copyLayoutFromOldAccount = copyLayoutFromOldAccount(oldLayoutID, newSender);
					System.out.println(copyLayoutFromOldAccount);
					copySuccess = true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					errorMsg.append(oldLayoutID + " : " + e.getMessage()).append("\n");
				}
				view.setCopyStatus(oldLayoutID, copySuccess);
				result.put(oldLayoutID, copySuccess);
			}
		}
		view.setErrorMsg(errorMsg.toString());
		return result;
	}

	private String copyLayoutFromOldAccount(String oldLayoutID, AccountVo accountVo) throws Exception {
		String layoutId = null;
		String packageId = null;
		try {
			System.out.println("sender info: " + accountVo);
			// #step1. create a package with layout
			// prepare new template metadata
			JSONObject layoutById = PackageService.getInstance().preparePackageMetadata(oldLayoutID, accountVo,
					"PACKAGE");

			// download and document content and remove default consent
			List<DocumentVo> prepareDocument = PackageService.getInstance().prepareDocument(layoutById, oldLayoutID);

			if (prepareDocument.size() != 1) {
				throw new RuntimeException("Copying layout:" + oldLayoutID + " error!");
			}
			packageId = PackageService.getInstance().createPackageInNewEnv(accountVo, prepareDocument, layoutById);
			System.out.println("package id: " + packageId);
			// copy document visibility
			PackageService.getInstance().copyVisibility(oldLayoutID, packageId, accountVo);
			// #step2. create layout from package
			layoutId = PackageService.getInstance().createLayoutInNewEnv(accountVo, packageId,
					prepareDocument.get(0).getId(), layoutById);
			System.out.println("layout id: " + layoutId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// #step3. delete package
			if (packageId != null) {
				RestService.getInstance().doDelete(UserData.destinationApiUrl + "/packages/" + packageId);
			}
		}
		return layoutId;
	}

	public Map<String, String> getOldEnvLayouts() throws Exception {
		// set Layout list in user data
		Map<String, String> LayoutIdAndName = new LinkedHashMap<>();

		try {
			// old env owner
			retrieveLayoutsCallback(UserData.sourceCredential, LayoutIdAndName);

			// other senders
			System.out.println("old sender map: " + UserData.oldSenderMap);
			for (AccountVo apiKey : UserData.oldSenderMap.values()) {
				if (apiKey.getSenderStatus() == SenderStatus.ACTIVE) {
					retrieveLayoutsCallback(apiKey, LayoutIdAndName);
				}
			}
		} catch (Exception e) {
			// to do
			throw e;
		}
		return LayoutIdAndName;
	}

	public void retrieveLayoutsCallback(AccountVo credential, Map<String, String> layoutIdAndName)
			throws IOException, JSONException {
		Map<String, LayoutVo> oldEnvLayoutList = UserData.oldEnvLayouts;

		JSONArray resultPage1;
		int pageNum = 1;
		do {
			String url = UserData.sourceApiUrl + "layouts?from=" + pageNum + "&to=" + (pageNum + 49);
			try {
				JSONObject sendersJSON = RestService.getInstance().doGet(url, credential);

				resultPage1 = sendersJSON.getJSONArray("results");

				if (resultPage1.length() == 0) {
					break; // break loop
				}

				for (int index = 0; index < resultPage1.length(); index++) {
					JSONObject layoutJSON = resultPage1.getJSONObject(index);
					if (layoutJSON.getJSONObject("sender").getString("email")
							.equals(credential.getSenderVo().getEmail())) {
						layoutIdAndName.put(layoutJSON.getString("id"),
								layoutJSON.getString("name") + " (from " + credential.getSenderVo().getEmail() + ")");
						LayoutVo layoutVo = new LayoutVo();
						layoutVo.setIsCopy(false); // initialize
						layoutVo.setOldEnvSenderEmail(layoutJSON.getJSONObject("sender").getString("email"));
						layoutVo.setLayoutId(layoutJSON.getString("id"));
						layoutVo.setContent(layoutJSON);
						oldEnvLayoutList.put(layoutJSON.getString("id"), layoutVo);
					}
				}
			} catch (Exception e) {
				throw e;
			}
			pageNum += 50;
		} while (resultPage1.length() == 50);
	}

}
