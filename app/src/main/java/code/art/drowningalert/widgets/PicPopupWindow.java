package code.art.drowningalert.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.view.View;

import java.io.File;

import code.art.drowningalert.R;
import code.art.drowningalert.Utils.DensityUtil;

public class PicPopupWindow extends PopupWindow implements View.OnClickListener {
    private Button picFromCameraBtn;
    private Button picFromPhotosBtn;
    private Button cancelBtn;

    private View mPopWindow;

    private Context mContext;
    private OnItemClickListener mListener;



    public PicPopupWindow(Context context){
        super(context);

        this.mContext = context;
        init(context);
        setPopupWindow();
        picFromPhotosBtn.setOnClickListener(this);
        picFromCameraBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

    }
    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        mPopWindow = inflater.inflate(R.layout.popup_pic,null);
        picFromCameraBtn = mPopWindow.findViewById(R.id.btn_take_photo);
        picFromPhotosBtn = mPopWindow.findViewById(R.id.btn_select_photo);
        cancelBtn = mPopWindow.findViewById(R.id.btn_cancel);
    }

    private void setPopupWindow(){
        this.setContentView(mPopWindow);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(DensityUtil.dip2px(mContext,130));
        this.setFocusable(true);
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mPopWindow.findViewById(R.id.layout_pic_popup).getTop();
                int y = (int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height)
                        dismiss();
                }
                return true;
            }
        });
    }
    public interface OnItemClickListener{
        void setOnItemClick(View view);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    @Override
    public void onClick(View v){
        if(mListener!=null){
            mListener.setOnItemClick(v);
        }
    }



}
