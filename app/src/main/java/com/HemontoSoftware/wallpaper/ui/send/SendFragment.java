package com.HemontoSoftware.wallpaper.ui.send;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HemontoSoftware.wallpaper.MainListAdapter;
import com.HemontoSoftware.wallpaper.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.HemontoSoftware.wallpaper.MyUtils.isOnline;

public class SendFragment extends Fragment implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    private static final int RC_STORAGE_PERMISSIONS = 123;

    private RecyclerView rc;
    private DatabaseReference mDatabase;

    private String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private TextView tv_warning;
    private AdView mAdView;
    private SpinKitView spinKitView;
    private boolean isFirstTime = false;

    private BroadcastReceiver networkBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline(context)){
                tv_warning.setVisibility(View.GONE);
                spinKitView.setVisibility(View.VISIBLE);
                isFirstTime = false;
                loadBanner();
                loadiPhoneData();
            }else {
                if(isFirstTime) {
                    spinKitView.setVisibility(View.GONE);
                    tv_warning.setVisibility(View.VISIBLE);
                }
            }

        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        isFirstTime = true;
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        rc = root.findViewById(R.id.samsung_list);
        spinKitView = root.findViewById(R.id.sSpin_kit);
        tv_warning = root.findViewById(R.id.sNo_net);

        MobileAds.initialize(getContext(),getResources().getString(R.string.app_id));


        spinKitView.setVisibility(View.VISIBLE);

        mAdView = root.findViewById(R.id.sAdView);
        loadBanner();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("oppo");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        getContext().registerReceiver(networkBroadCast, intentFilter);

        return root;
    }

    private void loadiPhoneData() {
        final ArrayList<String> urls = new ArrayList<>();
        final ArrayList<String> name = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    urls.add(dataSnapshot1.child("link").getValue().toString());
                    name.add(dataSnapshot1.child("name").getValue().toString());
                }
                Collections.reverse(urls);
                Collections.reverse(name);
                rc.setAdapter(new MainListAdapter(getContext(),urls,name));
                rc.setLayoutManager(new GridLayoutManager(getContext(), 3));
                spinKitView.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                spinKitView.setVisibility(View.GONE);
            }
        });
    }
    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(networkBroadCast);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, RC_STORAGE_PERMISSIONS, perms)
                        .build());
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            requestPermissions();
        }
        if (!isOnline(getContext())) {
            if (isFirstTime)
            {
                tv_warning.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (!EasyPermissions.hasPermissions(getContext(), this.perms)) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
            if (EasyPermissions.somePermissionDenied(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions();
            }
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        getActivity().finish();
    }

}