package com.svdnr.okuduunkitaplarnkaydet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {


    ImageView ımageView;
    EditText editText;
    Button button;
    static SQLiteDatabase database;//static ile her yerden ulaşabileceğiz.
    Bitmap selectedimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ımageView=findViewById(R.id.imageView);
        editText=findViewById(R.id.editText);
        button=findViewById(R.id.button);


        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if(info.equalsIgnoreCase("new")){

            Bitmap background= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.bgselectimage);
            // yeni bir kitap seçilecekse eski resimler gelmesin default olarak backgraound gelsin.
            button.setVisibility(View.VISIBLE);
            editText.setText("");
        }

        else {


            String name=intent.getStringExtra("name");
            editText.setText(name);
            int position=intent.getIntExtra("position",0);
            ımageView.setImageBitmap(MainActivity.bookImage.get(position));
            button.setVisibility(View.INVISIBLE);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void selectimage(View v) {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
        else{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//kitap ekleye tıklayınca galeriye erişmek için izin
            startActivityForResult(intent,1);

        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    if(requestCode==2){

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//kitap ekleye tıklayınca galeriye erişmek için izin
            startActivityForResult(intent,1);

        }
    }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK && data!=null)//kullanıcı resim seçmiş ise
        {


            Uri image=data.getData();
           try {
               selectedimage=MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
               ımageView.setImageBitmap(selectedimage);

           }catch (IOException e)

           {
               e.printStackTrace();
           }
        }


        super.onActivityResult(requestCode, resultCode, data);


    }

    public void save(View v){


         String  bookname = editText.getText().toString();//kullanıcının kaydettiği isme ulaştık.
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        selectedimage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytearray=outputStream.toByteArray();

        try {
            database=this.openOrCreateDatabase("Books",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS books (name VARCHAR,image BLOB)");
            String sqlString="INSERT INTO books(name,image)VALUES(?,?)";
            SQLiteStatement statement=database.compileStatement(sqlString);
            statement.bindString(1,bookname);
            statement.bindBlob(2,bytearray);
            statement.execute();

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }
}
