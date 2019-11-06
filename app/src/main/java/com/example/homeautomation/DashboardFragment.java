package com.example.homeautomation;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {
    DatabaseReference database;
    DatabaseReference s1Ref;
    DatabaseReference s2Ref;
    DatabaseReference s3Ref;

    RotateAnimation rotate;

    ProgressDialog mdialog;

    Context context;
    int s1Val;
    int s2Val;
    int s3Val;

    ImageButton b1;
    ImageButton b2;
    SeekBar fanSeekBar;
    ImageView fanImage;
    Switch bulb1Switch;
    Switch bulb2Switch;
    ImageView speechbtn;

    TextView inc_s1;
    TextView inc_s2;
    TextView inc_s3;

    //for music
    MediaPlayer alloff;
    MediaPlayer allon;
    MediaPlayer s1off;
    MediaPlayer s1on;
    MediaPlayer s2off;
    MediaPlayer s2on;
    MediaPlayer s3off;
    MediaPlayer sl3slow;
    MediaPlayer s3medium;
    MediaPlayer s3fast;
    MediaPlayer ligtsoff;
    MediaPlayer lightson;



    public DashboardFragment() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance().getReference("smarthome");
        s1Ref = database.child("s1");
        s2Ref = database.child("s2");
        s3Ref = database.child("s3");

        //voice decelerationn
          alloff=MediaPlayer.create(getContext(),R.raw.allappliancesturnedoff);
          allon=MediaPlayer.create(getContext(),R.raw.allappliancesturnedon);

        s1on=MediaPlayer.create(getContext(),R.raw.firstlightturnon);
        s1off=MediaPlayer.create(getContext(),R.raw.firstlightturnoff);

        s2on=MediaPlayer.create(getContext(),R.raw.secondlightturnedon);
        s2off=MediaPlayer.create(getContext(),R.raw.secondlightturnedoff);

        ligtsoff=MediaPlayer.create(getContext(),R.raw.lightsturnoff);
        lightson=MediaPlayer.create(getContext(),R.raw.lightsturnon);

        s3off=MediaPlayer.create(getContext(),R.raw.fanturnoff);
        sl3slow=MediaPlayer.create(getContext(),R.raw.fanturnontoslow);
        s3medium=MediaPlayer.create(getContext(),R.raw.fanturnontomedium);
        s3fast=MediaPlayer.create(getContext(),R.raw.fanturnontofast);




        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        b1 = v.findViewById(R.id.bulb1);
        b2 = v.findViewById(R.id.bulb2);
        bulb1Switch = v.findViewById(R.id.bulb1Switch);
        bulb2Switch = v.findViewById(R.id.bulb2Switch);
        fanSeekBar = v.findViewById(R.id.fan);
        fanImage = v.findViewById(R.id.fanImage);

        inc_s1=v.findViewById(R.id.inc_s1);
        inc_s2=v.findViewById(R.id.inc_s2);
        inc_s3=v.findViewById(R.id.inc_s3);


        speechbtn=v.findViewById(R.id.speech_rec_btn);
        mdialog=new ProgressDialog(getContext());
        mdialog.setTitle("Refreshing...");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();


        animateFan();
//        Toast.makeText(getContext(), "trial", Toast.LENGTH_SHORT).show();
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        speechbtn.setOnClickListener(this);

        bulb1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(context, "Updating bulb 1", Toast.LENGTH_SHORT).show();
                s1Ref.setValue(b ? 0 : 1);
            }
        });
        bulb2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(context, "Updating bulb 2", Toast.LENGTH_SHORT).show();
                s2Ref.setValue(b ? 0 : 1);

            }
        });
        s1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                s1Val = dataSnapshot.getValue(Integer.class);
                Log.d("s1", "onDataChange: " + s1Val);
//                Toast.makeText(context, "bulb 1 changed to " + s1Val, Toast.LENGTH_SHORT).show();
                if (s1Val == 0) {
                    b1.setImageResource(R.drawable.bulb_on);
                    bulb1Switch.setChecked(true);
                    inc_s1.setText("ON");
                    inc_s1.setTextColor(Color.RED);
                } else {
                    b1.setImageResource(R.drawable.bulb_off);
                    bulb1Switch.setChecked(false);
                    inc_s1.setText("OFF");
                    inc_s1.setTextColor(Color.BLACK);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        s2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                s2Val = dataSnapshot.getValue(Integer.class);
                Log.d("s2", "onDataChange: " + s2Val);
//                Toast.makeText(context, "bulb 2 changed to " + s2Val, Toast.LENGTH_SHORT).show();
                if (s2Val == 0) {
                    b2.setImageResource(R.drawable.bulb_on);
                    bulb2Switch.setChecked(true);
                    inc_s2.setText("ON");
                    inc_s2.setTextColor(Color.RED);
                } else {
                    b2.setImageResource(R.drawable.bulb_off);
                    bulb2Switch.setChecked(false);
                    inc_s2.setText("OFF");
                    inc_s2.setTextColor(Color.BLACK);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        s3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                s3Val = dataSnapshot.getValue(Integer.class);
                Log.d("s3", "onDataChange: " + s3Val);
//                Toast.makeText(context, "fanSeekBar speed 1 changed to " + s3Val, Toast.LENGTH_SHORT).show();
                if (s3Val <= 3) {
                    fanSeekBar.setProgress(s3Val);
                    animateFan();
                    switch (s3Val){
                        case 0:
                            inc_s3.setText("OFF");
                            inc_s3.setTextColor(Color.BLACK);
                            break;

                        case 1:
                            inc_s3.setText("SLOW");
                            inc_s3.setTextColor(Color.BLUE);
                            break;

                        case 2:
                            inc_s3.setText("MEDIUM");
                            inc_s3.setTextColor(Color.GREEN);
                            break;
                        case 3:
                            inc_s3.setText("FAST");
                            inc_s3.setTextColor(Color.BLACK);
                            inc_s3.setTextColor(Color.RED);
                            break;

                    }
                } else {
                    s3Ref.setValue(0);
                }

                mdialog.dismiss();
//

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    s3Ref.setValue(i);

                    switch (i){
                        case 0:
                            s3off.start();
                            break;

                        case 1:
                            sl3slow.start();
                            break;

                        case 2:
                            s3medium.start();
                            break;
                        case 3:
                            s3fast.start();
                            break;

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        return v;
    }


    void animateFan() {
        if (fanImage != null) {
            fanImage.setVisibility(View.INVISIBLE);
            if (s3Val == 0) {
                fanImage.clearAnimation();
                Log.d("duration", "animateFan: null");
                fanImage.setVisibility(View.VISIBLE);
                return;
            }
            rotate = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
//            int duration = 900 - s3Val * (200 + s4Val * (300 + s5Val * (400)));
            int duration = 900 - (s3Val - 1) * 300;
            rotate.setDuration(duration);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.setRepeatCount(Animation.INFINITE);
            fanImage.setAnimation(rotate);
            Log.d("duration", "animateFan: " + duration);
//            Toast.makeText(context, "fan duration: " + duration, Toast.LENGTH_SHORT).show();
            fanImage.setVisibility(View.VISIBLE);
//            fanImage.setVisibility(View.VISIBLE);
//            fanImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.speech_rec_btn:
                speech();
                break;

            case R.id.bulb1:
//                Toast.makeText(context, "Updating bulb 1", Toast.LENGTH_SHORT).show();
                s1Ref.setValue(s1Val == 1 ? 0 : 1);
                if(0==(s1Val == 1 ? 0 : 1)){
                    s1on.start();
                }
                else if(1==(s1Val == 1 ? 0 : 1)) {
                    s1off.start();
                }
                break;

            case R.id.bulb2:
//                Toast.makeText(context, "Updating bulb 2", Toast.LENGTH_SHORT).show();
                s2Ref.setValue(s2Val == 1 ? 0 : 1);
                if(0==(s2Val == 1 ? 0 : 1)){
                    s2on.start();
                }
                else if(1==(s2Val == 1 ? 0 : 1)) {
                    s2off.start();
                }
                break;
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
        }
        return true;
    }

    public void speech(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please Speak");

       try {
           startActivityForResult(intent,1);
       }catch (ActivityNotFoundException e){}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case 1:
                if(resultCode==RESULT_OK && null!=data){

                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String str=result.get(0).toLowerCase();


                    // for all lights
                    if(str.contains("lights") && str.contains("on")){


                        lights newUser =  new lights(0,0,s3Val);
                        database.setValue(newUser);
                        lightson.start();
                        return;
                    }
                    else if (str.contains("lights") && str.contains("off")){
                        lights newUser =  new lights(1,1,s3Val);
                        database.setValue(newUser);
                        ligtsoff.start();
                        return;
                    }

                    //for first & second light
                    else if (str.contains("light")){
                        if(str.contains("first")){
                            if(str.contains("on"))
                            {
                                s1Ref.setValue(0);
                                s1on.start();
                                return;
                            }
                            else if(str.contains("off"))
                            {
                                s1Ref.setValue(1);
                                s1off.start();
                                return;
                            }

                        }

                        else if(str.contains("second")){
                            if(str.contains("off"))
                            {
                                s2Ref.setValue(1);
                                s2off.start();
                                return;
                            }
                            else if(str.contains("on"))
                            {
                                s2Ref.setValue(0);
                                s2on.start();
                                return;
                            }


                        }

                    }

                    //for fan

                    else if(str.contains("fan")){
                        if(str.contains("off"))
                        {
                            s3Ref.setValue(0);
                            s3off.start();
                            return;
                        }

                        else if(str.contains("slow"))
                        {
                            s3Ref.setValue(1);
                            sl3slow.start();
                            return;
                        }
                        else if(str.contains("medium"))
                        {
                            s3Ref.setValue(2);
                            s3medium.start();
                            return;
                        }
                        else if(str.contains("fast"))
                        {
                            s3Ref.setValue(3);
                            s3fast.start();
                            return;
                        }
                        else if(str.contains("high"))
                        {
                            s3Ref.setValue(3);
                            s3fast.start();
                            return;
                        }

                        else if(str.contains("on"))
                        {
                            s3Ref.setValue(3);
                            s3fast.start();
                            return;
                        }
                    }

                    else if(str.contains("appliances") || str.contains("devices")|| str.contains("all")){
                        if(str.contains("off")){
                            lights newUser =  new lights(1,1,0);
                            database.setValue(newUser);
                            alloff.start();
                            return;
                        }

                        else if(str.contains("on")){
                            lights newUser =  new lights(0,0,3);
                            database.setValue(newUser);
                            allon.start();
                            return;
                        }
                    }


                }
                return;
        }
    }
}


