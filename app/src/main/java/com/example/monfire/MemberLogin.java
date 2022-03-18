package com.example.monfire;

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
    TextView txtNouveau, forgotPasse;
    EditText LoginEmail, LoginPasse;
    Button btnValider;
    ProgressBar progressBar2;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);
        txtNouveau=findViewById(R.id.txtNouveau);
        LoginEmail=findViewById(R.id.LoginEmail);
        LoginPasse=findViewById(R.id.LoginPasse);
        btnValider=findViewById(R.id.btnValider);
        progressBar2=findViewById(R.id.progressBar2);
        fAuth=FirebaseAuth.getInstance();
        forgotPasse=findViewById(R.id.forgotPasse);

        //Validation des entrees et login

        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = LoginEmail.getText().toString().trim();
                String passe = LoginPasse.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    LoginEmail.setError("L'adresse email est requise");
                    return;
                }
                if(TextUtils.isEmpty(passe))
                {
                    LoginPasse.setError("Le mot de passe est requis");
                    return;
                }
                if(passe.length()<6)
                {
                    LoginPasse.setError("Minimum 6 caracateres pour le mot de passe");
                    return;
                }
                progressBar2.setVisibility(view.VISIBLE);
                //rechercher les donnees
                fAuth.signInWithEmailAndPassword(email, passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MemberLogin.this, "Membre connecte", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MemberProfile.class));
                        }
                        else
                        {
                            Toast.makeText(MemberLogin.this, "Une erreur s'est produite "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        // fin validation

        // On click listener sur txtNouveau pour creer un novueaux compte

        txtNouveau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MemberRegister.class));
            }
        });

        // mot de passe oublier, reset password
        forgotPasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail=new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Mot de passe oublie?");
                passwordResetDialog.setMessage("Ecris ton courriel pour recevoir le lien");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // recuperer le email et envoyer le lien
                        String courriel=resetMail.getText().toString();

                        // Send courriel from the instance of fAuth for this account. Add on success listener to verify if the email is sent
                        fAuth.sendPasswordResetEmail(courriel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MemberLogin.this, "Le lien a ete envoye a ton adresse", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MemberLogin.this, "Le lien n'a pas ete envoye "+ e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ne rien faire et fermer le dialogue
                    }
                });
                passwordResetDialog.show();
            }
        });

        // fin rappel mot de passe

    }
}