package code.art.drowningalert.Item;

import java.io.File;

public class SignUpInfo{
    private File profile;
    private String account;
    private String nickname;
    private String password;
    private String region;
    private String scrQuestion;
    private String scrAnswer;

    public SignUpInfo(File profile, String account, String password,String nickname,String region,String scrQuestion,String scrAnswer){
        this.profile = profile;
        this.account = account;
        this.nickname = nickname;
        this.password = password;
        this.region = region;
        this.scrQuestion = scrQuestion;
        this.scrAnswer = scrAnswer;
    }
    public SignUpInfo(){

    }

    public File getProfile() {
        return profile;
    }

    public void setProfile(File profile) {
        this.profile = profile;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getScrQuestion() {
        return scrQuestion;
    }

    public void setScrQuestion(String scrQuestion) {
        this.scrQuestion = scrQuestion;
    }

    public String getScrAnswer() {
        return scrAnswer;
    }

    public void setScrAnswer(String scrAnswer) {
        this.scrAnswer = scrAnswer;
    }
}
