package code.art.drowningalert.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import code.art.drowningalert.Activities.NewPostActivity;
import code.art.drowningalert.Item.PostItem;
import code.art.drowningalert.PostAdapter;
import code.art.drowningalert.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ZoneFragment extends Fragment {
    private List<PostItem> postItems  = new ArrayList<>();
    FloatingActionButton floatingActionButton ;
    RecyclerView recyclerView;
    public static String ZONE_URL="http://120.77.212.58:3000/mobile/zone";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                PostAdapter postAdapter = new PostAdapter(postItems,getActivity());
                recyclerView.setAdapter(postAdapter);
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_zone,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.post_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        floatingActionButton = view.findViewById(R.id.flbt_new_post);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getActivity(),NewPostActivity.class));
            }
        });
//        initPosts();
        return view;
}

    @Override
    public void onResume() {
        super.onResume();
        initPosts();
    }

    private void initPosts(){

//        for(int i=0;i<6;i++){
//            PostItem postItem = new PostItem();
//            postItem.setProfile(R.drawable.profile);
//            postItem.setContent("腾讯云与找钢网的合作有了进一步实质性的动作，今年9月底双方曾宣布将联手在B2B领域进行开拓，《每日经济新闻》记者查阅天眼查后发现，腾讯云与找钢网合资的胖猫云（上海）科技有限公司已正式成立，该公司注册时间为2018年10月15日，其中找钢网持股60%，腾讯持股40%。另外，找钢网创始人王东担任公司董事长，找钢网“胖猫云商”项目负责人张晓坤担任企业法定代表人。");
//            postItem.setName("小伙纸");
//            postItem.setInterestNum(String.valueOf(Math.random()*30));
//            postItem.setPostTime("2018-10-02 13:45:12");
//            postItems.add(postItem);
//        }
        postItems = new ArrayList<>();

        new Thread(new Runnable() {
            Message message = new Message();
            @Override
            public void run() {
                try{
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(ZONE_URL+"?pageNum=1&&pageSize=10").get().build();
                    Response response = okHttpClient.newCall(request).execute();
                    String result = response.body().string();
                    JSONArray postJsonArray = new JSONObject(result).getJSONArray("data");
                    JSONObject jsonObject;


                    for(int i=0;i<postJsonArray.length();i++){
                        PostItem postItem = new PostItem();
                        jsonObject=postJsonArray.getJSONObject(i);
                        postItem.setName(jsonObject.getString("nickname"));
                        postItem.setPostTime(jsonObject.getString("issueTime"));
                        postItem.setProfile(jsonObject.getString("profileUrl"));
                        postItem.setContent(jsonObject.getString("content"));
                        postItem.setInterestNum(jsonObject.getString("likeNum"));
                        postItems.add(postItem);
                    }

                }catch (Exception e){
                    e.printStackTrace();

                }
                message.what=1;
                handler.sendMessage(message);

            }
        }).start();


    }

}
