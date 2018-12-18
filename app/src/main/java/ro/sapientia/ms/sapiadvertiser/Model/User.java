package ro.sapientia.ms.sapiadvertiser.Model;



public class User {
    private static final String TAG = "User";
    private String email;
    private String phoneNum;
    private String name;
    private String  profilePic ;

    public User()
    {

    }
    public User( String name, String email, String phoneNum) {
        this.email = email;
        this.phoneNum = phoneNum;
        this.name = name;
    }

    public String getProfilePicture() {
        if(profilePic!=null){
            return profilePic;
        }
        return "https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-5bc78.appspot.com/o/ProfilePictures%2Fsamu1.png?alt=media&token=1aa184d6-9f91-48ff-8732-864105516c90";
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePic = profilePicture;
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

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
