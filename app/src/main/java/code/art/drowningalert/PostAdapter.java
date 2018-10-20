package code.art.drowningalert;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import code.art.drowningalert.Item.PostItem;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostItem> mPostItemList;
    public PostAdapter (List<PostItem> postItemList){
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
        holder.postProfile.setImageResource(postItem.getProfile());
        holder.postInterestNum.setText(postItem.getInterestNum());
        holder.postName.setText(postItem.getName());
        holder.postContent.setText(postItem.getContent());

    }

    @Override
    public int getItemCount(){
        return mPostItemList.size();
    }
}
