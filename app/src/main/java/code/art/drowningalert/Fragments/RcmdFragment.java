package code.art.drowningalert.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.art.drowningalert.Activities.RcmdDetailActivity;
import code.art.drowningalert.R;
import code.art.drowningalert.Utils.GlideImageLoader;

public class RcmdFragment extends Fragment {
    private ArrayList<String> list_path_imgs = new ArrayList<>();
    private ArrayList<String> list_title = new ArrayList<>();
    Banner banner;
    private String[] scenicSpotName=new String[]{"悉尼歌剧院","悉尼歌剧院","悉尼歌剧院","悉尼歌剧院","悉尼歌剧院","悉尼歌剧院"};
    private String descs[] = new String[]{"悉尼歌剧院的帆船外观则一直是悉尼灯光节的点睛之笔...","悉尼歌剧院的帆船外观则一直是悉尼灯光节的点睛之笔...",
            "悉尼歌剧院的帆船外观则一直是悉尼灯光节的点睛之笔...","悉尼歌剧院的帆船外观则一直是悉尼灯光节的点睛之笔...",
            "悉尼歌剧院的帆船外观则一直是悉尼灯光节的点睛之笔...","悉尼歌剧院的帆船外观则一直是悉尼灯光节的点睛之笔..."};
    private int[] imageIds = new int[]{
            R.drawable.icon_scenic_spot,R.drawable.icon_scenic_spot,R.drawable.icon_scenic_spot,R.drawable.icon_scenic_spot,R.drawable.icon_scenic_spot,R.drawable.icon_scenic_spot
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_rcmd,container,false);
        initBanner(view);
        List<Map<String,Object>> listItems = new ArrayList<>();
        for(int i = 0;i<scenicSpotName.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("rec_item_header",imageIds[i]);
            listItem.put("rec_item_title",scenicSpotName[i]);
            listItem.put("rec_item_desc",descs[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),listItems,R.layout.recommend_item,new String[]{"rec_item_header","rec_item_title","rec_item_desc"},
        new int[]{R.id.rec_item_header,R.id.rec_item_title,R.id.rec_item_desc});
        ListView list = view.findViewById(R.id.scenic_spots);
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().startActivity(new Intent(getActivity(),RcmdDetailActivity.class));
            }
        });
        return view;
    }

    public void initBanner(View view){
        banner = (Banner)view.findViewById(R.id.home_banner);
        banner.setImageLoader(new GlideImageLoader());
        list_path_imgs.add("http://goss2.vcg.com/creative/vcg/800/version23/VCG41172243587.jpg");
        list_path_imgs.add("http://5b0988e595225.cdn.sohucs.com/images/20180715/31d0df4e56f84524ad6088f80ba1f357.jpeg");
        list_path_imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542106119028&di=ba85d78dfcec33b8aacb0f7de9b6da02&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F1e30e924b899a9015bc5951b16950a7b0208f52f.jpg");
//        list_path_imgs.add("http://120.77.212.58:8080/picTest/book.jpg");
        list_title.add("关于溺水你所不知道的事");
        list_title.add("如何识别溺水者");
        list_title.add("如何救援溺水者");
//        list_title.add("你与开心只差一步之遥");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
//        banner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        banner.setImages(list_path_imgs);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                //在此设置点击banner的响应事件
            }
        });
        banner.setBannerTitles(list_title);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.start();
    }
}
