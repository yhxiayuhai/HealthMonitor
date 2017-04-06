package com.example.user.HealthMonitor.info;

import android.content.Context;

import com.example.user.HealthMonitor.info.AccountInfo.FailCallback;
import com.example.user.HealthMonitor.info.AccountInfo.SuccessCallback;
import com.example.user.HealthMonitor.net.Netconnection;
import com.example.user.HealthMonitor.util.Config;
import com.example.user.HealthMonitor.util.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

public class DataInfo {

	private Context context = null;
	public DataInfo(Context context) {
		this.context = context;
	}
	
	/**
	 * �����ϴ�����
	 */
	public void upload(String header, String user, String state, String date, String time,
			int action,
			final SuccessCallback successCallback,
			final FailCallback failCallback) throws JSONException {

		// ���������ת����json�ַ���
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(AccountInfo.HEADER, header);
		jsonObject.put(AccountInfo.USER, user);
		jsonObject.put(AccountInfo.STATE, state);
		jsonObject.put(AccountInfo.DATE, date);
		jsonObject.put(AccountInfo.TIME, time);
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

	public void uploadLocation(String header, String user, String longitude, String latitude,String time,
					   int action,
					   final SuccessCallback successCallback,
					   final FailCallback failCallback) throws JSONException {

		// ���������ת����json�ַ���
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(AccountInfo.HEADER, header);
		jsonObject.put(AccountInfo.USER, user);
		jsonObject.put(AccountInfo.LONGITUDE, longitude);
		jsonObject.put(AccountInfo.LATITUDE, latitude);
		jsonObject.put(AccountInfo.TIME, time);
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
}
