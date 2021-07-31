package com.example.penpapercoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    TextView btn_userlogout;
    TextView clear;
    EditText mEditText;
    ImageView mImageView;
    ImageButton scanButton;
    ImageButton voice_to_text;
    Button compile_btn;
    String code ;
    String cameraPermisions[];
    String storagePermision[];
    Uri imageUril;

    private static final int REQUEST_CODE_FOR_CAMERA = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int GALLERY_CODE = 700;
    private static final int PICK_CAMERA_CODE = 1000;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  get all view id from XML
       btn_userlogout = findViewById(R.id.btn_userlogout);
       compile_btn =findViewById(R.id.compile_btn);
       mEditText = findViewById(R.id.scanResult);
       clear=findViewById(R.id.clear);
       mImageView = findViewById(R.id.image_display);
       scanButton =findViewById(R.id.scan_btn);
       voice_to_text =findViewById(R.id.voice_to_text);
       //  Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

       // Get Permission
        cameraPermisions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermision= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
       // To Display Import Image DialogBox

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageImportDialog();
            }
        });

        //Clear Text
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mEditText.setText("");
             mImageView.setImageDrawable(null);
            }
        });
         // Compile Button Action
        compile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompileActivity.class);
                code = mEditText.getText().toString();
                intent.putExtra("Code",code);
                startActivity(intent);
            }
        });

        voice_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSpeech();
            }
        });

        
// Handle Logout button
       btn_userlogout.setOnClickListener(new View.OnClickListener() {
          @Override
         public void onClick(View v) {
               mAuth.signOut();
               Toast.makeText(MainActivity.this, "Logout Successful ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

// If User Is Already Or Not Functionality
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            //There is user already logged in
        }else{
            //No User Logged In
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }
//Show Image Import Dialog Part and its implementation
private void showImageImportDialog() {
    String[] items = {"Camera", "Gallery"};
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle("Select image to scan.");
    dialog.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestPermissions();
                } else {
                    pickCamera();
                }
            }
            if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermissions();
                } else {
                    pickGalery();
                }
            }
        }
    });
    dialog.create().show();
}
// When Camera Option is selected
    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPhoto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        imageUril = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUril);
        startActivityForResult(cameraIntent, PICK_CAMERA_CODE);
    }

   // When Gallery Option Is Selected

    private void pickGalery() {
        Intent myIntent = new Intent(Intent.ACTION_PICK);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, GALLERY_CODE);
    }


    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, storagePermision, STORAGE_REQUEST_CODE);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermisions, REQUEST_CODE_FOR_CAMERA);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    // To deal And Handle Storage Permission According to specific Device

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAcceptedb = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAcceptedb) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAcceptedb = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAcceptedb) {
                        pickGalery();
                    } else {
                        Toast.makeText(this, "permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Voice To Text Request
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mEditText.setText(result.get(0));

                }
                break;


        }
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_CAMERA_CODE) {
                CropImage.activity(imageUril)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mImageView.setImageURI(resultUri);
                //Mistake Must Be Here
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!recognizer.isOperational()) {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < items.size(); j++) {
                            TextBlock myItems = items.valueAt(j);
                            sb.append(myItems.getValue());
                            sb.append("\n");

                        }
                        mEditText.append(("\n" + sb.toString()));
                        // Finally Getting recognize extracted text in mEditText Block
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();

                }

            }
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    public void btnSpeech() {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Try To Speak Your Code");
            try{
                startActivityForResult(intent,1);
            }catch (ActivityNotFoundException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }
}