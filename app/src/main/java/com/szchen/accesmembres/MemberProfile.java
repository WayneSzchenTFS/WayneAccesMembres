package com.szchen.accesmembres;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MemberProfile extends AppCompatActivity {
    TextView prName, prEmail, prPhone;
    ImageView prImage;
    StorageReference storageReference;
    Button btnChange;
    public Uri imageUri;
    String memberID;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        prName=findViewById(R.id.prName);
        prEmail=findViewById(R.id.prEmail);
        prPhone=findViewById(R.id.prPhone);
        storageReference=FirebaseStorage.getInstance().getReference();
        memberID=firebaseAuth.getCurrentUser().getUid();
        prImage=findViewById(R.id.prImage);
        btnChange=findViewById(R.id.btnChange);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

//afficher l'image du profil usage
        StorageReference profilRef=storageReference.child("users/" + memberID + "/profile.jpg");
        profilRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prImage);
            }
        });
        DocumentReference documentReference=firestore.collection("members").document(memberID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                prName.setText(value.getString("name"));
                prEmail.setText(value.getString("email"));
                prPhone.setText(value.getString("phone"));
            }
        });
//ajouter image
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
//fin ajouter image
    }
    //ResultLauncher
    ActivityResultLauncher<Intent> someActivityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==Activity.RESULT_OK) {
                        Intent data=result.getData();
                        imageUri=data.getData();
                        prImage.setImageURI(imageUri);
//Envoyer l'image sur le storage de firebase
                        uploadPicture(imageUri);
                    }
                }

                private void uploadPicture(Uri imageUri) {
                }
            });

    private void choosePicture()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}