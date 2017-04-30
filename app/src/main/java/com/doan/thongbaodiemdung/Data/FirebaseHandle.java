package com.doan.thongbaodiemdung.Data;

import android.util.Log;

import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Activity.SignIn;
import com.doan.thongbaodiemdung.Other.Account;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doan.thongbaodiemdung.Other.Constants.FB_ACCOUNT;

/**
 * Created by Hong Hanh on 4/24/2017.
 */

public class FirebaseHandle {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String userID;
    private static FirebaseHandle instance;
    private List<FriendInfo> listFriends;

    private FirebaseHandle() {
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseHandle getInstance() {
        if(instance == null) {
            instance = new FirebaseHandle();
        }
        return instance;
    }

    public void setUserID(String id) {
        this.userID = id;
        setAccountListener();
    }

    public void setStatusChange() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected) {
                    mRef.child(FB_ACCOUNT).child(userID)
                            .child("status").setValue("online");

                    mRef.child(FB_ACCOUNT).child(userID)
                            .child("status").onDisconnect().setValue("offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateRoute(Route route){
        mRef.child(FB_ACCOUNT).child(userID)
                .child("listRoute").child(String.valueOf(route.getId())).setValue(route);
    }

    public void updateCurPos(Double latitude, Double longitude) {
        mRef.child(FB_ACCOUNT).child(userID)
                .child("curPos").child("latitude").setValue(latitude);
        mRef.child(FB_ACCOUNT).child(userID)
                .child("curPos").child("longitude").setValue(longitude);
    }

    public void removeRoute(String id) {
        mRef.child(FB_ACCOUNT).child(userID)
                .child("listRoute").child(id).removeValue();
    }

    public void setAccountListener() {
        listFriends = new ArrayList<>();
        if(mRef.child(FB_ACCOUNT).child(userID).child("friends") != null)
        mRef.child(FB_ACCOUNT).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //nen lay list friend o day
                        //no se lay quai lay quai cho nay

                        for (DataSnapshot postSnapshot: dataSnapshot.child(userID).child("friends").getChildren()) {
                            if(listFriends.size() <  dataSnapshot.child(userID).child("friends").getChildrenCount())
                            {
                                String id = postSnapshot.getValue(String.class);
                                FriendInfo tempAccount = new FriendInfo();
                                tempAccount.setName(dataSnapshot.child(id).child("name").getValue(String.class));
                                tempAccount.setAvatarURL(dataSnapshot.child(id).child("avatarURL").getValue(String.class));
                                tempAccount.setStatus(dataSnapshot.child(id).child("status").getValue(String.class));
                                tempAccount.setLatitude(dataSnapshot.child(id).child("curPos").child("latitude").getValue(Double.class));
                                tempAccount.setLongitude(dataSnapshot.child(id).child("curPos").child("longitude").getValue(Double.class));
                                listFriends.add(tempAccount);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public List<FriendInfo> getListFriends()
    {
        return listFriends;
    }
}
