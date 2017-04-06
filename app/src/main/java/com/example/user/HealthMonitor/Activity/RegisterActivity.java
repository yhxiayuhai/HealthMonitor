package com.example.user.HealthMonitor.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.http.HttpClient;
import com.example.user.HealthMonitor.R;
import com.example.user.HealthMonitor.info.UserAction;
import com.example.user.HealthMonitor.util.Config;

import org.json.JSONException;

/**
 * Created by user on 2016/11/22.
 */
public class RegisterActivity extends Activity implements View.OnClickListener{

    private EditText user_phone;
    private EditText user_pwd;
    private Button user_next;
    private Button user_cancel;
    HttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        initEvents();
    }

    protected void initViews(){
        user_phone = (EditText)findViewById(R.id.phone_numberEdit);
        user_phone.requestFocus();
        user_pwd = (EditText)findViewById(R.id.pwdEdit);
        user_next = (Button)findViewById(R.id.next_btn);
        user_cancel = (Button)findViewById(R.id.cancel_btn);
    }

    protected void initEvents(){
        user_next.setOnClickListener(this);
        user_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.next_btn:
                if (user_phone.getText().toString().equals("")||user_pwd.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "请输入账号密码",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    UserAction ua = new UserAction(RegisterActivity.this);
                    try {
                        String str1 = user_phone.getText().toString();
                        String str2 = user_pwd.getText().toString();
                        ua.register(str1, str2, Config.ACTION_REGISTER, new UserAction.SuccessCallback() {
                            @Override
                            public void onSuccess(String jsonResult) {
                                // TODO Auto-generated method stub
                                Toast.makeText(RegisterActivity.this,"恭喜注册成功！",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(RegisterActivity.this, MainTabActivity.class);//将来需要修改，跳转界面
                             //   RegisterActivity.this.finish();
                                startActivity(intent);
                            }
                        }, new UserAction.FailCallback() {

                            @Override
                            public void onFail(int status, int reason) {
                                // TODO Auto-generated method stub
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("tips")
                                        .setMessage("失败了，啊哦~")
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
            case R.id.cancel_btn:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

}
