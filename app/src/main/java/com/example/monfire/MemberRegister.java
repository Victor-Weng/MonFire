package com.example.monfire;

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

import java.util.HashMap;
import java.util.Map;

public class MemberRegister extends AppCompatActivity {
    EditText txtNom,registerEmail,registerPasse,txtPhone;
    Button btnSubmit;
    TextView txtLoginR;
    ProgressBar progressBarRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    String membreID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_register);
        // champs a utiliser
        txtNom=findViewById(R.id.txtNom);
        registerEmail=findViewById(R.id.registerEmail);
        registerPasse=findViewById(R.id.registerPasse);
        txtPhone=findViewById(R.id.txtPhone);
        btnSubmit=findViewById(R.id.btnSubmit);
        progressBarRegister=findViewById(R.id.progressBarRegister);
        txtLoginR=findViewById(R.id.txtLoginR);
        fAuth= FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = registerEmail.getText().toString().trim();
                String passe = registerPasse.getText().toString().trim();
                String nomComplet = txtNom.getText().toString();
                String phone = txtPhone.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    registerEmail.setError("L'adresse email est requise");
                    return;
                }
                if(TextUtils.isEmpty(passe))
                {
                    registerPasse.setError("Le mot de passe est requis");
                    return;
                }
                if(passe.length()<6)
                {
                    registerPasse.setError("Minimum 6 caracateres pour le mot de passe");
                    return;
                }
                progressBarRegister.setVisibility(View.VISIBLE);
                // sauvegarde des donnees
                fAuth.createUserWithEmailAndPassword(email,passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MemberRegister.this,"Compte cree", Toast.LENGTH_SHORT).show();

                            // creer la base de donnees
                            membreID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=firestore.collection("membres").document(membreID);
                            Map<String,Object> membre=new HashMap<>();
                            membre.put("nom",nomComplet);
                            membre.put("email", email);
                            membre.put("phone", phone);
                            documentReference.set(membre).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "Profil cree pour le membre "+membreID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MemberProfile.class));
                        }
                        else
                        {
                            Toast.makeText(MemberRegister.this, "Une erreur s'est produite "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBarRegister.setVisibility(View.GONE);
                        }

                    }
                });

                // fin sauvegarde
            }
        });
        txtLoginR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MemberLogin.class));
            }
        });


    }

}