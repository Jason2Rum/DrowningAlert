package code.art.drowningalert.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import code.art.drowningalert.R;
import code.art.drowningalert.SignUpInfo;
import code.art.drowningalert.Utils.DensityUtil;
import code.art.drowningalert.widgets.LoadingDialog;
import code.art.drowningalert.widgets.PicPopupWindow;
import code.art.drowningalert.widgets.SingleOptionsPicker;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class SignUpActivity extends AppCompatActivity implements PicPopupWindow.OnItemClickListener {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO =3;
    private final String photoTakenName="output_image.jpg";  //照相机拍照的图片输出路径
    private final String cutImageName = "cutProfilePic.jpg"; //裁剪后的图片的输出路径
    private final String fileProvider="code.art.drowningalert.fileprovider";
    private final String SIGN_UP_URL = "http://120.77.212.58:3000/mobile/signup";
    private final String UPLOAD_PROFILE_URL="http://120.77.212.58:3000/mobile/uploadProfile";
    private PicPopupWindow mPop;
    private CircleImageView userProfile;
    private Uri finalProfileUri;
    private EditText accountText;
    private EditText pwdText;
    private EditText pwdConfirmText;
    private EditText nickNameText;
    private EditText regionText;
    private EditText scrQuestionText;
    private EditText scrAnswerText;
    private Button signUpButton;

    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        initEvents();

    }
    private void initViews(){
        userProfile = findViewById(R.id.profile_image);
        accountText = findViewById(R.id.et_account);
        pwdConfirmText = findViewById(R.id.et_confirm_pwd);
        pwdText = findViewById(R.id.et_pwd);
        nickNameText = findViewById(R.id.et_nickname);
        regionText = findViewById(R.id.et_region);
        scrQuestionText = findViewById(R.id.et_scr_question);
        scrAnswerText = findViewById(R.id.et_answer);
        signUpButton = findViewById(R.id.btn_login);
    }

    /**
     * 头像按钮事件 和 注册按钮事件
     */
    private void initEvents(){
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop=new PicPopupWindow(SignUpActivity.this);
                mPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                mPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mPop.setOnItemClickListener(SignUpActivity.this);
                mPop.showAtLocation(findViewById(R.id.sign_up_layout),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
            }
        });
        regionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> regionList = new ArrayList<>();
                regionList.add("江安");
                regionList.add("望江");
                SingleOptionsPicker.openOptionsPicker(SignUpActivity.this,regionList,1,regionText);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(finalProfileUri==null){
                    Toast.makeText(SignUpActivity.this,"请选择头像",Toast.LENGTH_SHORT).show();
                }
                EditText textViews[]={accountText,pwdText,nickNameText,pwdConfirmText,regionText,scrAnswerText,scrQuestionText};
                int i=0;
                for(;i<7;i++){
                    EditText editText = textViews[i];
                    if(editText.getText().toString().equals("")){
                        Toast.makeText(SignUpActivity.this,"请全部填写",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                }
                if(i==7){
                    if(!pwdConfirmText.getText().toString().equals(pwdText.getText().toString())){
                        Toast.makeText(SignUpActivity.this,"密码输入不一致",Toast.LENGTH_SHORT).show();

                    }else{
                        File profileFile=null;
                            try{
                                profileFile=new File(new URI(finalProfileUri.toString()));
                            }catch (URISyntaxException e){
                                Log.d("上传图片错误","URISyntaxException");
                            }

                        SignUpInfo signUpInfo = new SignUpInfo(profileFile,accountText.getText().toString(), pwdText.getText().toString(),nickNameText.getText().toString(),
                                regionText.getText().toString(),scrQuestionText.getText().toString(),scrAnswerText.getText().toString());

                        SignUpTask signUpTask = new SignUpTask();
                        signUpTask.execute(signUpInfo);
                    }
                }
            }
        });
    }

    /**
     *
     * @param v 单击的PicPopupWindow中的view
     * 活动继承了PicPopupWindow.OnItemClickListener 所以要重写setOnItemClick
     *
     * 打开相机的逻辑：首先创建一个File，两个参数分别为 The parent abstract pathname,The child pathname string（相当于在SD卡中的某一个特定路径下，
     * 开辟了一块空间）。然后利用FileProvider获取这个刚创建file的Uri,这时候需要提供一个自定义的provider，对应依据是authority，因此我要在
     * AndroidManifest中创建一个provider，其中meta-data配置的是我们可以访问的文件的路径配置信息，meta-data的name属性不可改变。获取到uri之后，把他传入到intent中
     * 相当于把开辟的那个空间的地址传入给了intent，这样拍照的时候，就会把照片放到我们指定的名字，指定的路径上去了
     */
    @Override
    public void setOnItemClick(View v){

        switch (v.getId()){
            case R.id.btn_take_photo:

                mPop.dismiss();
                File externalCacheDir = getExternalCacheDir();
                File outputImageFile = new File(externalCacheDir,photoTakenName);//第一个参数为目录的抽象路径名
                //outputImageFile 为照片file
                // /storage/emulated/0/Android/data/code.art.drowingalert/cache/output_image
                try{
                    if(outputImageFile.exists()){
                        outputImageFile.delete();
                    }
                    outputImageFile.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){

                    finalProfileUri = FileProvider.getUriForFile(SignUpActivity.this,fileProvider, outputImageFile);
                }else{
                    // file:///storage/emulated/0/Android/data/code.art.drowingalert/cache/output_image.jpg
                    finalProfileUri = Uri.fromFile(outputImageFile);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,finalProfileUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;

            case R.id.btn_select_photo:
                mPop.dismiss();
                if(ContextCompat.checkSelfPermission(SignUpActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();//会开启一个intent
                }

                break;
            case R.id.btn_cancel:
                mPop.dismiss();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    String path = this.getExternalCacheDir().getPath();
                    startActivityForResult(cutPhoto(null,path ,TAKE_PHOTO), CROP_PHOTO);

                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){

                    startActivityForResult(cutPhoto(data.getData(),null,CHOOSE_PHOTO), CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                try{

                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(finalProfileUri));
                    if(bitmap==null){
                        userProfile.setImageResource(R.drawable.profile);
                    }else{
                        userProfile.setImageBitmap(bitmap);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[]permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /**
     *
     * @param oriImageUri 要裁剪的图片的目录，当标志位为TAKE_PHOTO，oriImageUri重新指向拍摄的照片，当标志位为CHOOSE_PHOTO,oriImageUri直接指向参数，也就是选择的照片的uri
     * @param cameraPath 照相机拍的照片存储的目录
     * @param flag 相册选择还是拍照的标志位
     * @return 裁剪图片的intent
     */
    private Intent cutPhoto(Uri oriImageUri,String cameraPath,int flag){
        try{

            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri outputImageUri;
            File cutFile = new File(getExternalCacheDir(),cutImageName); //裁剪之后的图片file
            if(cutFile.exists()){
                cutFile.delete();
            }
            cutFile.createNewFile();
            if(flag==CHOOSE_PHOTO){
                if(Build.VERSION.SDK_INT>=24){
                    outputImageUri = FileProvider.getUriForFile(SignUpActivity.this,fileProvider,cutFile);
                }else{

                    outputImageUri = Uri.fromFile(cutFile);//如果不做判断只调用这一句的话会提示“无法引用经过裁剪的图片”
                }
            }else {
                File filePhotoTaken = new File(cameraPath,photoTakenName);
                if (Build.VERSION.SDK_INT >= 24) {//安卓7.0
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    oriImageUri = FileProvider.getUriForFile(this,
                            fileProvider,
                            filePhotoTaken);
                } else {
                    oriImageUri = Uri.fromFile(filePhotoTaken);//oriImageUri指向拍摄的照片
                }
                outputImageUri = Uri.fromFile(cutFile);
            }



            intent.putExtra("crop",true);
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            intent.putExtra("outputX",DensityUtil.dip2px(this,96));
            intent.putExtra("outputY",DensityUtil.dip2px(this,96));
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




    private class SignUpTask extends AsyncTask<SignUpInfo,Integer,Boolean> {
        @Override
        protected void onPreExecute(){
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(SignUpActivity.this, "注册中", false);
            }
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(SignUpInfo...params){
            try{
                SignUpInfo userInfo = params[0];

                RequestBody basicInfo = new FormBody.Builder()
                        .add("account",userInfo.getAccount())
                        .add("password",userInfo.getPassword())
                        .add("nickname",userInfo.getNickname())
                        .add("region",userInfo.getRegion())
                        .add("scrQuestion",userInfo.getScrQuestion())
                        .add("scrAnswer",userInfo.getScrAnswer())
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().post(basicInfo).url(SIGN_UP_URL).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                JSONObject basicResult = new JSONObject(responseData);
                int basicResultCode = basicResult.getInt("resultcode");
                String basicResultMsg = basicResult.getString("msg");
                if(basicResultMsg.equals("MOBILE_ALREADY_EXIST")){
                    basicResultCode=1;//如果用户基本信息已经存在，重新设置basicResultCode
                }

                MultipartBody profileImage = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("account",userInfo.getAccount())
                        .addFormDataPart("profile",userInfo.getProfile().getName(),RequestBody.create(MediaType.parse("image/jpg"), userInfo.getProfile()))
                        .build();



                request=new Request.Builder().post(profileImage).url(UPLOAD_PROFILE_URL).build();
                response = client.newCall(request).execute();

                responseData = response.body().string();


                JSONObject profileResult = new JSONObject(responseData);
                int profileResultCode = profileResult.getInt("resultcode");

                return basicResultCode==1&&profileResultCode==1;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result){//接收doInBackground的返回值
            loadingDialog.dismiss();
            if(result){
                Toast.makeText(SignUpActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent();
                loginIntent.putExtra("account",accountText.getText().toString());//这里一定要toString，不然前一个活动获取到的参数会是null
                loginIntent.putExtra("password",pwdText.getText().toString());
                setResult(RESULT_OK,loginIntent);
                finish();
            }else {
                Toast.makeText(SignUpActivity.this,"注册失败",Toast.LENGTH_SHORT).show();

            }

        }
    }




}
