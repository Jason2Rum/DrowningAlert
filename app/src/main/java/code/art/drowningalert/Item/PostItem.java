package code.art.drowningalert.Item;

import android.graphics.drawable.Drawable;

public class PostItem {
    private String name;
    private String postTime;
    private int profile;
    private String content;

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    private String interestNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInterestNum() {
        return interestNum;
    }

    public void setInterestNum(String interestNum) {
        this.interestNum = interestNum;
    }
}
