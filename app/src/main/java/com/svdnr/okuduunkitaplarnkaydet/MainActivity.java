package com.svdnr.okuduunkitaplarnkaydet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    static ArrayList<Bitmap> bookImage=new ArrayList<Bitmap>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_book,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_book);

        {

            Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView=findViewById(R.id.listview);

        final ArrayList<String> bookName=new ArrayList<String>();
        bookImage=new ArrayList<Bitmap>();
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,bookName);
        listView.setAdapter(arrayAdapter);

        try {

            Main2Activity.database=this.openOrCreateDatabase("Books",MODE_PRIVATE,null);
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS books(name VARCHAR,image BLOB)");
            Cursor cursor=Main2Activity.database.rawQuery("SELECT *FROM books",null);
            int nameIx=cursor.getColumnIndex("name");
            int imageIx=cursor.getColumnIndex("image");
            cursor.moveToFirst();
            while (cursor!=null){

                bookName.add(cursor.getString(nameIx));
                byte[]byteArray=cursor.getBlob(imageIx);
                Bitmap image= BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                bookImage.add(image);
                cursor.moveToNext();
                arrayAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();

        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("info","old");
                intent.putExtra("name",bookName.get(position));
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });
    }




}
