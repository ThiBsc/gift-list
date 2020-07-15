package com.thibsc.giftlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The lists that you following
 */
public class FollowedListFragment extends Fragment implements FirebaseAuth.AuthStateListener, SwipeRefreshLayout.OnRefreshListener, View.OnCreateContextMenuListener {

    public static final String TAG_FOLLOWED_LIST_FRAGMENT = "FOLLOWED_LIST_FRAGMENT";
    private final int FOLLOWED_GROUPID = 2;

    private ExpandableListView expandableListView;
    private ExpandableListFollowedAdapter expandableListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton followListButton;
    private FirebaseAuth mAuth;

    public static FollowedListFragment newInstance(){
        return new FollowedListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_FOLLOWED_LIST_FRAGMENT, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_followedlist, container, false);

        expandableListView = view.findViewById(R.id.expandableListView);
        expandableListAdapter = new ExpandableListFollowedAdapter(getContext());
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnCreateContextMenuListener(this);

        swipeRefreshLayout = view.findViewById(R.id.followedlist_refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        followListButton = view.findViewById(R.id.followlist_btn);
        followListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Référence liste");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ref_list = input.getText().toString();
                        addFollowedList(String.format("users/%s", ref_list));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        //setListAdapter(listAdapter)
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(expandableListView);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);

        // Only create a context menu for child items
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
            //getActivity().getMenuInflater().inflate(R.menu.followedlist_menu, menu);

            GiftItem gi = (GiftItem) expandableListAdapter.getChild(group, child);
            if (!gi.isBuy()){
                menu.add(FOLLOWED_GROUPID, R.id.getGift, Menu.NONE, R.string.getgift);
            } else {
                if (gi.getGet_by().get("user").equals(String.format("users/%s", FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                    menu.add(FOLLOWED_GROUPID, R.id.getGift_cancel, Menu.NONE, R.string.getgift_cancel);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean ret = super.onContextItemSelected(item);

        if (item.getGroupId() == FOLLOWED_GROUPID){
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

            int groupPos = 0, childPos = 0;
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
            }

            switch (item.getItemId()){
                case R.id.getGift:
                    getThisGift(groupPos, childPos, false);
                    break;
                case R.id.getGift_cancel:
                    getThisGift(groupPos, childPos,true);
                    break;
                default:
                    break;
            }
            ret = true;
        }

        return ret;
    }

    private void getThisGift(final int listPosition, final int giftPosition, final boolean cancel){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

        final ListItem li = (ListItem) expandableListAdapter.getGroup(listPosition);

        db.collection("lists")
                .whereEqualTo("id", li.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("FOLLOWED LIST", "Success: " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListItem item = document.toObject(ListItem.class);
                                DocumentReference doc_ref = db.collection("lists").document(document.getId());

                                Map<String, Object> gifts = new HashMap<>();
                                GiftItem gi = (GiftItem) expandableListAdapter.getChild(listPosition, giftPosition);
                                if (cancel){
                                    gi.setGet_by(null);
                                } else {
                                    HashMap<String, Object> buyer = new HashMap<>();
                                    buyer.put("user", userRef.getPath());
                                    buyer.put("user_displayname", firebaseUser.getDisplayName());
                                    gi.setGet_by(buyer);
                                }

                                ArrayList<GiftItem> new_giftlist = li.getGifts();
                                new_giftlist.set(giftPosition, gi);
                                li.setGifts(new_giftlist);

                                gifts.put("gifts", new_giftlist);
                                doc_ref.set(gifts, SetOptions.mergeFields("gifts"));

                                // Update the view
                                expandableListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            // Can't arrive, just a no result in the if statement
                            //Log.d("FOLLOWED LIST", "Error getting documents: ", task.getException());
                        }
                    }
                });
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
                                expandableListAdapter.addList(item);
                                expandableListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("FOLLOWED LIST", "Error getting documents: ", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void addFollowedList(String ref_list){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

        db.collection("lists")
                .whereEqualTo("id", ref_list)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("FOLLOWED LIST", "Success: " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListItem item = document.toObject(ListItem.class);
                                if (item.addFollower(userRef.getPath())){
                                    DocumentReference doc_ref = db.collection("lists").document(document.getId());

                                    Map<String, Object> followers = new HashMap<>();
                                    ArrayList<DocumentReference> followers_ref = new ArrayList<DocumentReference>();
                                    for (String f : item.getFollowers()){
                                        followers_ref.add(db.collection("users").document(f.split("/")[1]));
                                    }
                                    followers.put("followers", followers_ref);
                                    doc_ref.set(followers, SetOptions.mergeFields("followers"));
                                    expandableListAdapter.addList(item);
                                    expandableListAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            // Can't arrive, just a no result in the if statement
                            //Log.d("FOLLOWED LIST", "Error getting documents: ", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
            expandableListAdapter.clear();
            loadFollowedLists();
        }
    }
}
