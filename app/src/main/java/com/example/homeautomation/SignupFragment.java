package com.example.homeautomation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    DatabaseReference userDatabase;
    private EditText etEmail;

    private EditText etName;

    private EditText etPhone;

    private EditText etPassword;
    private EditText etRPassword;
    private Button bSignup;

    private TextView tvGoToLogin;

    MediaPlayer accountsuccreated;

    ProgressDialog mdialog;

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountsuccreated=MediaPlayer.create(getContext(),R.raw.accountcreated);
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        etEmail = v.findViewById(R.id.etEmailSignup);
        etName = v.findViewById(R.id.etNamelSignup);

        etPassword = v.findViewById(R.id.etPasswordSignup);

        etRPassword = v.findViewById(R.id.etRetypePasswordSignup);

        etPhone = v.findViewById(R.id.etPhoneSignup);

        bSignup = v.findViewById(R.id.bSignup);

        tvGoToLogin = v.findViewById(R.id.tvGoToLogin);

        bSignup.setOnClickListener(this);

        tvGoToLogin.setOnClickListener(this);

        return v;
    }

    void signup(){

        mdialog=new ProgressDialog(getContext());
        mdialog.setTitle("Please wait");
        mdialog.setMessage("your account is under process...");
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String rPassword = etRPassword.getText().toString();
        final String phoneText = etPhone.getText().toString();
        final String name = etName.getText().toString();
        if(name==null || name.length()==0){
            etName.setError("Enter a name");
            etName.requestFocus();
        }
        if(email==null || email.length()==0){
            etEmail.setError("Enter an email");
            etEmail.requestFocus();
        }
        else if(phoneText==null || phoneText.length()!=10){
            etPhone.setError("Plese enter 10-dig phone number");
            etPhone.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
        }
        else if(password==""){
            etPassword.setError("Please enter a password");
            etPassword.requestFocus();
        }
        else if (password.length()<6){
            etPassword.setError("Password length should be greater than 6");
            etPassword.requestFocus();
        }
        else if(rPassword ==""){
            etRPassword.setError("Please retype the password");
            etRPassword.requestFocus();
        }
        else if(!password.equals(rPassword)){
            etRPassword.setError("Passwords don't match");
            etRPassword.requestFocus();
        }else{
            mdialog.show();
            final long phone = Long.parseLong(phoneText);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mdialog.dismiss();
                        Toast.makeText(getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
//                        String newUserId = mAuth.getCurrentUser().getUid();
//                        User newUser =  new User(newUserId, name, phone, email, User.ROLE_MEMBER);
//                        userDatabase.child(newUserId).setValue(newUser);
                        startActivity(new Intent(getContext(),MainActivity.class));
                        Toast.makeText(getContext(), "Please verify your account before login..", Toast.LENGTH_SHORT).show();
                        accountsuccreated.start();
                        mAuth.signOut();
                    }
                    else{
                        mdialog.dismiss();
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bSignup:
                signup();
                break;
            case R.id.tvGoToLogin:
                mListener.switchFragment();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void switchFragment();
    }
}
