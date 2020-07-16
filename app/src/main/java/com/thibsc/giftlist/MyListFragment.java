package com.thibsc.giftlist;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Your gift lists (what you have created)
 */
public class MyListFragment extends ListFragment implements FirebaseAuth.AuthStateListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG_MY_LIST_FRAGMENT = "MY_LIST_FRAGMENT";
    private final int MYLIST_GROUPID = 0;

    private ListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        swipeRefreshLayout = view.findViewById(R.id.mylist_refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

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
                                ListItem item = document.toObject(ListItem.class);
                                listAdapter.add(item);
                                Log.d("USER LIST", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("USER LIST", "Error getting documents: ", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
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
        //getActivity().getMenuInflater().inflate(R.menu.list_menu, menu);
        menu.add(MYLIST_GROUPID, R.id.copyIdList, Menu.NONE, R.string.copy);
        menu.add(MYLIST_GROUPID, R.id.deleteList, Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean ret = super.onContextItemSelected(item);

        if (item.getGroupId() == MYLIST_GROUPID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case R.id.deleteList:
                    listAdapter.remove(listAdapter.getItem(info.position));
                    break;
                case R.id.copyIdList:
                    String idlist = listAdapter.getItem(info.position).getId().substring("users/".length());

                    // Copy the ID of the list in the clipboard
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("idlist", idlist);
                    clipboard.setPrimaryClip(data);
                    Snackbar.make(createListButton, R.string.copy_done, Snackbar.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            ret = true;
        }

        return ret;
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
                ListItem new_listitem = listAdapter.getItem(idx);
                new_listitem.setGifts(list.getGifts());
                listAdapter.remove(list);
                listAdapter.insert(new_listitem, idx);
                updateUserList(new_listitem);
            } else {
                //list.setId(String.format("notsynchronized#%d", listAdapter.getCount()));
                addUserList(list);
                listAdapter.insert(list, 0);
            }
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            swipeRefreshLayout.setRefreshing(true);
            loadUserLists();
            Snackbar.make(createListButton, R.string.fireauth_connection_ok, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            listAdapter.clear();
            loadUserLists();
        }
    }

    public void addUserList(ListItem list){
        // Create the user document to firebase
        // Due to the security rules in the firebase configuration, if the document exists, it does nothing
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newListRef = db.collection("lists").document();

        // Later...
        newListRef.set(list, SetOptions.merge())
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Snackbar.make(createListButton, "List saved with success", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(createListButton, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateUserList(final ListItem li){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

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
                                doc_ref.set(li, SetOptions.merge());
                            }
                        } else {
                            // Can't arrive, just a no result in the if statement
                            //Log.d("FOLLOWED LIST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
