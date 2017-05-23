package com.doan.thongbaodiemdung.Data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doan.thongbaodiemdung.Other.Constants.ALARMS;
import static com.doan.thongbaodiemdung.Other.Constants.AVATAR_URL;
import static com.doan.thongbaodiemdung.Other.Constants.CURRENT_POSITION;
import static com.doan.thongbaodiemdung.Other.Constants.FB_ACCOUNT;
import static com.doan.thongbaodiemdung.Other.Constants.FB_FRIENDS;
import static com.doan.thongbaodiemdung.Other.Constants.ID;
import static com.doan.thongbaodiemdung.Other.Constants.ISFOLLOWING;
import static com.doan.thongbaodiemdung.Other.Constants.LATITUDE;
import static com.doan.thongbaodiemdung.Other.Constants.LONGITUDE;
import static com.doan.thongbaodiemdung.Other.Constants.MIN_DISTANCE;
import static com.doan.thongbaodiemdung.Other.Constants.NAME;
import static com.doan.thongbaodiemdung.Other.Constants.OFFLINE;
import static com.doan.thongbaodiemdung.Other.Constants.ONLINE;
import static com.doan.thongbaodiemdung.Other.Constants.RINGTONE_NAME;
import static com.doan.thongbaodiemdung.Other.Constants.RINGTONE_PATH;
import static com.doan.thongbaodiemdung.Other.Constants.STATUS;

/**
 * Created by Hong Hanh on 4/24/2017.
 */

public class FirebaseHandle {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String userID;
    private static FirebaseHandle instance;
    private List<FriendInfo> listFriends;
    private List<Route> listRoute;
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
                        if(userID != "") {
                            mRef.child(FB_ACCOUNT).child(userID)
                                    .child(STATUS).setValue(ONLINE);

                            mRef.child(FB_ACCOUNT).child(userID)
                                    .child(STATUS).onDisconnect().setValue(OFFLINE);
                        }
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
        try {
            if(userID != "") {
                mRef.child(FB_ACCOUNT).child(userID)
                        .child("listRoute").child(String.valueOf(route.getId())).setValue(route);
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void updateCurPos(Double latitude, Double longitude) {
        try {
            if(userID != null) {
                mRef.child(FB_ACCOUNT).child(userID)
                        .child(CURRENT_POSITION).child(LATITUDE).setValue(latitude);
                mRef.child(FB_ACCOUNT).child(userID)
                        .child(CURRENT_POSITION).child(LONGITUDE).setValue(longitude);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void removeRoute(String id) {
        if(userID != "") {
            mRef.child(FB_ACCOUNT).child(userID)
                    .child(ALARMS).child(id).removeValue();
        }
    }

    public void setAccountListener() {
        if(mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS) != null)
        mRef.child(FB_ACCOUNT).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listFriends = new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.child(userID).child(FB_FRIENDS).getChildren()) {
                            if(listFriends.size() <  dataSnapshot.child(userID).child(FB_FRIENDS).getChildrenCount())
                            {
                                FriendInfo tempAccount = new FriendInfo();
                                String id = postSnapshot.child(ID).getValue(String.class);
                                tempAccount.setId(id);
                                if(postSnapshot.hasChild(ISFOLLOWING))
                                    tempAccount.setFollowing(postSnapshot.child(ISFOLLOWING).getValue(Boolean.class));
                                else
                                    tempAccount.setFollowing(false);
                                if(postSnapshot.hasChild("isNotifying"))
                                    tempAccount.setNotifying(postSnapshot.child("isNotifying").getValue(Boolean.class));
                                else
                                    tempAccount.setNotifying(false);
                                if(postSnapshot.hasChild(MIN_DISTANCE)) {
                                    tempAccount.setMinDis(postSnapshot.child(MIN_DISTANCE).getValue(Integer.class));
                                } else {
                                    tempAccount.setMinDis(100);
                                }
                                if(postSnapshot.hasChild(RINGTONE_NAME)) {
                                    tempAccount.setRingtoneName(postSnapshot.child(RINGTONE_NAME).getValue(String.class));
                                } else {
                                    tempAccount.setRingtoneName("");
                                }
                                if (postSnapshot.hasChild(RINGTONE_PATH)) {
                                    tempAccount.setRingtonePath(postSnapshot.child(RINGTONE_PATH).getValue(String.class));
                                } else {
                                    tempAccount.setRingtonePath("");
                                }
                                tempAccount.setName(dataSnapshot.child(id).child(NAME).getValue(String.class));
                                tempAccount.setAvatarURL(dataSnapshot.child(id).child(AVATAR_URL).getValue(String.class));
                                tempAccount.setStatus(dataSnapshot.child(id).child(STATUS).getValue(String.class));
                                tempAccount.setLatitude(dataSnapshot.child(id).child(CURRENT_POSITION).child(LATITUDE).getValue(Double.class));
                                tempAccount.setLongitude(dataSnapshot.child(id).child(CURRENT_POSITION).child(LONGITUDE).getValue(Double.class));
                                listFriends.add(tempAccount);
                                latitude = dataSnapshot.child(userID).child(CURRENT_POSITION)
                                        .child(LATITUDE).getValue(Double.class);
                                longitude = dataSnapshot.child(userID).child(CURRENT_POSITION)
                                        .child(LONGITUDE).getValue(Double.class);
                            }
                        }

                        listRoute = new ArrayList<Route>();
                        for (DataSnapshot data : dataSnapshot.child(userID).child("listRoute").getChildren()) {
                            if(listRoute.size() < dataSnapshot.child(userID).child("listRoute").getChildrenCount()) {
                                Route route = new Route();
                                route.setId(data.child(ID).getValue(Integer.class));
                                route.setName(data.child("name").getValue(String.class));
                                route.setDistance(data.child("distance").getValue(Double.class));
                                route.setInfo(data.child("info").getValue(String.class));
                                route.setIsEnable(data.child("isEnable").getValue(Integer.class));
                                route.setLatitude(data.child("latitude").getValue(Double.class));
                                route.setLongitude(data.child("longitude").getValue(Double.class));
                                route.setRingtone(data.child("ringtone").getValue(String.class));
                                route.setRingtonePath(data.child("ringtoneName").getValue(String.class));
                                route.setMinDistance(data.child("minDistance").getValue(Integer.class));
                                listRoute.add(route);
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

    public List<Route> getListRoute() {return listRoute;}

    public void setFollowFriend(String id, boolean isFollowing) {
        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child(ISFOLLOWING).setValue(isFollowing);
    }

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
        return location.distanceTo(friendLocation);
    }

    public void updateNotiOfFriend(String id, int minDis, String ringtoneName, String ringtonePath) {
        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child(MIN_DISTANCE).setValue(minDis);

        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child(RINGTONE_NAME).setValue(ringtoneName);
        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child(RINGTONE_PATH).setValue(ringtonePath);
    }


    public void setNotifyFriend(String id, boolean isNotifying) {
        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child("isNotifying").setValue(isNotifying);
    }
}
