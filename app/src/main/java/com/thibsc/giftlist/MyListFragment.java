package com.thibsc.giftlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

/**
 * Your gift lists (what you have created)
 */
public class MyListFragment extends ListFragment implements FirebaseAuth.AuthStateListener {

    public static final String TAG_MY_LIST_FRAGMENT = "MY_LIST_FRAGMENT";

    private ListAdapter listAdapter;
    private FloatingActionButton createListButton;
    private FirebaseAuth mAuth;

    public static MyListFragment newInstance(){
        return new MyListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_MY_LIST_FRAGMENT, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_mylist, container, false);

        listAdapter = new ListAdapter(getActivity());

        createListButton = view.findViewById(R.id.addlist_btn);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateListActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        setListAdapter(listAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    private void loadUserLists(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

        db.collection("lists")
                .whereEqualTo("creator", userRef)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("USER LIST", "Success: " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListItem item = new ListItem(
                                        firebaseUser.getDisplayName(),
                                        document.getString("name"),
                                        firebaseUser.getUid()
                                );
                                ArrayList<HashMap<String, Object>> gifts = (ArrayList<HashMap<String, Object>>)document.get("gifts");
                                for (HashMap<String, Object> h : gifts){
                                    GiftItem gift = new GiftItem(h.get("name").toString(), h.get("url").toString(), Integer.parseInt(h.get("amount").toString()));
                                    item.addGift(gift);
                                    Log.d("USER LIST GIFTS", h.toString());
                                }
                                listAdapter.add(item);
                                Log.d("USER LIST", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("USER LIST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListItem listItem = (ListItem) l.getItemAtPosition(position);
        Log.d(TAG_MY_LIST_FRAGMENT, listItem.getName());

        Intent intent = new Intent(getActivity(), CreateListActivity.class);
        intent.putExtra("list", listItem);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.deleteList:
                listAdapter.remove(listAdapter.getItem(info.position));
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            // From CreateListActivity
            // replace the old by the new if different
            ListItem list = (ListItem) data.getSerializableExtra("list");
            int idx = 0;
            boolean found = false;
            while (idx < listAdapter.getCount() && !found){
                if (list.equals(listAdapter.getItem(idx++))){
                    idx--;
                    found = true;
                }
            }
            if (found){
                // Remove the last
                listAdapter.remove(list);
                listAdapter.insert(list, idx);
            } else {
                list.setId(String.format("notsynchronized#%d", listAdapter.getCount()));
                listAdapter.insert(list, 0);
            }
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            loadUserLists();
            Snackbar.make(createListButton, R.string.fireauth_connection_ok, Snackbar.LENGTH_LONG).show();
        }
    }
}
