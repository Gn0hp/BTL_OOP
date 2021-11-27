package com.learning.mymusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    String[] items;     //songs


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listViewSong);

        runtimePermission();
    }


    //storage permission ->manifest file
    public void runtimePermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySong();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findSong(File file){
        ArrayList<File> songsList = new ArrayList<>();



        File[] files = file.listFiles();
        try {
            //check all file and folder
            for (int i= 0;i<files.length;++i) {
                File singleFile=files[i];
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    songsList.addAll(findSong(singleFile));         //check in single file

                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                        songsList.add(singleFile);
                    }
                }
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        return songsList;

    }

    //display song in listView
    public void displaySong(){
        final ArrayList<File> mySong = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySong.size()];
        //add single to items
        for(int i=0;i<mySong.size();++i){
            items[i]=mySong.get(i).getName().toString().replace(".mp3","").replace(".wav","");

        }

        //add items to listview
//        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items); //convert data
//        listView.setAdapter(myAdapter);

        customAdapter customAdapter=new customAdapter();        //sync all adapter
        listView.setAdapter(customAdapter);


    }


    public class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView=getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong=myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);

            return myView;
        }
    }
}