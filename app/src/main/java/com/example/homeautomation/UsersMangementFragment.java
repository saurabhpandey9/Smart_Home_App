package com.example.homeautomation;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersMangementFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    DatabaseReference userRef;
    private UsersAdapter adapter;
    private ArrayList<User> usersList;

    public UsersMangementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users_mangement, container, false);
        RecyclerView rView = v.findViewById(R.id.userRView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersList = new ArrayList<User>();

        adapter = new UsersAdapter(getActivity(), usersList);
        rView.setAdapter(adapter);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot curUser : dataSnapshot.getChildren()) {
                    User user = curUser.getValue(User.class);
                    if (user != null && user.role!=null && user.role.equals(User.ROLE_MEMBER)) {
                        usersList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
//                adapter.notify();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }


    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

        private Context mctx;
        private List<User> userList;

        public UsersAdapter(Context ctx, List<User> userList) {
            this.mctx = ctx;
            this.userList = userList;
        }

        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mctx);
            View view = inflater.inflate(R.layout.user_card, parent, false);
            return new UsersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final UsersViewHolder holder, final int i) {
            final User curUser = userList.get(i);
            holder.tvName.setText(curUser.name);
            holder.verifiedSwitch.setChecked(curUser.isVerifiedByAdmin);

            userRef.child(curUser.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean newVerifiedByAdminStatus = dataSnapshot.child("verifiedByAdmin").getValue(Boolean.class);
                    holder.verifiedSwitch.setChecked(newVerifiedByAdminStatus==null? false: newVerifiedByAdminStatus );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.verifiedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(context, "Updating bulb 1", Toast.LENGTH_SHORT).show();
                    userRef.child(curUser.getId()).child("verifiedByAdmin").setValue(b);
                }
            });
        }

        @Override
        public int getItemCount() {
            return userList == null ? 1 : userList.size();
        }

        class UsersViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            Switch verifiedSwitch;
            public UsersViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.usernameTV);
                verifiedSwitch = itemView.findViewById(R.id.verificationSwitch);
            }
        }
    }
}
