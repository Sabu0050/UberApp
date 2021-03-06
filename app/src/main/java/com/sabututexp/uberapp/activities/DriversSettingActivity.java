package com.sabututexp.uberapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sabututexp.uberapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriversSettingActivity extends AppCompatActivity {

    private EditText mNameEditText, mPhoneNumberEditText, mCarNumberEditText;
    private Button mConfirmButton, mBackButton;
    private ImageView mProfileImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String driverID;
    private String mName;
    private String mPhone;
    private String mCarNumber;
    private String mService;
    private String mProfileImageUrl;

    private Uri resultUri;
    private RadioGroup mRadioGroup;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_setting);

        mProfileImageView = (ImageView) findViewById(R.id.profileImageView);
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        mCarNumberEditText = (EditText) findViewById(R.id.carNumberEditText);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mConfirmButton = (Button) findViewById(R.id.confirmButton);
        mBackButton = (Button) findViewById(R.id.backButton);

        mAuth = FirebaseAuth.getInstance();

        driverID = mAuth.getCurrentUser().getUid();

        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(driverID);
        getDriverInfo();

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                saveDriverInformation();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                finish();
                return;
            }
        });
    }

    private void getDriverInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String ,Object>) dataSnapshot.getValue();

                    if(map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameEditText.setText(mName);
                    }
                    if(map.get("phone") != null){
                        mPhone = map.get("phone").toString();
                        mPhoneNumberEditText.setText(mPhone);
                    }
                    if(map.get("carNumber") != null){
                        mPhone = map.get("carNumber").toString();
                        mCarNumberEditText.setText(mPhone);
                    }
                    if(map.get("service")!=null) {
                        mService = map.get("service").toString();
                        switch (mService) {
                            case "UberX":
                                mRadioGroup.check(R.id.UberX);
                                break;
                            case "UberBlack":
                                mRadioGroup.check(R.id.UberBlack);
                                break;
                            case "UberXl":
                                mRadioGroup.check(R.id.UberXl);
                                break;
                        }
                    }

                    if (map.get("profileImageUrl") != null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplicationContext()).load(mProfileImageUrl).into(mProfileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private  void saveDriverInformation() {

        mName = mNameEditText.getText().toString();
        mPhone = mPhoneNumberEditText.getText().toString();
        mCarNumber = mCarNumberEditText.getText().toString();

        int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if (radioButton.getText() == null){
            return;
        }

        mService = radioButton.getText().toString();

        Map driverInfo = new HashMap();
        driverInfo.put("name",mName);
        driverInfo.put("phone",mPhone);
        driverInfo.put("carNumber",mCarNumber);
        driverInfo.put("service", mService);
        mDriverDatabase.updateChildren(driverInfo);

        if(resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(driverID);

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mDriverDatabase.updateChildren(newImage);
                    finish();
                    return;
                }
            });
        }else {
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK ){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImageView.setImageURI(resultUri);
        }
    }
}
