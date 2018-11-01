package com.firstapp.quyen.freakingmath;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class Screen_Options extends AppCompatActivity implements View.OnClickListener {
    Button btnBg;
    Button btnRed;
    Button btnWhite;
    Button btnBlue;
    Button btnMusic;
    Button btnDefault;
    TextView txtViewBg;
    TextView txtViewMusic;
    TextView txtViewSound;
    TextView txtViewDefaultSetting;
    ListView listViewMusic;
    RelativeLayout ScreenOptionLayout;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private int colorOptions;
    private int musicOptions;
    private ArrayList<String> arrayMusicSong;
    private ArrayList<Integer> arraySongReferences;
    private  static MediaPlayer mediaPlayer;
    private static final int NO_SONG_PLAYBACK = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.screen_options_layout);
        ScreenOptionLayout = findViewById(R.id.screen_options_layout);
        btnBg = findViewById(R.id.button_Background);
        txtViewBg = findViewById(R.id.txtView_Background);
        txtViewMusic = findViewById(R.id.txtView_Music);
        txtViewSound = findViewById(R.id.txtView_Sound);
        txtViewDefaultSetting = findViewById(R.id.txtView_DefaultSetting);
        btnMusic = findViewById(R.id.button_Music);
        btnDefault = findViewById(R.id.button_DefaultSetting);
        arraySongReferences = new ArrayList<>();

        arraySongReferences.add(-1);
        arraySongReferences.add(R.raw.black_mirrors);
        arraySongReferences.add(R.raw.snow_on_the_roses);
        arraySongReferences.add(R.raw.tears_in_your_eyes);

        btnBg.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        btnDefault.setOnClickListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedOptions", MODE_PRIVATE);
        colorOptions = sharedPreferences.getInt("color_options", R.color.colorWhite);
        musicOptions = sharedPreferences.getInt("music_options", -1);
        ScreenOptionLayout.setBackgroundColor(ContextCompat.getColor(Screen_Options.this,sharedPreferences.getInt("color_options", R.color.colorWhite)));
        checkColorBackground();
        initControls();
        arrayMusicSong = new ArrayList<>();
        addSongToList();
    }

    public void addSongToList(){
        arrayMusicSong.add("None");
        arrayMusicSong.add("Black Mirror (Vladimir Sterzer)");
        arrayMusicSong.add("Snow on the roses (Vladimir Sterzer)");
        arrayMusicSong.add("Tears in your eyes (Vladimir Sterzer)");
    }

    public void checkColorBackground(){
        if(colorOptions == R.color.colorRed){
            setTextColorForView(R.color.colorWhite);
        }
        else if(colorOptions == R.color.colorWhite){
            setTextColorForView(R.color.colorBlack);
        }
        else if(colorOptions == R.color.colorPrimaryDark){
            setTextColorForView(R.color.colorWhite);
        }
    }
    public void setTextColorForView(int color){
        txtViewBg.setTextColor(ContextCompat.getColor(Screen_Options.this,color));
        txtViewMusic.setTextColor(ContextCompat.getColor(Screen_Options.this,color));
        txtViewSound.setTextColor(ContextCompat.getColor(Screen_Options.this,color));
        txtViewDefaultSetting.setTextColor(ContextCompat.getColor(Screen_Options.this,color));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("color_options",colorOptions);
        bundle.putInt("music_options", musicOptions);
        intent.putExtra("dataFromOptionsScreen", bundle);
        setResult(RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_Background:
                displayBackGroundDialog();
                break;
            case R.id.button_Music:
                displayMusicDialog();
                break;
            case R.id.button_DefaultSetting:
                displayResetDialog();
                break;
        }
    }

    public void displayResetDialog(){
        final AlertDialog.Builder resetDialog = new AlertDialog.Builder(Screen_Options.this);
        resetDialog.setTitle("Reset the settings");
        resetDialog.setMessage("Do you really want to reset all settings ?");
        resetDialog.setIcon(R.mipmap.ic_launcher);

        resetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ScreenOptionLayout.setBackgroundColor(ContextCompat.getColor(Screen_Options.this,R.color.colorWhite));
                setTextColorForView(R.color.colorBlack);
                colorOptions = R.color.colorWhite;
                playMusic(NO_SONG_PLAYBACK);
                MainActivity.stopMusic();
            }
        });

        resetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        resetDialog.show();

    }
    public void displayMusicDialog(){
        Dialog musicDialog = new Dialog(Screen_Options.this);
        musicDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        musicDialog.setContentView(R.layout.dialog_listview_music_layout);
        setMusicListView(musicDialog);
        musicDialog.show();
    }

    public void setMusicListView(Dialog dialog){
        listViewMusic = dialog.findViewById(R.id.listView_Music);
        ArrayAdapter arraySongAdapter = new ArrayAdapter(Screen_Options.this, android.R.layout.simple_list_item_1, arrayMusicSong);
        listViewMusic.setAdapter(arraySongAdapter);

        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusic(position);
                MainActivity.stopMusic();
            }
        });
    }

    public void playMusic(int position){
        if(arrayMusicSong.get(position).equals("None") ){
            if(mediaPlayer!=null){
                mediaPlayer.stop();
            }
            musicOptions = -1;
            Toast.makeText(Screen_Options.this,"musicOptions = " + musicOptions,Toast.LENGTH_SHORT).show();

        }
        else if(!arrayMusicSong.get(position).equals("None") && mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(Screen_Options.this,arraySongReferences.get(position));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.start();
            musicOptions = arraySongReferences.get(position);
        }
        else if(!arrayMusicSong.get(position).equals("None") && mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(Screen_Options.this,arraySongReferences.get(position));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.start();
            musicOptions = arraySongReferences.get(position);
        }
    }

    public void displayBackGroundDialog(){
        Dialog backgroundDialog = new Dialog(Screen_Options.this);
        backgroundDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        backgroundDialog.setContentView(R.layout.background_option_dialog_layout);
        btnRed = backgroundDialog.findViewById(R.id.button_Red);
        btnWhite = backgroundDialog.findViewById(R.id.button_White);
        btnBlue = backgroundDialog.findViewById(R.id.button_Blue);

        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenOptionLayout.setBackgroundColor(ContextCompat.getColor(Screen_Options.this,R.color.colorRed));
                setTextColorForView(R.color.colorWhite);
                colorOptions = R.color.colorRed;
            }
        });

        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenOptionLayout.setBackgroundColor(ContextCompat.getColor(Screen_Options.this,R.color.colorWhite));
                setTextColorForView(R.color.colorBlack);
                colorOptions = R.color.colorWhite;
            }
        });

        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenOptionLayout.setBackgroundColor(ContextCompat.getColor(Screen_Options.this,R.color.colorPrimaryDark));
                setTextColorForView(R.color.colorWhite);
                colorOptions = R.color.colorPrimaryDark;
            }
        });

        backgroundDialog.show();
    }

    private void initControls() {
        try {
            volumeSeekbar = findViewById(R.id.seekbar_Sound);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
