package code.art.drowningalert.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import code.art.drowningalert.Activities.ChangePwdActivity;
import code.art.drowningalert.Activities.ChangeScrActivity;
import code.art.drowningalert.Activities.PrivacyActivity;
import code.art.drowningalert.Activities.UsageDetailActivity;
import code.art.drowningalert.R;

public class MineFragment extends Fragment {

    private TextView changePwd;
    private TextView changeScr;
    private TextView aboutPrivacy;
    private TextView aboutUsage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_mine,container,false);

        changePwd= view.findViewById(R.id.mine_change_pwd);
        changeScr = view.findViewById(R.id.mine_change_scr);
        aboutPrivacy = view.findViewById(R.id.mine_privacy);
        aboutUsage = view.findViewById(R.id.mind_about_usage);

        initEvents();


        return view;
    }
    private void initEvents(){
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
    }


}
