package code.art.drowningalert.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.art.drowningalert.Activities.BannerDetailActivity;
import code.art.drowningalert.Activities.RcmdDetailActivity;
import code.art.drowningalert.R;
import code.art.drowningalert.Utils.GlideImageLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RcmdFragment extends Fragment {

    private String TEST_TAG="RcmdFragment测试: ";

    private ArrayList<String> list_path_imgs = new ArrayList<>();
    private ArrayList<String> list_title = new ArrayList<>();
    List<Map<String,Object>> listItems ;
    Banner banner;
    ListView rcmdList;
    private String[] scenicSpotName;
    private String descs[] ;
    private String[] imageUrl;
    private String []content;
    public static final String RECOMMEND_LIST = "http://120.77.212.58:3000/mobile/recommend";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                Log.d(TEST_TAG, "handleMessage: ");

                SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),
                        listItems,//要填充的信息
                        R.layout.recommend_item,//列表子项布局
                        new String[]{"rec_item_header","rec_item_title","rec_item_desc"},//填充信息的头部
                        new int[]{R.id.rec_item_header,R.id.rec_item_title,R.id.rec_item_desc});//填充信息对应的布局控件id

                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if(view instanceof ImageView ){
                            String imageUrl = (String)data;
                            Glide.with(getActivity()).load(imageUrl).error(R.drawable.sydney).into((ImageView)view);
                        }
                        return false;
                    }
                });
                simpleAdapter.notifyDataSetChanged();
                rcmdList.setAdapter(simpleAdapter);

            }
        }
    };






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_rcmd,container,false);
        initBanner(view);
        rcmdList = view.findViewById(R.id.scenic_spots);
        rcmdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),RcmdDetailActivity.class);
                intent.putExtra("imageUrl",imageUrl[position]);
                intent.putExtra("content",content[position]);
                intent.putExtra("title",scenicSpotName[position]);
                getActivity().startActivity(intent);
            }
        });

        Log.d(TEST_TAG, "onCreateView: ");
        initRec();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void initRec() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                try{
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(RECOMMEND_LIST).get().build();
                    Response response = okHttpClient.newCall(request).execute();
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    imageUrl= new String[jsonArray.length()];
                    scenicSpotName= new String[jsonArray.length()];
                    descs=new String[jsonArray.length()];
                    content = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        imageUrl[i] = "http://120.77.212.58:3000/" + obj.getString("imageUrl");
                        scenicSpotName[i] = obj.getString("title");
                        descs[i] = obj.getString("content").substring(0, 16) + "...";
                        content[i] = obj.getString("content");
                    }
                    listItems = new ArrayList<>();
                    for(int i = 0;i<scenicSpotName.length;i++){
                        Map<String,Object> listItem = new HashMap<String,Object>();
                        listItem.put("rec_item_header", imageUrl[i]);
                        listItem.put("rec_item_title",scenicSpotName[i]);
                        listItem.put("rec_item_desc",descs[i]);
                        listItems.add(listItem);
                    }
                    message.what=1;
                }catch (Exception e){
                    e.printStackTrace();
                    message.what=0;
                }
                handler.sendMessage(message);
            }
        }).start();
    }


    public void initBanner(View view){
        banner = (Banner)view.findViewById(R.id.home_banner);
        banner.setImageLoader(new GlideImageLoader());
        list_path_imgs.add("http://goss2.vcg.com/creative/vcg/800/version23/VCG41172243587.jpg");
        list_path_imgs.add("http://5b0988e595225.cdn.sohucs.com/images/20180715/31d0df4e56f84524ad6088f80ba1f357.jpeg");
        list_path_imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542106119028&di=ba85d78dfcec33b8aacb0f7de9b6da02&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F1e30e924b899a9015bc5951b16950a7b0208f52f.jpg");
        list_title.add("关于溺水你所不知道的事");
        list_title.add("如何识别溺水者");
        list_title.add("如何救援溺水者");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);

        banner.setImages(list_path_imgs);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                startActivity(new Intent(getActivity(),BannerDetailActivity.class));
            }
        });
        banner.setBannerTitles(list_title);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.start();
    }
}
