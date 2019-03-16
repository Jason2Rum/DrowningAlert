package code.art.drowningalert;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import code.art.drowningalert.Item.PostItem;
import code.art.drowningalert.Utils.SharedPreferencesUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostItem> mPostItemList;
    private Context mContext;
    public static final String LIKE_URL="http://120.77.212.58:3000/mobile/like";
    public PostAdapter (List<PostItem> postItemList, Context context){
        mContext=context;
        mPostItemList = postItemList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView postProfile;
        TextView postName;
        TextView postTime;
        TextView postContent;
        ImageView postInterestImage;
        TextView postInterestNum;

        public ViewHolder (View view){
            super(view);
            postProfile = view.findViewById(R.id.post_profile);
            postName = view.findViewById(R.id.post_nickname);
            postTime = view.findViewById(R.id.post_issue_time);
            postContent = view.findViewById(R.id.post_content);
            postInterestImage = view.findViewById(R.id.post_interest);
            postInterestNum = view.findViewById(R.id.post_interest_count);

            postInterestImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postInterestImage.setImageResource(R.drawable.icon_thumb_click);
                    postInterestNum.setText(String.valueOf(Integer.parseInt(postInterestNum.getText().toString())+1));
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            OkHttpClient okHttpClient = new OkHttpClient();
//                            Request request = new Request.Builder().url(LIKE_URL+"?postId"+).get().build();
//                        }
//                    })


                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        PostItem postItem = mPostItemList.get(position);
//        holder.postProfile.setImageResource(postItem.getProfile());
        Glide.with(mContext).load("http://40.73.35.185:3000/"+mPostItemList.get(position).getProfile()).into(holder.postProfile);
        holder.postInterestNum.setText(postItem.getInterestNum());
        holder.postName.setText(postItem.getName());
        holder.postContent.setText(postItem.getContent());


    }

    @Override
    public int getItemCount(){
        return mPostItemList.size();
    }
}
