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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;

import code.art.drowningalert.R;
import code.art.drowningalert.Utils.DensityUtil;
import code.art.drowningalert.widgets.LoadingDialog;
import code.art.drowningalert.widgets.PicPopupWindow;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity implements PicPopupWindow.OnItemClickListener {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO =3;
    private final String cameraOutputImage="output_image.jpg";
    private final String cutOutPutImage = "cutProfilePic.jpg";
    private final String fileProvider="code.art.drowningalert.fileprovider";
    private final String SIGN_UP_URL = "http://120.77.212.58";
    private PicPopupWindow mPop;
    private CircleImageView userProfile;
    private Uri imageUri;
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
        signUpButton = findViewById(R.id.bt_get_pwd);
    }
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

        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText textViews[]={accountText,pwdText,nickNameText,pwdConfirmText,regionText,scrAnswerText,scrQuestionText};
                int i=0;
                for(;i<7;i++){
                    EditText editText = textViews[i];
                    if(editText.getText().toString().equals("")){
                        Toast.makeText(SignUpActivity.this,"请全部填写",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(i==7){
                    if(!pwdConfirmText.getText().toString().equals(pwdText.getText().toString())){
                        Toast.makeText(SignUpActivity.this,"密码输入不一致",Toast.LENGTH_SHORT).show();

                    }else{
                        SignUpTask signUpTask = new SignUpTask();
                        signUpTask.execute(accountText.getText().toString(), pwdText.getText().toString(),nickNameText.getText().toString(),
                                regionText.getText().toString(),scrQuestionText.getText().toString(),scrAnswerText.getText().toString());
                    }
                }
            }
        });
    }

    /**
     *
     * @param v
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
                File outputImage = new File(getExternalCacheDir(),cameraOutputImage);//第一个参数为目录的抽象路径名

                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){

                    imageUri = FileProvider.getUriForFile(SignUpActivity.this,fileProvider,outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
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
                    String name = cameraOutputImage;
                    startActivityForResult(cutPhoto(null,path ,name,TAKE_PHOTO), CROP_PHOTO);

                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){

                    startActivityForResult(cutPhoto(data.getData(),null,null,CHOOSE_PHOTO), CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                try{
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    userProfile.setImageBitmap(bitmap);
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


    private Intent cutPhoto(Uri oriImageUri,String cameraPath,String imageName,int flag){
        try{
            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri outputImageUri;
            File cutFile = new File(getExternalCacheDir(),cutOutPutImage);
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
                File filePhotoTaken = new File(cameraPath,imageName);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    oriImageUri = FileProvider.getUriForFile(this,
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
            intent.putExtra("outputX",DensityUtil.dip2px(this,96));
            intent.putExtra("outputY",DensityUtil.dip2px(this,96));
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


    private class SignUpTask extends AsyncTask<String,Integer,Boolean> {
        @Override
        protected void onPreExecute(){
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(SignUpActivity.this, "注册中", false);
            }
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(String...params){
            try{
                RequestBody requestBody = new FormBody.Builder()
                        .add("account",params[0])
                        .add("password",params[1])
                        .add("nickName",params[2])
                        .add("region",params[3])
                        .add("scrQuestion",params[4])
                        .add("scrAnswer",params[5])
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().post(requestBody).url(SIGN_UP_URL).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result){//接收doInBackground的返回值
            loadingDialog.dismiss();
            if(result){
                Toast.makeText(SignUpActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(SignUpActivity.this,"注册失败",Toast.LENGTH_SHORT).show();

            }

        }
    }
//    private String getImagePath(Uri uri,String selection){
//        String path = null;
//        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
//        if(cursor!=null){
//            if(cursor.moveToFirst()){
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return path;
//    }
//
//    @NonNull
//    private Intent cutForPhoto(Uri oriImageUri){
//        try{
//            Intent intent = new Intent("com.android.camera.action.CROP");
//            File cutFile = new File(getExternalCacheDir(),cutOutPutImage);
//            if(cutFile.exists()){
//                cutFile.delete();
//            }
//            cutFile.createNewFile();
//
//            Uri outputImageUri;
//            if(Build.VERSION.SDK_INT>=24){
//                outputImageUri = FileProvider.getUriForFile(SignUpActivity.this,fileProvider,cutFile);
//            }else{
//                outputImageUri = Uri.fromFile(cutFile);//如果不做判断只调用这一句的话会提示“无法引用经过裁剪的图片”
//            }
//            intent.putExtra("crop",true);
//            intent.putExtra("aspectX",1);
//            intent.putExtra("aspectY",1);
//            intent.putExtra("outputX",DensityUtil.dip2px(this,96));
//            intent.putExtra("outputY",DensityUtil.dip2px(this,96));
//            intent.putExtra("scale",true);
//            intent.putExtra("return-data",false);
//            if(oriImageUri!=null){
//                intent.setDataAndType(oriImageUri,"image/*");
//            }if(outputImageUri!=null){
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputImageUri);
//                imageUri = outputImageUri;//更改成员变量imageUri，也就是将其指向裁剪后的图片，在onActivityResult的CROP_PHOTE分支中要用到
//            }
//
//            intent.putExtra("noFaceDetection",true);
//            intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
//            return intent;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @NonNull
//    private Intent cutForCamera(String cameraPath,String imageName) {
//        try {
//
//            Intent intent = new Intent("com.android.camera.action.CROP");
//            //设置裁剪之后的图片路径文件
//            File cutfile = new File(getExternalCacheDir(),cutOutPutImage); //随便命名一个
//            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
//                cutfile.delete();
//            }
//            cutfile.createNewFile();
//            //初始化 uri
//            Uri oriImageUri; //拍照得到的图片的uri
//            Uri outputImageUri ; //真实的 uri
//            //拍照留下的图片
//            File filePhotoTaken = new File(cameraPath,imageName);
//            if (Build.VERSION.SDK_INT >= 24) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                oriImageUri = FileProvider.getUriForFile(this,
//                        fileProvider,
//                        filePhotoTaken);
//            } else {
//                oriImageUri = Uri.fromFile(filePhotoTaken);
//            }
//            outputImageUri = Uri.fromFile(cutfile);
//            //把这个 uri 提供出去，就可以解析成 bitmap了
//
//            // crop为true是设置在开启的intent中设置显示的view可以剪裁
//            intent.putExtra("crop",true);
//            // aspectX,aspectY 是宽高的比例，这里设置正方形
//            intent.putExtra("aspectX",1);
//            intent.putExtra("aspectY",1);
//            //设置要裁剪的宽高
//            intent.putExtra("outputX", DensityUtil.dip2px(this,96));
//            intent.putExtra("outputY",DensityUtil.dip2px(this,96));
//            intent.putExtra("scale",true);
//            //如果图片过大，会导致oom，这里设置为false
//            intent.putExtra("return-data",false);
//            if (oriImageUri != null) {
//                intent.setDataAndType(oriImageUri, "image/*");
//            }
//            if (outputImageUri != null) {
//                imageUri = outputImageUri;
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
//            }
//            intent.putExtra("noFaceDetection", true);
//            //压缩图片
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//            return intent;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @TargetApi(19)
//    private void handleImageOnKitKat(Intent data){
//        String imagePath = null;
//        Uri uri = data.getData();
//        if(DocumentsContract.isDocumentUri(this,uri)){
//            String docId = DocumentsContract.getDocumentId(uri);
//            if("com.android.providers.media.documents".equals(uri.getAuthority())){
//                String id = docId.split(":")[1];//shift+空格跳到末尾
//                String selection = MediaStore.Images.Media._ID+"="+id;
//                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
//            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
//                imagePath = getImagePath(contentUri,null);
//            }
//        }else if("content".equalsIgnoreCase(uri.getScheme())){
//            imagePath = getImagePath(uri,null);
//        }else if("file".equalsIgnoreCase(uri.getScheme())){
//            imagePath = uri.getPath();
//        }
//
//    }
//    private void handleImageBeforeKitKat(Intent data){
//        Uri uri = data.getData();
//        String imagePath = getImagePath(uri,null);
//
//
//    }
//    private void displayImage(String imagePath){
//        if(imagePath!=null){
//            Bitmap bitmap =BitmapFactory.decodeFile(imagePath);
//            userProfile.setImageBitmap(bitmap);
//        }else{
//            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
//        }
//    }

}
