package code.art.drowningalert.Item;


public class PostItem {
    private String name;
    private String postTime;
    private String profile;
    private String content;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
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
