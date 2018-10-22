package code.art.drowningalert.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import code.art.drowningalert.R;

public class UsageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_detail);
        Toolbar toolbar = findViewById(R.id.usage_bar);
        toolbar.setTitle("使用说明");
        setSupportActionBar(toolbar);
    }
}
