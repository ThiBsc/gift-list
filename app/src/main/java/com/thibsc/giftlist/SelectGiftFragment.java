package com.thibsc.giftlist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * To select a gift and say "I take this gift"
 */
public class SelectGiftFragment extends Fragment implements View.OnCreateContextMenuListener {

    private TextView listName, creator;
    private ListView giftLift;
    private GiftAdapter giftAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selectgift, container, false);

        listName = view.findViewById(R.id.selectGift_listName);
        creator = view.findViewById(R.id.selectGift_creator);
        giftLift = view.findViewById(R.id.selectGift_giftList);
        giftLift.setOnCreateContextMenuListener(this);

        giftAdapter = new GiftAdapter(getActivity(), true);
        giftLift.setAdapter(giftAdapter);
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.followedlist_menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        GiftItem gi = giftAdapter.getItem(position);
        if (!gi.isBuy()){
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
        } else {
            menu.getItem(0).setEnabled(false);
            if (gi.getGet_by().get("user").equals(String.format("users/%s", FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                menu.getItem(1).setEnabled(true);
            } else {
                menu.getItem(1).setEnabled(false);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.getGift:
                getThisGift(info.position, false);
                break;
            case R.id.getGift_cancel:
                getThisGift(info.position,true);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void getThisGift(final int giftPosition, final boolean cancel){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

        String idList = ((TextView) getView().findViewById(R.id.listRef)).getText().toString();

        db.collection("lists")
                .whereEqualTo("id", idList)
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
                                ArrayList<GiftItem> giftsList = new ArrayList<>();
                                GiftItem gi = giftAdapter.getItem(giftPosition);
                                if (cancel){
                                    gi.setGet_by(null);
                                } else {
                                    HashMap<String, Object> buyer = new HashMap<>();
                                    buyer.put("user", userRef.getPath());
                                    buyer.put("user_displayname", firebaseUser.getDisplayName());
                                    gi.setGet_by(buyer);
                                }

                                int nGift = giftAdapter.getCount();
                                for (int i=0; i<nGift; i++){
                                    if (i == giftPosition){
                                        giftsList.add(gi);
                                    } else {
                                        giftsList.add(giftAdapter.getItem(i));
                                    }
                                }
                                gifts.put("gifts", giftsList);
                                doc_ref.set(gifts, SetOptions.mergeFields("gifts"));

                                // Update the view
                                giftAdapter.clear();
                                giftAdapter.addAll(giftsList);
                                //getView().findViewById(R.id.selectGift_giftList).invalidate();
                            }
                        } else {
                            // Can't arrive, just a no result in the if statement
                            //Log.d("FOLLOWED LIST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
