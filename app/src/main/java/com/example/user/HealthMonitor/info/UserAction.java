package com.example.user.HealthMonitor.info;
/**
 * @author MingLei Jia
 */
import android.content.Context;

import com.example.user.HealthMonitor.util.Config;

import org.json.JSONException;

public class UserAction {



	private Context context = null;

	public UserAction(Context context) {
		this.context = context;
	}

	public void login(String phoneMd5, String passwordMd5,int action,
			final SuccessCallback successCallback,
			final FailCallback failCallback) throws JSONException {

		AccountInfo accountInfo = new AccountInfo(context);
		accountInfo.login(phoneMd5, passwordMd5,action, new AccountInfo.SuccessCallback() {

			@Override
			public void onSuccess(String jsonResult) {
				if (successCallback != null) {
					successCallback.onSuccess(jsonResult);
				} else {
					failCallback.onFail(Config.STATUS_FAIL,
							Config.STATUS_FAIL_REASON_OTHER);
				}
			}
		}, new AccountInfo.FailCallback() {

			@Override
			public void onFail(int status, int reason) {
				if (failCallback != null) {
					failCallback.onFail(status, reason);
				}
			}
		});

		accountInfo = null;

	}

	public void register(String phone,String passwordMd5, int action,
			final SuccessCallback successCallback,
			final FailCallback failCallback) throws JSONException {

		AccountInfo accountInfo = new AccountInfo(context);
		accountInfo.register(phone, passwordMd5, action, new AccountInfo.SuccessCallback() {
			@Override
			public void onSuccess(String jsonResult) {
				// TODO Auto-generated method stub
				if (successCallback != null) {
					successCallback.onSuccess(jsonResult);
				} else {
					failCallback.onFail(Config.STATUS_FAIL,Config.STATUS_FAIL_REASON_OTHER);
				}
			}
		}, new AccountInfo.FailCallback() {
			@Override
			public void onFail(int status, int reason) {
				// TODO Auto-generated method stub
				if (failCallback != null) {
					failCallback.onFail(status, reason);
				}
			}
		});
		accountInfo = null;
	}
	
	public void upload(String header, String user, String state, String date, String time,int action,
			final SuccessCallback successCallback,
			final FailCallback failCallback) throws JSONException {

		DataInfo dataInfo = new DataInfo(context);
		dataInfo.upload(header, user, state, date, time, action,
				new AccountInfo.SuccessCallback() {
			@Override
			public void onSuccess(String jsonResult) {
				// TODO Auto-generated method stub
				if (successCallback != null) {
					successCallback.onSuccess(jsonResult);
				} else {
					failCallback.onFail(Config.STATUS_FAIL,Config.STATUS_FAIL_REASON_OTHER);
				}
			}
		}, new AccountInfo.FailCallback() {
			@Override
			public void onFail(int status, int reason) {
				// TODO Auto-generated method stub
				if (failCallback != null) {
					failCallback.onFail(status, reason);
				}
			}
		});
		dataInfo = null;
	}

	public void uploadLocation(String header, String user, String longitude, String latitude, String time, int action,
					   final SuccessCallback successCallback,
					   final FailCallback failCallback) throws JSONException {

		DataInfo dataInfo = new DataInfo(context);
		dataInfo.uploadLocation(header, user, longitude, latitude, time, action,
				new AccountInfo.SuccessCallback() {

					@Override
					public void onSuccess(String jsonResult) {
						// TODO Auto-generated method stub
						if (successCallback != null) {
							successCallback.onSuccess(jsonResult);
						} else {
							failCallback.onFail(Config.STATUS_FAIL,Config.STATUS_FAIL_REASON_OTHER);
						}
					}
				}, new AccountInfo.FailCallback() {

					@Override
					public void onFail(int status, int reason) {
						// TODO Auto-generated method stub
						if (failCallback != null) {
							failCallback.onFail(status, reason);
						}
					}
				});
		dataInfo = null;
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
