package code.art.drowningalert.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import code.art.drowningalert.R;

public class ChangePwdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        Toolbar toolbar = findViewById(R.id.new_post_toolbar);
        toolbar.setTitle("更改密码");
        setSupportActionBar(toolbar);
    }
}
