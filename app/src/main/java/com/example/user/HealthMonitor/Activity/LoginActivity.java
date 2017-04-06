package com.example.user.HealthMonitor.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.http.HttpClient;
import com.example.user.HealthMonitor.R;
import com.example.user.HealthMonitor.info.UserAction;
import com.example.user.HealthMonitor.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2016/11/23.
 * 缺少数据上传的代码
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText user_account;
    private EditText user_pwd;
    private Button user_login;
    private TextView register;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    HttpClient httpClient;
    String loginStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initEvents();
    }

    protected void initViews(){
        user_account = (EditText)findViewById(R.id.accountEdit);
        user_login = (Button) findViewById(R.id.login_btn);
        user_pwd = (EditText)findViewById(R.id.pwdEdit);
        register = (TextView)findViewById(R.id.register);
        register.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        preferences = getSharedPreferences("UserInformation", MODE_PRIVATE);
        editor = preferences.edit();
    }

    protected void initEvents(){
        user_login.setOnClickListener(this);
        register.setOnClickListener(this);
    }
@Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_btn:
                if (user_account.getText().toString().equals("")||user_pwd.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "请输入账号密码",Toast.LENGTH_SHORT);
                    toast.show();
                }else {//应该对比服务器里的用户信息
                   UserAction ua = new UserAction(LoginActivity.this);
                    try {
                        String str1 = user_account.getText().toString();
                        String str2 = user_pwd.getText().toString();
                        ua.login(str1, str2, Config.ACTION_LOGIN, new UserAction.SuccessCallback() {
                            @Override
                            public void onSuccess(String jsonResult) {
                                // TODO Auto-generated method stub
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(jsonResult.toString());
                                    loginStatus = obj.getString("result");
                                }catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                if (loginStatus.equals("success")){
                                    loginStatus = null;
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                    editor.putString("UserAccount", user_account.getText().toString());
                                    editor.commit();
                                    Intent intent = new Intent();
                                    intent.setClass(LoginActivity.this, MainTabActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    loginStatus = null;
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                                }
                            }
                       }, new UserAction.FailCallback() {

                           @Override
                           public void onFail(int status, int reason) {
                                // TODO Auto-generated method stub
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("tips")
                                        .setMessage("登录失败")
                                        .setPositiveButton("确定", null)
                                        .show();
                            }
                        });
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                    break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                break;
        }
    }
}
