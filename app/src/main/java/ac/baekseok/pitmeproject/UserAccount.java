package ac.baekseok.pitmeproject;

import android.net.Uri;

/**
 * 사용자 계정 정보 모델 클래스
 */

public class UserAccount {
    private String idToken; //Firebase Uid(고유 토큰정보)
    private String emailId;
    private String password;
    private String nickname;

    public UserAccount() {
    }//파이어베이스 database 가져올 때 생성자 없으면 error


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
