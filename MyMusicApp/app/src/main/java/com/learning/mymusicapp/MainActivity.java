package com.learning.mymusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    String[] items;     //songs name


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        listView=findViewById(R.id.listViewSong);       //tìm view id trong file xml

        runtimePermission();
    }


    //storage permission ->manifest file
    public void runtimePermission(){            //quyền truy cập và ghi âm
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();      // nếu cho phép thì displaySong(hàm để hiển thị list bài hát)
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file){         //tìm tất cả bài hát trong cả bộ nhớ
        ArrayList<File> songsList = new ArrayList<>();



        File[] files = file.listFiles();        //list ra tất cả các file trong (File) file
        try {               //nhớ là phải trong try catch
            //check all file and folder
            for (int i= 0;i<files.length;++i) {
                File singleFile=files[i];
                if (singleFile.isDirectory() && !singleFile.isHidden()) {       //nếu là folder và không bị ẩn thì gọi đệ quy tìm trong folder đó
                    songsList.addAll(findSong(singleFile));         //check in single file

                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                        songsList.add(singleFile);      //end = .mp3 hoặc wav thì thêm vào songList
                    }
                }
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        Collections.sort(songsList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return songsList;

    }

    //display song in listView
    public void displaySong(){
        final ArrayList<File> mySong = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySong.size()];
        //add single to items
        for(int i=0;i<mySong.size();++i){
            items[i]=mySong.get(i).getName().toString().replace(".mp3","").replace(".wav","");      //thêm name(toString) vào String[] để hiển thị

        }

        //add items to listview
//        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);   //convert data
//        listView.setAdapter(myAdapter);

        customAdapter customAdapter=new customAdapter();        //sync all adapter              //
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName=(String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("songs",mySong).putExtra("songname",songName).putExtra("pos",position));
            }
        });

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
            View myView=getLayoutInflater().inflate(R.layout.list_item,null);       //lấy view listItem từ trong xml files
            TextView textsong=myView.findViewById(R.id.txtsongname);            //lây view của txtsongname
            textsong.setSelected(true);
            textsong.setText(items[i]);

            return myView;
        }
    }
}