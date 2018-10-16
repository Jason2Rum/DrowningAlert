package code.art.drowningalert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.PopupWindow;

import code.art.drowningalert.widgets.PicPopupWindow;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements PicPopupWindow.OnItemClickListener {
    private PicPopupWindow mPop;
    private CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        initEvents();

    }
    private void initViews(){
        profile = findViewById(R.id.profile_image);

    }
    private void initEvents(){
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop=new PicPopupWindow(SignUpActivity.this,"");
                mPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                mPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mPop.setOnItemClickListener(SignUpActivity.this);
                mPop.showAtLocation(findViewById(R.id.sign_up_layout),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
            }
        });
    }


    @Override
    public void setOnItemClick(View v,String path){
        switch (v.getId()){
            case R.id.btn_take_photo:
                mPop.dismiss();
                break;
            case R.id.btn_select_photo:
                mPop.dismiss();
                break;
            case R.id.btn_cancel:
                mPop.dismiss();
                break;
        }
    }
}
