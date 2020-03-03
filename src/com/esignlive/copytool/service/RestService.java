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
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.utils.MimeTypeUtil;
import com.esignlive.copytool.utils.SSLFix;
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
		SSLFix.execute();

		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection(Proxy.NO_PROXY);
		sourceConn.setRequestProperty("Content-Type", "application/json; esl-api-version=11.21");
		HttpURLConnectionUtil.addCredential(sourceConn, UserData.destinationCredential);
		sourceConn.setRequestProperty("Accept", "application/json; esl-api-version=11.21");
		sourceConn.setRequestMethod("DELETE");

		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
		System.out.println(url + " : " +sourceResponseCode);

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
		SSLFix.execute();

		URL client = new URL(url);

		byte[] byteArray;
		HttpURLConnection conn = (HttpURLConnection) client.openConnection(Proxy.NO_PROXY);
		conn.setRequestProperty("Content-Type", "application/json; esl-api-version=11.21");
		HttpURLConnectionUtil.addCredential(conn, accountVo);
		conn.setRequestProperty("Accept", "application/pdf; esl-api-version=11.21");

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
		SSLFix.execute();

		URL client = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) client.openConnection(Proxy.NO_PROXY);
		conn.setRequestProperty("Accept-Encoding","UTF-8");
		conn.setRequestProperty("Accept-Charset","UTF-8");
		
		conn.setRequestProperty("Content-Type", "application/json; esl-api-version=11.21");
		HttpURLConnectionUtil.addCredential(conn, accountVo);
		conn.setRequestProperty("Accept", "application/json; esl-api-version=11.21");

		int sourceResponseCode = ((HttpURLConnection) conn).getResponseCode();
		System.out.println(url + " : " +sourceResponseCode);

		if (sourceResponseCode == 200) {
			Reader ir = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(ir);
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			conn.disconnect();
			
			return (JSONObject) JSON.parseObject(new String(response.toString().getBytes(),"UTF-8"));
		} else if (sourceResponseCode == 204) {	//no content
			return null;
		}else if (sourceResponseCode == 403) {	//no access (package has been deleted)
			return null;
		}
		else if (sourceResponseCode == 401) {
			Reader ir = new InputStreamReader(conn.getErrorStream());
			BufferedReader in = new BufferedReader(ir);
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			conn.disconnect();

			if (response.toString().contains("accountSuspended")) {
				return null;
			} else {
				throw new RuntimeException(response.toString());
			}
		} else {
			Reader ir = new InputStreamReader(conn.getErrorStream());
			BufferedReader in = new BufferedReader(ir);
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			conn.disconnect();
			throw new RuntimeException(response.toString());
		}
	}

	public JSONObject doPostMultipart(String url, AccountVo accountVo, JSONObject payloadJSON,
			List<DocumentVo> prepareDocument) throws Exception {
		SSLFix.execute();

		System.out.println(payloadJSON.toString());

		String charset = "UTF-8";
		String boundary = Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n"; // Line separator used in multipart/form-data.

		HttpsURLConnection connection = null;
		URL client = new URL(url);
		connection = (HttpsURLConnection) client.openConnection(Proxy.NO_PROXY);
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
			throw ex;
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

			return (JSONObject) JSON.parse(response.toString().getBytes("UTF-8"));
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
			throw new RuntimeException(url+" : "+response.toString());
		}
	}

	public JSONObject doPost(String url, AccountVo accountVo, JSONObject payloadJSON)
			throws IOException, JSONException {
		SSLFix.execute();

		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection(Proxy.NO_PROXY);
		sourceConn.setRequestProperty("Content-Type", "application/json; esl-api-version=11.21");
		HttpURLConnectionUtil.addCredential(sourceConn, accountVo);
		sourceConn.setRequestProperty("Accept", "application/json; esl-api-version=11.21");
		sourceConn.setRequestMethod("POST");
		sourceConn.setDoOutput(true);
		sourceConn.setDoInput(true);

		OutputStream os = sourceConn.getOutputStream();
		os.write(payloadJSON.toString().getBytes());
		os.flush();
		os.close();

		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
		System.out.println(url + " : " +sourceResponseCode);

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
				return (JSONObject) JSON.parse(response.toString().getBytes("UTF-8"));
			} catch (Exception e) {
				return null;
			}
		}
		if (sourceResponseCode == 204) {
			return null;
		} else {
			throw new RuntimeException(response.toString());
		}
	}

}
