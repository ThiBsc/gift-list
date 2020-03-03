package com.thibsc.giftlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * The lists that you following
 */
public class FollowedListFragment extends ListFragment implements FirebaseAuth.AuthStateListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG_FOLLOWED_LIST_FRAGMENT = "FOLLOWED_LIST_FRAGMENT";

    private ListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;

    public static FollowedListFragment newInstance(){
        return new FollowedListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_FOLLOWED_LIST_FRAGMENT, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_followedlist, container, false);
        listAdapter = new ListAdapter(getActivity());

        swipeRefreshLayout = view.findViewById(R.id.followedlist_refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        setListAdapter(listAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    private void loadFollowedLists(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

        db.collection("lists")
                .whereArrayContains("followers", userRef)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("FOLLOWED LIST", "Success: " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListItem item = document.toObject(ListItem.class);
                                listAdapter.add(item);
                            }
                        } else {
                            Log.d("FOLLOWED LIST", "Error getting documents: ", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListItem listItem = (ListItem) l.getItemAtPosition(position);
        Log.d(TAG_FOLLOWED_LIST_FRAGMENT, listItem.getName());

        Intent intent = new Intent(getActivity(), SelectGiftActivity.class);
        intent.putExtra("list", listItem);
        startActivity(intent);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            swipeRefreshLayout.setRefreshing(true);
            loadFollowedLists();
        }
    }

    @Override
    public void onRefresh() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            listAdapter.clear();
            loadFollowedLists();
        }
    }
}
