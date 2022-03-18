package com.example.monfire;

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
    //Layout
    TextView nomMembre, emailMembre, phoneMembre;
    ImageView profileImage;
    Button addProfileImage;
    //autre
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String membreID;
    StorageReference storageReference;
    public Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        nomMembre=findViewById(R.id.nomMembre);
        emailMembre=findViewById(R.id.emailMembre);
        phoneMembre=findViewById(R.id.phoneMembre);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        membreID=firebaseAuth.getCurrentUser().getUid();
        profileImage=findViewById(R.id.profileImage);
        addProfileImage=findViewById(R.id.addProfileImage);

        //afficher l'image du profil usager
        StorageReference profileRef=storageReference.child("users/"+membreID+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(profileImage);
            }
        });

        DocumentReference documentReference=firestore.collection("membres").document(membreID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nomMembre.setText(value.getString("nom"));
                emailMembre.setText(value.getString("email"));
                phoneMembre.setText(value.getString("phone"));
            }
        });

        // ajouter image
        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choosePicture();
            }
        });
        // fin ajouter image

    }

    //ResultLauncher
    ActivityResultLauncher<Intent> someAcitivtyResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri=data.getData();
                        profileImage.setImageURI(imageUri);
                        //Envoyer l'image sur le storage de firebase
                        uploadPicture(imageUri);
                    }
                }
            });

    private void uploadPicture(Uri imageUri)

    {

    }

    //onCreate termine

    private void choosePicture()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someAcitivtyResultLauncher.launch(intent);

    }

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}