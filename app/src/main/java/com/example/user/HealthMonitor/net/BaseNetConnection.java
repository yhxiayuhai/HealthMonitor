package com.example.user.HealthMonitor.net;
/**
 * @author MingLei Jia
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.example.user.HealthMonitor.util.HttpMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class BaseNetConnection {

	private Context context = null;

	public BaseNetConnection(Context context) {
		this.context = context;
	}

	/**
	 * �������ӻ���
	 * 
	 * @param url
	 *            ���������ص�ַ
	 * @param httpMethod
	 *            ���󷽷�
	 * @param successCallback
	 *            �ɹ�ʱ��ص��ķ���
	 * @param failCallback
	 *            ʧ��ʱ��Ļص�����
	 * @param action
	 *            �������
	 * @param identify
	 *            �û���ݱ�ʶ
	 * @param jsonParams
	 *            ���������json�ַ���
	 */

	public BaseNetConnection(Context contex, final String url,
			final HttpMethod httpMethod, final SuccessCallback successCallback,
			final FailCallback failCallback, final int action, final String jsonParams) {

		this.context = contex;

		new AsyncTask<String, Void, String>() {//���ص���jsonResult��ֵ������null

			@Override
			protected String doInBackground(String... params) {//String...Ϊ�ɱ䳤����

				StringBuffer jsonResult = new StringBuffer();
				URLConnection conn = null;//URLConnection ��ʾӦ�ó�����URL֮���ͨ������

				String sessionId = BaseNetConnection.this.get_cashed_sessionId();

				try {
					switch (httpMethod) {
					case POST:

//						StringBuffer postParams = new StringBuffer();
//						postParams.append("jsonRequestParams").append("=")
//								.append(jsonParams);

						conn = new URL(url).openConnection();//��Ҫͨ��openConnection()����������URLConnection����
						conn.setRequestProperty("connection", "Keep-Alive");//���ø�URLConnection�ġ�connection������ͷ�ֶε�ֵΪ��Keep-Alive",��ʾ���Գ�������
						if (!sessionId
								.equals(BaseNetConnection.this.DEFAULT_SESSION_ID) && sessionId != null) {

							conn.setRequestProperty("Cookie", "JSESSIONID="
									+ sessionId);//���ͷ��Ϣ��cookie�͵�������
						}
						conn.setDoInput(true);//����POST���������������������ͷ�ֶε�ֵΪ��
						conn.setDoOutput(true);
						conn.connect();//����ʵ�ʵ�����

						// ���������д��ȥ
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(conn.getOutputStream(),//��ȡURLConnection�����Ӧ�������������д����Դ
										"UTF-8"));
						bw.write(jsonParams.toString());
						bw.flush();//������Ļ���
						bw.close();


						break;

					case GET:
						if (sessionId
								.equals(BaseNetConnection.this.DEFAULT_SESSION_ID)) {
							conn = new URL(url + "?" + "jsonRequestParams"
									+ "=" + jsonParams).openConnection();//�򿪺�URL֮�������
						} else {

							conn = new URL(url + "?" + "jsonRequestParams"
									+ "=" + jsonParams + ";" + "JSESSIONID="
									+ sessionId).openConnection();
						}
						conn.setRequestProperty("connection", "Keep-Alive");
						conn.connect();
						break;

					}

					// ��ȡ���������صĽ��������ȡURL����Ӧ��
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"utf-8"));
					String line = "";
					while ((line = br.readLine()) != null) {
						jsonResult.append(line);
					}

					br.close();

					return jsonResult.toString();

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				if (result != null) {
					if (successCallback != null) {

						// ���������ص�json���β������sessionId
						String[] results = result.split("#");
						if (results.length > 1) {
							result = results[0];
							BaseNetConnection.this.cash_sessionId(results[1]);
						}
						successCallback.onSuccess(result);
					}

				} else {
					if (failCallback != null) {
						failCallback.onFail(0,0);
					}
				}
			}
		}.execute();

	}

	// �ɹ��ص�����
	public interface SuccessCallback {

		void onSuccess(String result);
	}

	// ʧ�ܻص�����
	public interface FailCallback {
		void onFail(int status, int reason);
	}

	private String SHAREDPREFERENCE_SESSION = "SharedPreferences_sessionId";
	private String SESSION_ID = "sessionId";
	private String DEFAULT_SESSION_ID = "-1";

	/**
	 * �����������ص�sessionId���ڱ���
	 * 
	 * @param sessionId
	 *            sessionId
	 */
	public void cash_sessionId(String sessionId) {

		SharedPreferences sd = context.getSharedPreferences(
				this.SHAREDPREFERENCE_SESSION, Context.MODE_PRIVATE);//д������ݻḲ��ԭ�ļ�������
		Editor e = sd.edit();
		e.putString(this.SESSION_ID, sessionId);
		e.commit();
		
	}

	/**
	 * ��ȡ���ڱ��ص�sessionId
	 * 
	 * @return
	 */
	private String get_cashed_sessionId() {
		SharedPreferences sd = context.getSharedPreferences(
				this.SHAREDPREFERENCE_SESSION, Context.MODE_PRIVATE);
		return sd.getString(this.SESSION_ID, this.DEFAULT_SESSION_ID);
	}

}
