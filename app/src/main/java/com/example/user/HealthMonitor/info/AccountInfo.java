package com.example.user.HealthMonitor.info;
/**
 * @author MingLei Jia
 */
import android.content.Context;

import com.example.user.HealthMonitor.net.Netconnection;
import com.example.user.HealthMonitor.util.Config;
import com.example.user.HealthMonitor.util.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountInfo {

	public static final String HEADER = "header";
	public static final String PHONE = "phone";
//	public static final String NAME = "userName";
	public static final String PASSWORD_MD5 = "passwordMd5";
	public static final String ACTION = "action";
	public static final String USER = "user";
	public static final String STATE = "state";
	public static final String DATE = "date";
	public static final String TIME = "time";
	public static final String LONGITUDE= "longitude";
	public static final String LATITUDE = "latitude";
	public static final String LOCATION = "location";
	// ��¼ʱ��һЩ״̬��
	public static final int STATUS_LOGIN_SUCCESS = 1;
	public static final int STATUS_LOGIN_FAIL = 0;
	public static final int FAIL_REASON_ACCOUNT_FAIL = 1;
	public static final int FAIL_REASON_OTHER = 0;

	// ��¼ʱ��һЩ״̬��
	public static final int STATUS_REGISTER_SUCCESS = 1;
	public static final int STATUS_REGISTER_FAIL = 0;
	public static final int FAIL_REASON_MSG_CODE_NOT_CORRECT = -1;
	public static final int FAIL_REASON_PHONE_REGISTERED = 1;

	private Context context = null;
	public AccountInfo(Context context) {
		this.context = context;
	}


	public void register(String phone, String passwordMd5, int action,
			final SuccessCallback successCallback,
			final FailCallback failCallback) throws JSONException {

		// ���������ת����json�ַ���
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(AccountInfo.PHONE, phone);
//		map.put(AccountInfo.PASSWORD_MD5, passwordMd5);

//		String phoneParams = JsonTool.createJsonString(AccountInfo.PHONE, phone);
//		String pwParams = JsonTool.createJsonString(AccountInfo.PASSWORD_MD5, passwordMd5);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(AccountInfo.PHONE, phone);
		jsonObject.put(AccountInfo.PASSWORD_MD5, passwordMd5);
		jsonObject.put(AccountInfo.ACTION, action);
		
//		JSONArray jsonMembers = new JSONArray(); 
//		jsonMembers.put(jsonObject);
		
		System.out.println("JSON: "+jsonObject.toString());
//		System.out.println("map: "+map);
//		String jsonRequestParams = JsonTool.createJsonString(
//				JsonTool.JSON_REQUEST_PARAMS, map);
//		map = null;
		new Netconnection(context,Config.GATE_URL, HttpMethod.POST,
				new Netconnection.SuccessCallback() {
					@Override
					public void onSuccess(String result) {

						if (successCallback != null) {
							successCallback.onSuccess(result);
						}
					}
				}, new Netconnection.FailCallback() {
					@Override
					public void onFail(int status, int reason) {

						if (failCallback != null) {
							failCallback.onFail(status, reason);
						}
					}
				}, action, jsonObject.toString());
	}


	public void login(String phone, String passwordMd5,int action,
			final SuccessCallback successCallback,
			final FailCallback failCallback) throws JSONException {

		// ���������ת����json�ַ���
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(AccountInfo.PHONE, phone);
		jsonObject.put(AccountInfo.PASSWORD_MD5, passwordMd5);
		jsonObject.put(AccountInfo.ACTION, action);
		


		// ��������������ӷ�����
		new Netconnection(context,Config.GATE_URL, HttpMethod.POST,
				new Netconnection.SuccessCallback() {
					@Override
					public void onSuccess(String result) {

						if (successCallback != null) {
							successCallback.onSuccess(result);
						}
					}
				}, new Netconnection.FailCallback() {
					@Override
					public void onFail(int status, int reason) {

						if (failCallback != null) {
							failCallback.onFail(status, reason);
						}
					}
				}, action, jsonObject.toString());

	}


	

	
	/**
	 * �ɹ��ص�����
	 *
	 */
	public interface SuccessCallback {
		void onSuccess(String jsonResult);
	}

	/**
	 * ʧ�ܻص�����
	 */
	public interface FailCallback {
		void onFail(int status, int reason);
	}

}
