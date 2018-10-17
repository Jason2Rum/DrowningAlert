package code.art.drowningalert;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import java.io.File;
import java.io.IOException;

import code.art.drowningalert.Utils.DensityUtil;
import code.art.drowningalert.widgets.PicPopupWindow;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements PicPopupWindow.OnItemClickListener {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO =3;
    private final String cameraOutputImage="output_image.jpg";
    private final String fileProvider="code.art.drowningalert.fileprovider";
    private PicPopupWindow mPop;
    private CircleImageView userProfile;
    private Uri imageUri;
    private EditText accountText;
    private EditText pwdText;
    private EditText pwdConfirmText;
    private EditText nickNameText;
    private EditText regionText;
    private EditText scrQuestionText;
    private EditText srcAnswerText;
    private Button signUpButton;


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
                    startActivityForResult(cutForCamera(path ,name), CROP_PHOTO);

                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){

                    startActivityForResult(cutForPhoto(data.getData()), CROP_PHOTO);
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

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @NonNull
    private Intent cutForPhoto(Uri oriImageUri){
        try{
            Intent intent = new Intent("com.android.camera.action.CROP");
            File cutFile = new File(getExternalCacheDir(),"cutProfilePic");
            if(cutFile.exists()){
                cutFile.delete();
            }else{
                cutFile.createNewFile();
            }
            Uri outputImageUri;
            if(Build.VERSION.SDK_INT>=24){
                outputImageUri = FileProvider.getUriForFile(SignUpActivity.this,fileProvider,cutFile);
            }else{
                outputImageUri = Uri.fromFile(cutFile);//如果不做判断只调用这一句的话会提示“无法引用经过裁剪的图片”
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

    @NonNull
    private Intent cutForCamera(String cameraPath,String imageName) {
        try {

            //设置裁剪之后的图片路径文件
            File cutfile = new File(Environment.getExternalStorageDirectory().getPath(),
                    "cutcamera.jpg"); //随便命名一个
            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri filePhotoTakenUri; //返回来的 uri
            Uri outputImageUri ; //真实的 uri
            Intent intent = new Intent("com.android.camera.action.CROP");
            //拍照留下的图片
            File filePhotoTaken = new File(cameraPath,imageName);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                filePhotoTakenUri = FileProvider.getUriForFile(this,
                        fileProvider,
                        filePhotoTaken);
            } else {
                filePhotoTakenUri = Uri.fromFile(filePhotoTaken);
            }
            outputImageUri = Uri.fromFile(cutfile);
            //把这个 uri 提供出去，就可以解析成 bitmap了
            imageUri = outputImageUri;
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop",true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", DensityUtil.dip2px(this,96));
            intent.putExtra("outputY",DensityUtil.dip2px(this,96));
            intent.putExtra("scale",true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data",false);
            if (filePhotoTakenUri != null) {
                intent.setDataAndType(filePhotoTakenUri, "image/*");
            }
            if (outputImageUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//shift+空格跳到末尾
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }

    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);


    }
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap =BitmapFactory.decodeFile(imagePath);
            userProfile.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }
}
