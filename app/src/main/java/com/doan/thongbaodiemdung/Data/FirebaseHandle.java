package com.doan.thongbaodiemdung.Data;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Activity.SignIn;
import com.doan.thongbaodiemdung.Other.Account;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doan.thongbaodiemdung.Other.Constants.FB_ACCOUNT;
import static com.doan.thongbaodiemdung.Other.Constants.FB_FRIENDS;

/**
 * Created by Hong Hanh on 4/24/2017.
 */

public class FirebaseHandle {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String userID;
    private static FirebaseHandle instance;
    private List<FriendInfo> listFriends;
    private Double latitude;
    private Double longitude;

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
                    try {
                        mRef.child(FB_ACCOUNT).child(userID)
                                .child("status").setValue("online");

                        mRef.child(FB_ACCOUNT).child(userID)
                                .child("status").onDisconnect().setValue("offline");
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

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
        try {
            mRef.child(FB_ACCOUNT).child(userID)
                    .child("curPos").child("latitude").setValue(latitude);
            mRef.child(FB_ACCOUNT).child(userID)
                    .child("curPos").child("longitude").setValue(longitude);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void removeRoute(String id) {
        mRef.child(FB_ACCOUNT).child(userID)
                .child("listRoute").child(id).removeValue();
    }

    public void setAccountListener() {
        if(mRef.child(FB_ACCOUNT).child(userID).child("friends") != null)
        mRef.child(FB_ACCOUNT).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listFriends = new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.child(userID).child("friends").getChildren()) {
                            if(listFriends.size() <  dataSnapshot.child(userID).child("friends").getChildrenCount())
                            {
                                FriendInfo tempAccount = new FriendInfo();
                                String id = postSnapshot.child("id").getValue(String.class);
                                tempAccount.setId(id);
                                if(postSnapshot.hasChild("isFollowing"))
                                    tempAccount.setFollowing(postSnapshot.child("isFollowing").getValue(Boolean.class));
                                else
                                    tempAccount.setFollowing(false);
                                if(postSnapshot.hasChild("minDis")) {
                                    tempAccount.setMinDis(postSnapshot.child("minDis").getValue(Integer.class));
                                } else {
                                    tempAccount.setMinDis(100);
                                }
                                if(postSnapshot.hasChild("isNotifying")) {
                                    tempAccount.setNotifying(postSnapshot.child("isNotifying").getValue(Boolean.class));
                                } else {
                                    tempAccount.setNotifying(false);
                                }
                                tempAccount.setName(dataSnapshot.child(id).child("name").getValue(String.class));
                                tempAccount.setAvatarURL(dataSnapshot.child(id).child("avatarURL").getValue(String.class));
                                tempAccount.setStatus(dataSnapshot.child(id).child("status").getValue(String.class));
                                tempAccount.setLatitude(dataSnapshot.child(id).child("curPos").child("latitude").getValue(Double.class));
                                tempAccount.setLongitude(dataSnapshot.child(id).child("curPos").child("longitude").getValue(Double.class));
                                listFriends.add(tempAccount);
                                latitude = dataSnapshot.child(userID).child("curPos")
                                        .child("latitude").getValue(Double.class);
                                longitude = dataSnapshot.child(userID).child("curPos")
                                        .child("longitude").getValue(Double.class);
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

    public void setFollowFriend(String id, boolean isFollowing) {
        mRef.child(FB_ACCOUNT).child(userID).child("friends").child(id)
                .child("isFollowing").setValue(isFollowing);
    }

<<<<<<< HEAD
    public double getDistanceFromFriend(String friendId)
    {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Location friendLocation = new Location(LocationManager.GPS_PROVIDER);

        for(FriendInfo friend: listFriends)
        {
            if(friend.getId() == friendId) {
                friendLocation.setLongitude(friend.getLongitude());
                friendLocation.setLatitude(friend.getLatitude());
                break;
            }

        }
        Log.e("friend location", friendLocation.toString());

        return location.distanceTo(friendLocation);
    }

    public Map<String, Float> DistanceFromFriends() {
        Map<String, Float> listDistance = new HashMap<String, Float>();

        Location friendLocation = new Location("");
        for(FriendInfo friend : listFriends)
        {
            friendLocation.setLongitude(friend.getLongitude());
            friendLocation.setLatitude(friend.getLatitude());
            if(friend.getStatus() == "online")
                listDistance.put(friend.getId(), getSeftLocation().distanceTo(friendLocation));
            else
                listDistance.put(friend.getId(), null);
        }
        return listDistance;
    }

    private Location getSeftLocation()
    {
        final Location location = new Location(LocationManager.GPS_PROVIDER);

        mRef.child(FB_ACCOUNT).child(userID).child("curPos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location.setLatitude(dataSnapshot.child("latitude").getValue(Double.class));
                location.setLongitude(dataSnapshot.child("longitude").getValue(Double.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return location;
    }

=======
    public void updateNotiOfFriend(String id, int minDis) {
        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child("minDis").setValue(minDis);
    }

    public void setNotifyFriend(String id, boolean isNotifying) {
        mRef.child(FB_ACCOUNT).child(userID).child("friends").child(id)
                .child("isNotifying").setValue(isNotifying);
    }
>>>>>>> origin/master
}
