package code.art.drowningalert.Utils;

import android.content.Context;
import android.widget.Adapter;

import com.baidu.mapapi.map.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static void setMapCustomFile(Context context,String PATH){
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try{
            inputStream = context.getAssets().open("customConfigdir/"+PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName+"/"+PATH);
            if(f.exists()){
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(inputStream!=null){
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        MapView.setCustomMapStylePath(moduleName+"/"+PATH);
    }

}
