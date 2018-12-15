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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.utils.MimeTypeUtil;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.DocumentVo;

public class RestService {
	private static RestService endpointService;

	private RestService() {
	}

	public static RestService getInstance() {
		return endpointService == null ? new RestService() : endpointService;
	}

	public void doDelete(String url) throws IOException {
		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, UserData.destinationCredential);
		sourceConn.setRequestProperty("Accept", "application/json");
		sourceConn.setRequestMethod("DELETE");

		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
		System.out.println(sourceResponseCode);

		Reader ir = sourceResponseCode == 200 ? new InputStreamReader(sourceConn.getInputStream())
				: new InputStreamReader(sourceConn.getErrorStream());
		BufferedReader in = new BufferedReader(ir);
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		sourceConn.disconnect();

		if (sourceResponseCode != 200) {
			throw new RuntimeException(response.toString());
		}
	}

	public byte[] doGetByteArray(String url, AccountVo accountVo) throws Exception {
		URL client = new URL(url);

		byte[] byteArray;
		HttpURLConnection conn = (HttpURLConnection) client.openConnection();
		conn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(conn, accountVo);
		conn.setRequestProperty("Accept", "application/pdf");

		InputStream inputStream = conn.getInputStream();
		inputStream = conn.getInputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		buffer.close();
		inputStream.close();
		conn.disconnect();

		byteArray = buffer.toByteArray();
		return byteArray;
	}

	public JSONObject doGet(String url, AccountVo accountVo) throws IOException, JSONException {
		URL client = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) client.openConnection();
		conn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(conn, accountVo);
		conn.setRequestProperty("Accept", "application/json");

		int sourceResponseCode = ((HttpURLConnection) conn).getResponseCode();
		System.out.println(sourceResponseCode);

		Reader ir = sourceResponseCode == 200 ? new InputStreamReader(conn.getInputStream())
				: new InputStreamReader(conn.getErrorStream());
		BufferedReader in = new BufferedReader(ir);
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		conn.disconnect();

		if (sourceResponseCode == 200) {
			return new JSONObject(response.toString());
		} else if (sourceResponseCode == 204) {
			return null;
		} else if(sourceResponseCode == 401 && response.toString().contains("accountSuspended")) {
			return null;
		}
		else {
			throw new RuntimeException(response.toString());
		}
	}

	public JSONObject doPostMultipart(String url, AccountVo accountVo, JSONObject payloadJSON,
			List<DocumentVo> prepareDocument) throws Exception {
		System.out.println(payloadJSON.toString());

		String charset = "UTF-8";
		String boundary = Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n"; // Line separator used in multipart/form-data.

		HttpsURLConnection connection = null;
		URL client = new URL(url);
		connection = (HttpsURLConnection) client.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		HttpURLConnectionUtil.addCredential(connection, accountVo);
		connection.setRequestProperty("Accept", "application/json; esl-api-version=11.21");
		OutputStream output = connection.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

		try {
			for (DocumentVo documentVo : prepareDocument) {
				String docName = documentVo.getName();
				System.out.println("original doc name: " + docName);
				// Add pdf file.
				writer.append("--" + boundary).append(CRLF);
				writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + docName + "\"")
						.append(CRLF);
				String contentType = "application/pdf";
				try {
					if (documentVo.isOriginal()) {
						// contentType = URLConnection.guessContentTypeFromName(docName);
						contentType = MimeTypeUtil.getContentTypeByFileName(docName);
					}
				} catch (Exception e) {
					// to do
				}
				System.out.println(contentType + " content type");

				writer.append("Content-Type: " + contentType).append(CRLF);
				writer.append("Content-Transfer-Encoding: application/pdf").append(CRLF);
				writer.append(CRLF).flush();
				output.write(documentVo.getContent());
				output.flush();
				writer.append(CRLF).flush();
			}
			// add json payload
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"payload\"").append(CRLF);
			writer.append("Content-Type: application/json; charset=" + charset).append(CRLF);
			writer.append(CRLF).append(payloadJSON.toString()).append(CRLF).flush();

			// End of multipart/form-data.
			writer.append("--" + boundary + "--").append(CRLF).flush();
		} catch (IOException ex) {
			System.err.println(ex);
		}

		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		if (responseCode == 200) {
			// get and write out response
			System.out.println("create successfully!");
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return new JSONObject(response.toString());
		} else {
			// get and write out response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			throw new RuntimeException(response.toString());
		}
	}

	public JSONObject doPost(String url, AccountVo accountVo, JSONObject payloadJSON)
			throws IOException, JSONException {
		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, accountVo);
		sourceConn.setRequestProperty("Accept", "application/json");
		sourceConn.setRequestMethod("POST");
		sourceConn.setDoOutput(true);
		sourceConn.setDoInput(true);

		OutputStream os = sourceConn.getOutputStream();
		os.write(payloadJSON.toString().getBytes());
		os.flush();
		os.close();

		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
		System.out.println(sourceResponseCode);

		Reader ir = sourceResponseCode == 200 ? new InputStreamReader(sourceConn.getInputStream())
				: new InputStreamReader(sourceConn.getErrorStream());
		BufferedReader in = new BufferedReader(ir);
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		sourceConn.disconnect();

		if (sourceResponseCode == 200) {
			try {
				return new JSONObject(response.toString());
			} catch (Exception e) {
				return null;
			}
		} else {
			throw new RuntimeException(response.toString());
		}
	}

}
