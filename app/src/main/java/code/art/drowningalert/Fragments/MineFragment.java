package code.art.drowningalert.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;

import code.art.drowningalert.Activities.ChangePwdActivity;
import code.art.drowningalert.Activities.ChangeScrActivity;
import code.art.drowningalert.Activities.LoginActivity;
import code.art.drowningalert.Activities.PrivacyActivity;
import code.art.drowningalert.Activities.UsageDetailActivity;
import code.art.drowningalert.R;
import code.art.drowningalert.Utils.DensityUtil;
import code.art.drowningalert.Utils.SharedPreferencesUtil;
import code.art.drowningalert.widgets.PicPopupWindow;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MineFragment extends Fragment implements PicPopupWindow.OnItemClickListener {

    private String MINE_TAG="MineFragment测试: ";
    private Uri finalProfileUri;
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO =3;
    private final String photoTakenName ="output_image.jpg";
    private final String cutImageName = "cutProfilePic.jpg";
    private final String fileProvider="code.art.drowningalert.fileprovider";
    public final String UPLOAD_PROFILE_URL="http://40.73.35.185:3000/mobile/uploadProfile";
    private SharedPreferencesUtil spHelper ;
    private PicPopupWindow mPop;
    private CircleImageView userProfile;
    private TextView changePwd;
    private TextView changeScr;
    private TextView userNickname;
    private TextView aboutPrivacy;
    private TextView aboutUsage;
    private Button quitButton;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==1){
                Toast.makeText(getActivity(),"注册成功",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getActivity(),"注册失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("碎片m","oncreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("碎片m","onPause");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("碎片m", "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        initViews(view);
        initEvents(view);
        initUserData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("碎片m", "onDestroyView: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("碎片m","onStop");
    }

    @Override
    public void onStart() {
        Log.d("碎片m", "onStart: ");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("碎片m", "onDestroy: ");
    }

    private void initViews(View view ){
        Log.d("测试", "initViews: ");

        changePwd= view.findViewById(R.id.mine_change_pwd);
        changeScr = view.findViewById(R.id.mine_change_scr);
        aboutPrivacy = view.findViewById(R.id.mine_privacy);
        aboutUsage = view.findViewById(R.id.mind_about_usage);
        userProfile = view.findViewById(R.id.mine_profile);
        userNickname = view.findViewById(R.id.nav_nickname);
        quitButton = view.findViewById(R.id.btn_quit);
    }
    private void initUserData(){
        spHelper = new SharedPreferencesUtil(getActivity(),"setting");
        String profileUrl = spHelper.getString("profileUrl");

        Log.d(MINE_TAG, "initUserData: "+profileUrl);
        Glide.with(getActivity()).load(profileUrl).error(R.drawable.profile).into(userProfile);
        Log.d(MINE_TAG, "initUserData: 2---");
        userNickname.setText(spHelper.getString("nickname"));
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

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spHelper.putValues(new SharedPreferencesUtil.ContentValue("autoLogin",false));
                Intent intent =new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private Intent cutPhoto(Uri oriImageUri,String cameraPath,int flag){
        try{

            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri outputImageUri;
            File cutFile = new File(getActivity().getExternalCacheDir(),cutImageName); //裁剪之后的图片file
            if(cutFile.exists()){
                cutFile.delete();
            }
            cutFile.createNewFile();
            if(flag==CHOOSE_PHOTO){
                if(Build.VERSION.SDK_INT>=24){
                    outputImageUri = FileProvider.getUriForFile(getActivity(),fileProvider,cutFile);
                }else{

                    outputImageUri = Uri.fromFile(cutFile);//如果不做判断只调用这一句的话会提示“无法引用经过裁剪的图片”
                }
            }else {
                File filePhotoTaken = new File(cameraPath,photoTakenName);
                if (Build.VERSION.SDK_INT >= 24) {//安卓7.0
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    oriImageUri = FileProvider.getUriForFile(getActivity(),fileProvider,filePhotoTaken);
                } else {
                    oriImageUri = Uri.fromFile(filePhotoTaken);//oriImageUri指向拍摄的照片
                }
                outputImageUri = Uri.fromFile(cutFile);
            }



            intent.putExtra("crop",true);
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            intent.putExtra("outputX",DensityUtil.dip2px(getActivity(),96));
            intent.putExtra("outputY",DensityUtil.dip2px(getActivity(),96));
            intent.putExtra("scale",true);
            intent.putExtra("return-data",false);
            if(oriImageUri!=null){
                intent.setDataAndType(oriImageUri,"image/*");
            }if(outputImageUri!=null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputImageUri);
                finalProfileUri = outputImageUri;//更改成员变量imageUri，也就是将其指向裁剪后的图片，在onActivityResult的CROP_PHOTE分支中要用到
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
                File outputImageFile = new File(getContext().getExternalCacheDir(), photoTakenName);//第一个参数为目录的抽象路径名

                try{
                    if(outputImageFile.exists()){
                        outputImageFile.delete();
                    }
                    outputImageFile.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){

                    finalProfileUri = FileProvider.getUriForFile(getContext(),fileProvider,outputImageFile);
                }else{
                    finalProfileUri = Uri.fromFile(outputImageFile);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, finalProfileUri);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    String path = getActivity().getExternalCacheDir().getPath();
                    startActivityForResult(cutPhoto(null,path ,TAKE_PHOTO), CROP_PHOTO);

                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==Activity.RESULT_OK){

                    startActivityForResult(cutPhoto(data.getData(),null,CHOOSE_PHOTO), CROP_PHOTO);
                }
                break;
            case CROP_PHOTO://裁剪完图片后的回调
                try{

                    Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(finalProfileUri));
                    if(bitmap==null){
                        Glide.with(getActivity()).load( spHelper.getString("profileUrl")).error(R.drawable.profile).into(userProfile);

                    }else{
                       userProfile.setImageBitmap(bitmap);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message= new Message();
                                File profileFile=null;
                                try{
                                    profileFile=new File(new URI(finalProfileUri.toString()));

                                    OkHttpClient httpClient = new OkHttpClient();
                                    MultipartBody profileImage = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("account",spHelper.getString("name"))
                                            .addFormDataPart("profile",profileFile.getName(),RequestBody.create(MediaType.parse("image/jpg"), profileFile))
                                            .build();
                                    Request request=new Request.Builder().post(profileImage).url(UPLOAD_PROFILE_URL).build();
                                    Response response = httpClient.newCall(request).execute();
                                    String result = response.body().string();
                                    JSONObject resultObj = new JSONObject(result);
                                    spHelper.putValues(new SharedPreferencesUtil.ContentValue("profileUrl",finalProfileUri.toString()));
                                    Log.d(MINE_TAG, "run: "+spHelper.getString("profileUrl"));
                                    if(resultObj.getInt("resultcode")==1){
                                        message.what=1;
                                    }else{
                                        message.what=0;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    message.what=0;
                                }
                                handler.sendMessage(message);
                            }
                        }).start();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }
}
