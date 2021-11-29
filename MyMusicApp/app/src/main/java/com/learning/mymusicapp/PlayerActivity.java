package com.learning.mymusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay, btnnext,btnprev,btnff,btnfr;
    TextView txtname,txtstart,txtstop;
    SeekBar seekBar;
    BarVisualizer barVisualizer;

    String sname;
    public static final String EXTRA_NAME="song_name";
    public static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnprev=findViewById(R.id.btnprev);
        btnnext=findViewById(R.id.btnnext);
        btnplay=findViewById(R.id.playbtn);
        btnff=findViewById(R.id.btnff);
        btnfr=findViewById(R.id.btnfr);
        txtname=findViewById(R.id.txtsn);
        txtstart=findViewById(R.id.txtstart);
        txtstop=findViewById(R.id.txtstop);
        seekBar=findViewById(R.id.seekbar);
        barVisualizer=findViewById(R.id.barVisualizer);

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i=getIntent();
        Bundle bundle=i.getExtras();

        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");    //chieu sangg Intent trong displyasong main
        String songName=i.getStringExtra("songname");
        position=bundle.getInt("pos");
        txtname.setSelected(true);
        Uri uri= Uri.parse(mySongs.get(position).toString());
        sname=mySongs.get(position).getName();
        txtname.setText(sname);

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

    }
}