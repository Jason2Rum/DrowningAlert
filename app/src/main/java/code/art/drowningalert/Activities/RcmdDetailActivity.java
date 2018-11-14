package code.art.drowningalert.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import code.art.drowningalert.R;
import code.art.drowningalert.Utils.GlideImageLoader;

public class RcmdDetailActivity extends AppCompatActivity {

    private ImageView rcmdImage;
    private TextView rcmdContent;
    private TextView rcmdTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcmd_detail);
        Intent callerIntent = getIntent();
        rcmdImage = findViewById(R.id.rcmdImage);
        rcmdContent = findViewById(R.id.rcmdContent);
        rcmdTitle = findViewById(R.id.rcmdTitle);

        Glide.with(this).load(callerIntent.getStringExtra("imageUrl")).error(R.drawable.sydney).into(rcmdImage);
        rcmdContent.setText(callerIntent.getStringExtra("content"));
        rcmdTitle.setText(callerIntent.getStringExtra("title"));

    }
}
