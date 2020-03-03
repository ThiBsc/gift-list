package com.thibsc.giftlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * The fragment that allow you to create a gift list
 */
public class CreateListFragment extends Fragment {

    private TextView id;
    private EditText nameList;
    private Button btnAddGift;
    private ListView giftList;
    private FloatingActionButton saveListBtn;
    private GiftAdapter giftAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createlist, container, false);

        id = view.findViewById(R.id.listId);
        nameList = view.findViewById(R.id.listName);
        btnAddGift = view.findViewById(R.id.btnAddGift);
        giftList = view.findViewById(R.id.myNewlist_gistList);
        saveListBtn = view.findViewById(R.id.savelist_btn);

        giftAdapter = new GiftAdapter(getActivity(), false);
        giftList.setAdapter(giftAdapter);

        btnAddGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGiftActivity.class);
                getActivity().startActivityForResult(intent, 1);
            }
        });

        saveListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameList.getText().length() == 0){
                    Toast.makeText(getContext(), R.string.missing_namelist, Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    final DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

                    ListItem list = new ListItem(userRef, firebaseUser.getDisplayName(), nameList.getText().toString(), new ArrayList<DocumentReference>(), new ArrayList<GiftItem>());
                    for (int i=0; i<giftList.getCount(); i++){
                        list.addGift(giftAdapter.getItem(i));
                    }

                    Intent data = new Intent();
                    data.putExtra("list", list);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(giftList);
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
                giftAdapter.remove(giftAdapter.getItem(info.position));
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
