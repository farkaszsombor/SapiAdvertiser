package ro.sapientia.ms.sapiadvertiser.Model;


public class User {
    private static final String TAG = "User";
    private String email;
    private String phoneNum;
    private String name;

    public User( String name, String email, String phoneNum) {
        this.email = email;
        this.phoneNum = phoneNum;
        this.name = name;
    }


    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
