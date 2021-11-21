package me.seg.fitbites.data;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import me.seg.fitbites.firebase.AuthManager;
import me.seg.fitbites.firebase.FirestoreDatabase;
import me.seg.fitbites.firebase.OnTaskComplete;

public class FitClass {

    private String uid;
    //The uid of the FitClass type it belongs to
    private String fitClassTypeUid;
    private String teacherUID;
    private Days date;
    private int time;
    private int EndTime;
    private int capacity;
    private Difficulty difficulty;
    private LinkedList<String> memberIdList;

    public FitClass(String uid, String typeuid, String teacherUID, Days day, int time, int maxCapacity, Difficulty difficulty) {
        this.uid = uid;
        this.fitClassTypeUid = typeuid;
        this.teacherUID = teacherUID;
        this.date = day;
        this.time = time;
        this.capacity = maxCapacity;
        this.difficulty = difficulty;
        memberIdList = new LinkedList<>();
    }


    public static int convertTime(int hours, int minutes){
        return hours*60 + minutes;
    }


    public FitClass() {}

    public String getUid() { return uid; }
    public String getFitClassTypeUid(){ return fitClassTypeUid; };
    public String getTeacherUID(){ return teacherUID; }
    public void setUID(String uid){ this.uid = uid; }
    public void setFitClassTypeUid(String fitClassTypeUid){ this.fitClassTypeUid = fitClassTypeUid; }
    public void setTeacherUID(String teacherUID){ this.teacherUID = teacherUID; }
    public int getTime(){ return time; }
    public void setTime(int time){ this.time = time; }
    public int getEndTime(){ return this.EndTime; }
    public void setEndTime(int time){ this.EndTime = time; }
    public void setCapacity(int cap){ this.capacity = cap; }
    public int getCapacity(){ return this.capacity; }
    public void setDifficulty(Difficulty difficulty){ this.difficulty = difficulty; }
    public Difficulty getDifficulty(){ return this.difficulty; }
    @Exclude
    public Days getDateObj() { return date; }
    @Exclude
    public void setDateObj(Days date) { this.date = date; }
    public String getDate() {return date.toString(); }
    public void setDate(String d) { this.date = Days.valueOf(d); }
    public void setMemberIdList(List<String> ml) { memberIdList = new LinkedList<String>(ml); }
    public List<String> getMemberIdList() { return memberIdList; }
    @Exclude
    public int getMemberListSize() { return memberIdList.size(); }
    public void enrollMember(UserData ud) { memberIdList.add(ud.getUid()); }
    public void unenrollMember(UserData ud) { memberIdList.remove(ud.getUid()); }


    public void checkCollision(OnTaskComplete<Boolean> a){

        FirestoreDatabase.getInstance().getAvailableClasses(new OnTaskComplete<FitClass[]>() {
            @Override
            public void onComplete(FitClass[] result) {
                for (FitClass i: result){
                    if(i.getDateObj() == FitClass.this.getDateObj() && i.getFitClassTypeUid().equals(FitClass.this.getFitClassTypeUid())){

                        a.onComplete(true);
                        return;
                    }

                }

                a.onComplete(false);

            }
        });

    }


    public static FitClass createClass(FitClassType type){
        // creates a new Class
        //generates a uuid. likelihood of uuid collisions is essentially 0 therefore no duplicate checks
        //required.

        UUID uuid = UUID.randomUUID();

        FitClass fc = new FitClass(uuid.toString(), type.getUid(), null,  Days.SUNDAY, 690, 420, null);

        return fc;
    }

    public void updateClass(){
        // edits a class
        // access to instructor
        if(AuthManager.getInstance().getCurrentUserData() instanceof Instructor) {
            FirestoreDatabase.getInstance().setFitClass(this);
        }
    }

    public static void searchClass(String className, OnTaskComplete<FitClass[]> onTaskComplete){
        // returns the searched class

        //get all classes in database
        FirestoreDatabase.getInstance().getAvailableClasses(new OnTaskComplete<FitClass[]>() {
            @Override
            public void onComplete(FitClass[] classResults) {
                if(classResults != null) {
                    //get all class types
                    FirestoreDatabase.getInstance().viewAllClassTypes(new OnTaskComplete<FitClassType[]>() {
                        @Override
                        public void onComplete(FitClassType[] typeResults) {
                            if(typeResults != null) {
                                ArrayList<FitClass> resultList = new ArrayList<>();

                                classList:
                                for(FitClass c : classResults) {
                                    String tuid = c.getFitClassTypeUid();

                                    //find
                                    for(FitClassType t : typeResults) {
                                        if(t.getUid().equals(tuid)) {
                                            if(t.getClassName().contains(className)) {
                                                //add to list
                                                resultList.add(c);
                                                continue classList;
                                            }
                                        }
                                    }
                                    // uid does not exist ignore element
                                }

                                //list is full of potential search elements
                                onTaskComplete.onComplete(resultList.toArray(new FitClass[resultList.size()]));
                            } else {
                                Log.w("Process", "Something went wrong. you shouldnt be seeing this message. Code 601");
                                onTaskComplete.onComplete(null);
                            }
                        }
                    });

                } else {
                    onTaskComplete.onComplete(null);
                }
            }
        });

    }

    public void cancelClass(){
        //cancels a class
        // access to instructor
        if(AuthManager.getInstance().getCurrentUserData() instanceof Instructor) {
            FirestoreDatabase.getInstance().deleteFitClass(this);
        }
    }

    public static void searchClassByTeacher(String lastName, OnTaskComplete<FitClass []> onTaskComplete){

        Instructor.searchInstructor(lastName, new OnTaskComplete<Instructor[]>() {
            @Override
            public void onComplete(Instructor[] instructors) {

                if(instructors == null || instructors.length == 0){
                    onTaskComplete.onComplete(null);
                    return;
                }

                FirestoreDatabase.getInstance().getAvailableClasses(new OnTaskComplete<FitClass[]>() {
                    @Override
                    public void onComplete(FitClass[] result) {
                        ArrayList<FitClass> list = new ArrayList<>();

                        outside:
                        for (FitClass i: result){
                            for (Instructor j: instructors){
                                if (i.getTeacherUID().equals(j.getUid())){
                                    list.add(i);
                                    continue outside;
                                }
                            }
                        }
                        onTaskComplete.onComplete(list.toArray(new FitClass[list.size()]));


                    }
                });
            }
        });


    }

}