package com.example.penpapercoder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.CoderMalfunctionError;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CompileActivity extends AppCompatActivity {
    public String path;
   public EditText Result;
   public EditText etInput;
    TextView btn_userlogout;
    TextView clear;
    Button btnSubmit;
    Button view_pdf_btn;
    Button txt_pdf_btn;
    EditText input_txt;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    String code;
    APIClient api = APIClient.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile);
//Get All Widget Value Of XML FilestorageReference

        Result       =findViewById(R.id.tv_result);
        etInput      = findViewById(R.id.et_input);
        input_txt    =findViewById(R.id.input_txt);
        btnSubmit    = findViewById(R.id.btn_submit);
        view_pdf_btn =findViewById(R.id.view_pdf_btn);
        txt_pdf_btn  =findViewById(R.id.txt_pdf_btn);
        btn_userlogout=findViewById(R.id.btn_userlogout);
        clear = findViewById(R.id.clear);
        code=getIntent().getExtras().getString("Code");
        etInput.setText(code);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        //Handle Clear Button
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etInput.setText("");
            }
        });

        //  To  Handle User Logout
        btn_userlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                Toast.makeText(CompileActivity.this, "Logout Successful ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CompileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
//To Generate PDF
        txt_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String inputCode = etInput.getText().toString();
                String input =input_txt.getText().toString();
                String output = Result.getText().toString();
                //pdf will be stored at this path
                path = getExternalFilesDir(null).toString()+"/"+getFileName();
                File file = new File(path);
// To Check File Exists or Not
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document,new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    Toast.makeText(CompileActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                }
                document.open();


                Font myFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("Program Code :\n"+inputCode,myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Input :\n"+ input,myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Output :\n"+output,myFont));

                try {
                    document.add(paragraph);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                document.close();
                Toast.makeText(CompileActivity.this,"Pdf Created Successfully.",Toast.LENGTH_SHORT).show();

            }
        });

        // To view pdf functionality it is same as get pdf functionality....
        view_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //To Pass the path

                String inputCode = etInput.getText().toString();
                String input =input_txt.getText().toString();
                String output = Result.getText().toString();

                //pdf will be stored at this path
                Intent intent = new Intent(getApplicationContext(), ViewPDFActivity.class);
                path = getExternalFilesDir(null).toString()+"/"+getFileName();
                File file = new File(path);
               // To Check File Exists or Not
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document,new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    Toast.makeText(CompileActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                }
                document.open();

                Font myFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("Program Code :\n"+inputCode,myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Input :\n"+input,myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Output :\n"+output,myFont));

                try {
                    document.add(paragraph);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                document.close();
                intent.putExtra("path",path);
                startActivity(intent);
            }
        });

    }
 //To Generate New Name Of File Each Time.

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getFileName() {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        return "User_" + timeStamp + "_.pdf";
    }

    // Function of compile Button
    public void runCompile(View view) {
        Call<String> execute = api.getAPI().execute(new PostData(etInput.getText().toString(), input_txt.getText().toString()));

        Result.setText("Loading...");

        execute.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Result.setText("");

                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String output = jsonObject.getString("output")+"\nMemory Used:  "+jsonObject.getString("memory") +"\nCPU Time :"+ jsonObject.getString("cpuTime");
                        Result.setText(output);
                    } else {
                        assert response.errorBody() != null;
                        showSnackBar(response.errorBody().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showSnackBar("Something Went Wrong  : " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Result.setText("Failed");
                showSnackBar("An error occurred : " + t.getMessage());
            }
        });
    }

    private void showSnackBar(String message) {
        Toast.makeText(CompileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
