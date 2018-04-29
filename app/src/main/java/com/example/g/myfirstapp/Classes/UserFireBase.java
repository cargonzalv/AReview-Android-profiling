package com.example.g.myfirstapp.Classes;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by G on 02-Apr-18.
 */

public class UserFireBase implements Serializable
{
    private String username;
    private String email;
    private String uid;
    private String photoURL;
    private long friendCount;
    private long reviewCount;

    public UserFireBase(DocumentSnapshot doc) {
        username= (String)doc.get("username");
        email= (String) doc.get("email");
        uid= (String) doc.get("uid");
        photoURL= (String) doc.get("photoURL");
        if(doc.get("friendCount")!=null && doc.get("reviewCount")!=null)
        {
            friendCount= (long) doc.get("friendCount");
            reviewCount= (long) doc.get("reviewCount");
        }

    }

    public UserFireBase(FirebaseUser user) {
        username= user.getDisplayName();
        email= user.getEmail();
        uid= user.getUid();
        Uri pPhotoUrl = user.getPhotoUrl();
        if(pPhotoUrl!=null)
        {
            this.photoURL=photoURL.toString();
        }
        else
        {
            photoURL=null;
        }
    }

    public long getFriendCount()
    {
        return friendCount;
    }

    public long getReviewCount()
    {
        return reviewCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return email;
    }

    public UserFireBase(String username, String email, String uid, Uri pPhotoUrl,long friendCount,long reviewCount)
    {
        this.username = username;
        this.email = email;
        this.uid=uid;
        if(pPhotoUrl!=null)
        {
            this.photoURL=photoURL.toString();
        }
        else
        {
            photoURL=null;
        }
        friendCount= this.friendCount;
        reviewCount= this.reviewCount;


    }

    public UserFireBase(HashMap<String, Object> actual) {
        username = (String) actual.get("username");
        uid = (String) actual.get("uid");
        email = (String) actual.get("email");
        if(actual.get("friendCount")!=null && actual.get("reviewCount")!=null)
        {
            friendCount= (int) actual.get("friendCount");
            reviewCount= (int) actual.get("reviewCount");
        }
    }



    public String getId() {
        return uid;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("email", email);
        requestData.put("uid", uid);
        requestData.put("username", username);
        requestData.put("photoURL", photoURL);
        requestData.put("friendCount", friendCount);
        requestData.put("reviewCount", reviewCount);
        return requestData;
    }

    public static ArrayList<UserFireBase> getFriends(HashMap<String,Object> friends) {
        ArrayList<UserFireBase> r = new ArrayList<>();
        if(friends!=null)
        {
            Set<String> keysSet =friends.keySet();
            String[] keys= new String[keysSet.size()];
            keys = keysSet.toArray(keys);

            for (int j =0; j<keys.length;j++)
            {
                HashMap<String, Object> actual = (HashMap<String, Object>) friends.get(keys[j]);
                if(actual!=null)
                {

                    UserFireBase user = new UserFireBase(actual);
                    r.add(user);
                }

            }
        }
        return r;
    }

    public static ArrayList<UserFireBase> getUsers(QuerySnapshot queryDocumentSnapshots) {
        ArrayList<DocumentSnapshot> f = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
        ArrayList<UserFireBase> requests = new ArrayList<>();
        for (int i=0;i<f.size();i++)
        {
            DocumentSnapshot act = f.get(i);
            requests.add(new UserFireBase(act));
        }
        return requests;
    }
}
