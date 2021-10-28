package com.example.music_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button playbtn, prevbtn, nextbtn;
    TextView txtname, txtstart, txtstop;
    SeekBar seekmusic;
    BarVisualizer visualizer;

    String sname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer != null){
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        playbtn = findViewById(R.id.playbtn);
        prevbtn = findViewById(R.id.previousbtn);
        nextbtn = findViewById(R.id.nextbtn);
        txtname = findViewById(R.id.txtsn);
        txtstart = findViewById(R.id.txtstart);
        txtstop = findViewById(R.id.txtstop);
        seekmusic = findViewById(R.id.seekbar);
        visualizer = findViewById(R.id.bar);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();


        updateseekbar = new Thread(){
            @Override
            public void run() {
                int duracaoTotal = mediaPlayer.getDuration();
                int posicaoAtual = 0;
                while (posicaoAtual < duracaoTotal){
                    try{
                        sleep(500);
                        posicaoAtual = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(posicaoAtual);
                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.SRC_IN);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String tempoDuracao = createTime(mediaPlayer.getDuration());
        txtstop.setText(tempoDuracao);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String duracaoAtual = createTime(mediaPlayer.getCurrentPosition());
                txtstart.setText(duracaoAtual);
                handler.postDelayed(this, delay);
            }
        }, delay);
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    playbtn.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();

                }else{
                    playbtn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextbtn.performClick();
            }
        });
        int audioSessaoId = mediaPlayer.getAudioSessionId();
        if(audioSessaoId != -1){
            visualizer.setAudioSessionId(audioSessaoId);
        }
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                int audioSessaoId = mediaPlayer.getAudioSessionId();
                if(audioSessaoId != -1){
                    visualizer.setAudioSessionId(audioSessaoId);
                }
            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size() - 1):(position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                int audioSessaoId = mediaPlayer.getAudioSessionId();
                if(audioSessaoId != -1){
                    visualizer.setAudioSessionId(audioSessaoId);
                }
            }
        });
    }
    public static String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time += min + ":";
        if(sec<10){
            time += "0";
        }
        time += sec;
        return time;
    }
}