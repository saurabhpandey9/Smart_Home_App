package com.example.homeautomation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements SignupFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener {
    FirebaseAuth mAuth;
    FirebaseUser curUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();


        FragmentManager fragmentManager = getSupportFragmentManager();
        if(curUser==null) {
            LoginFragment loginFragment = LoginFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.llContainer, loginFragment).commit();
//            SignupFragment signupFragment = SignupFragment.newInstance();
//            fragmentManager.beginTransaction().replace(R.id.llContainer, signupFragment).commit();
        }else{
            startActivity(new Intent(MainActivity.this, SmartHome.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void switchFragment() {
        Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.llContainer);
        if(curFragment instanceof LoginFragment){
            Fragment fragment = SignupFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.llContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else if (curFragment instanceof SignupFragment){
            Fragment fragment = LoginFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.llContainer, fragment).addToBackStack(null).commit();
        }
    }
}
