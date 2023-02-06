package com.szchen.accesmembres;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MemberLogin extends AppCompatActivity {
    TextView loRegister,loForgor;
    EditText loEmail,loPass;
    Button btnLogin;
    ProgressBar loPBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);
        loRegister=findViewById(R.id.loRegister);
        loForgor=findViewById(R.id.loForgor);
        loEmail=findViewById(R.id.loEmail);
        loPass=findViewById(R.id.loPass);
        btnLogin=findViewById(R.id.btnLogin);
        loPBar=findViewById(R.id.loPBar);
        fAuth=FirebaseAuth.getInstance();
//validation des entrées et login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=loEmail.getText().toString().trim();
                String passe=loPass.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    loEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(passe))
                {
                    loPass.setError("Password is required");
                    return;
                }
                if(passe.length()<6)
                {
                    loPass.setError("Password is too short");
                    return;
                }
                loPBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email, passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MemberLogin.this, "Member connected", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MemberProfile.class));
                        }
                        else
                        {
                            Toast.makeText(MemberLogin.this, "An error has occurred" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loPBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
//fin validation
        loRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MemberRegister.class));
            }
        });
//mot de passe oublié
        loForgor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                EditText resetMail=new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Forgot Password?");
                passwordResetDialog.setMessage("Insert your email to receive the link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//recupérer le email et envoyer le lien
                        String mail=resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MemberLogin.this, "The link was sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MemberLogin.this, "The link couldn't be sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//Ne rien faire et fermer le dialogue
                    }
                });
                passwordResetDialog.show();
            }
        });
//fin rappel mot de passe
    }
}