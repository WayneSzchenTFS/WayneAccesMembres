package com.szchen.accesmembres;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.szchen.accesmembres.MemberLogin;
import com.szchen.accesmembres.MemberProfile;
import com.szchen.accesmembres.R;

import java.util.HashMap;
import java.util.Map;

public class MemberRegister extends AppCompatActivity {
    EditText reName,reEmail,rePass,rePhone;
    Button btnRegister;
    TextView reLogin;
    ProgressBar rePBar;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    String memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_register);
//champs à utiliser
        reName=findViewById(R.id.reName);
        reEmail=findViewById(R.id.reEmail);
        rePass=findViewById(R.id.rePass);
        rePhone=findViewById(R.id.rePhone);
        btnRegister=findViewById(R.id.btnRegister);
        rePBar=findViewById(R.id.rePBar);
        reLogin=findViewById(R.id.reLogin);
        fAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String nom=reName.getText().toString().trim();
                String email=reEmail.getText().toString().trim();
                String passe=rePass.getText().toString().trim();
                String phone=rePhone.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    reEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(passe))
                {
                    rePass.setError("Password is required");
                    return;
                }
                if(passe.length()<6)
                {
                    rePass.setError("Password is too short");
                    return;
                }
                rePBar.setVisibility(View.VISIBLE);
//sauvegarde les données
                fAuth.createUserWithEmailAndPassword(email, passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MemberRegister.this, "Account created", Toast.LENGTH_SHORT).show();
//créer la base de données
                            memberID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=firestore.collection("members").document(memberID);
                            Map<String,Object> membre=new HashMap<>();
                            membre.put("name", nom);
                            membre.put("email", email);
                            membre.put("phone", phone);
                            documentReference.set(membre).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "Profile created" + memberID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MemberProfile.class));
                        }
                        else
                        {
                            Toast.makeText(MemberRegister.this, "An error has occurred" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            rePBar.setVisibility(View.GONE);
                        }
                    }
                });
//fin sauvegarde
            }
        });
        reLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), MemberLogin.class));
            }
        });
    }
}