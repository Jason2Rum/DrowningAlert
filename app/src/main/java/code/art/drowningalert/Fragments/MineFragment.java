package code.art.drowningalert.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;

import code.art.drowningalert.Activities.ChangePwdActivity;
import code.art.drowningalert.Activities.ChangeScrActivity;
import code.art.drowningalert.Activities.PrivacyActivity;
import code.art.drowningalert.Activities.SignUpActivity;
import code.art.drowningalert.Activities.UsageDetailActivity;
import code.art.drowningalert.R;
import code.art.drowningalert.Utils.DensityUtil;
import code.art.drowningalert.widgets.PicPopupWindow;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends Fragment implements PicPopupWindow.OnItemClickListener {

    private Uri imageUri;
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO =3;
    private final String cameraOutputImage="output_image.jpg";
    private final String cutOutPutImage = "cutProfilePic.jpg";
    private final String fileProvider="code.art.drowningalert.fileprovider";
    private final String SIGN_UP_URL = "http://120.77.212.58";

    private PicPopupWindow mPop;
    private CircleImageView userProfile;
    private TextView changePwd;
    private TextView changeScr;
    private TextView aboutPrivacy;
    private TextView aboutUsage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        initViews(view);
        initEvents(view);
        return view;
    }
    private void initViews(View view ){
        changePwd= view.findViewById(R.id.mine_change_pwd);
        changeScr = view.findViewById(R.id.mine_change_scr);
        aboutPrivacy = view.findViewById(R.id.mine_privacy);
        aboutUsage = view.findViewById(R.id.mind_about_usage);
        userProfile = view.findViewById(R.id.mine_profile);
    }

    private void initEvents(final View view){

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop=new PicPopupWindow(getActivity());
                mPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                mPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mPop.setOnItemClickListener(MineFragment.this);
                mPop.showAtLocation(view.findViewById(R.id.mine_frg_layout),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
            }
        });
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),ChangePwdActivity.class));
            }
        });
        changeScr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),ChangeScrActivity.class));
            }
        });
        aboutPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),PrivacyActivity.class));
            }
        });
        aboutUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),UsageDetailActivity.class));
            }
        });
    }

    private Intent cutPhoto(Uri oriImageUri, String cameraPath, String imageName, int flag){
        try{
            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri outputImageUri;
            File cutFile = new File(getContext().getExternalCacheDir(),cutOutPutImage);
            if(cutFile.exists()){
                cutFile.delete();
            }
            cutFile.createNewFile();
            if(flag==CHOOSE_PHOTO){
                if(Build.VERSION.SDK_INT>=24){
                    outputImageUri = FileProvider.getUriForFile(getContext(),fileProvider,cutFile);
                }else{
                    outputImageUri = Uri.fromFile(cutFile);//如果不做判断只调用这一句的话会提示“无法引用经过裁剪的图片”
                }
            }else {
                File filePhotoTaken = new File(cameraPath,imageName);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    oriImageUri = FileProvider.getUriForFile(getContext(),
                            fileProvider,
                            filePhotoTaken);
                } else {
                    oriImageUri = Uri.fromFile(filePhotoTaken);
                }
                outputImageUri = Uri.fromFile(cutFile);
            }

            intent.putExtra("crop",true);
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            intent.putExtra("outputX",DensityUtil.dip2px(getContext(),96));
            intent.putExtra("outputY",DensityUtil.dip2px(getContext(),96));
            intent.putExtra("scale",true);
            intent.putExtra("return-data",false);
            if(oriImageUri!=null){
                intent.setDataAndType(oriImageUri,"image/*");
            }if(outputImageUri!=null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputImageUri);
                imageUri = outputImageUri;//更改成员变量imageUri，也就是将其指向裁剪后的图片，在onActivityResult的CROP_PHOTE分支中要用到
            }

            intent.putExtra("noFaceDetection",true);
            intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
            return intent;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setOnItemClick(View v){
        switch (v.getId()){
            case R.id.btn_take_photo:
                mPop.dismiss();
                File outputImage = new File(getContext().getExternalCacheDir(),cameraOutputImage);//第一个参数为目录的抽象路径名

                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){

                    imageUri = FileProvider.getUriForFile(getContext(),fileProvider,outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;

            case R.id.btn_select_photo:
                mPop.dismiss();
                if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();//会开启一个intent
                }

                break;
            case R.id.btn_cancel:
                mPop.dismiss();
                break;
        }
    }
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

}
