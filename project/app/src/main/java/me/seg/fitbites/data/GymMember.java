package me.seg.fitbites.data;

public class GymMember extends UserData {

    public static final String GYM_MEMBER_LABEL = "GYM_MEMBER";

    public GymMember(String uid, String firstName, String lastName, String userName, String address, String age, String password, String email){
        super(uid, firstName, lastName, userName, address, age, password, email);
        this.label = GYM_MEMBER_LABEL;
    }

    public GymMember() {}

}
