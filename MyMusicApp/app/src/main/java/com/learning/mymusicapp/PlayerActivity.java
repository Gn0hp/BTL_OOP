package com.learning.mymusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView imageView;

    String sname;
    public static final String EXTRA_NAME="song_name";
    public static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(barVisualizer!=null){
            barVisualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        imageView =findViewById(R.id.imageview);

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

        updateSeekbar=new Thread(){
            @Override
            public void run(){
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;
                while(currentposition<totalDuration){
                    try{
                        sleep(500);
                        currentposition=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);


                    } catch (InterruptedException | IllegalStateException e)  {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        String endTime=createTime(mediaPlayer.getDuration());
        txtstop.setText(endTime);

        final Handler handler=new Handler();
        final int delay=1000;

        //xu ly tre time khi di chuyen (tua)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currenTime=createTime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currenTime);
                handler.postDelayed(this,delay);

            }
        },delay);



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
        //next listener when End
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });

        int audiosessionId=mediaPlayer.getAudioSessionId();  //reuse
        if(audiosessionId!=-1){
            barVisualizer.setAudioSessionId(audiosessionId);
        }



        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)>=mySongs.size()?(0):position+1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);

                int audiosessionId=mediaPlayer.getAudioSessionId();
                if(audiosessionId!=-1){
                    barVisualizer.setAudioSessionId(audiosessionId);
                }

            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0?(mySongs.size()-1):position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);

                int audiosessionId=mediaPlayer.getAudioSessionId();
                if(audiosessionId!=-1){
                    barVisualizer.setAudioSessionId(audiosessionId);
                }
            }
        });

        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);


            }
        });

        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);

            }
        });
    }


    public void startAnimation(View view){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

    public String createTime(int duration){
        String time="";
        int minute=duration/1000/60;
        int sec=duration/1000%60;
        time+=minute+":";

        if(sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}