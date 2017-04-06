package com.example.user.HealthMonitor.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.user.HealthMonitor.R;

/**
 * Created by user on 2016/12/5.
 */
public class InforSettingActivity extends Activity implements View.OnClickListener{

    EditText userName;
    RadioGroup userGender;
    EditText contactLastName;
    RadioGroup contactGender;
    EditText contactPhone;
    Button cancel;
    Button comfirm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_setting);
        initViews();
        initEvents();
    }

    protected void initViews(){
        userName = (EditText)findViewById(R.id.nameEdit);
        userGender = (RadioGroup) findViewById(R.id.user_gender);
        contactLastName = (EditText) findViewById(R.id.lastnameEdit);
        contactGender = (RadioGroup) findViewById(R.id.contact_gender);
        contactPhone = (EditText) findViewById(R.id.phoneEdit);
        cancel = (Button) findViewById(R.id.cancel_btn);
        comfirm = (Button) findViewById(R.id.comfirm_btn);
    }

    protected void initEvents(){
        cancel.setOnClickListener(this);
        comfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.cancel_btn:
                InforSettingActivity.this.finish();
                break;
            case R.id.comfirm_btn:
                //加入上传信息的代码
                //加入弹出toast的代码
                Intent intent = new Intent();
                intent.setClass(InforSettingActivity.this, MainTabActivity.class);
                InforSettingActivity.this.finish();
                startActivity(intent);
        }
        }
        }
