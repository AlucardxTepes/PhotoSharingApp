package com.parse.alucard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

  Context mContext;
  ArrayList<String> mUsernames;
  ArrayAdapter mArrayAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_list);

    mContext = this;

    final ListView userListView = (ListView) findViewById(R.id.userListView);

    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent(getApplicationContext(), UserFeed.class);
        intent.putExtra("username", mUsernames.get(i));
        startActivity(intent);

      }
    });

    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
    query.addAscendingOrder("username");

    query.findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> objects, ParseException e) {
        if (e == null) {
          if (objects.size() > 0) {
            mUsernames = new ArrayList<>();
            for (ParseUser user : objects) {
              mUsernames.add(user.getUsername());
            }
            mArrayAdapter = new ArrayAdapter(mContext,
                    android.R.layout.simple_list_item_1, mUsernames);
            userListView.setAdapter(mArrayAdapter);
          }
        } else {
          Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_user_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_share_picture:

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

        return true;
      case R.id.action_logout:
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);;
    }
    return false;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
      try {
        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                data.getData());
        // share image
        // turn image into bytes data
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        // store in byte array and convert it to parse file
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("image.png", byteArray);

        // upload to parse server database
        ParseObject object = new ParseObject("images"); // images table
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("image", file);
        // give public read access
        ParseACL parseACL = new ParseACL();
        parseACL.setPublicReadAccess(true);
        object.setACL(parseACL);

        object.saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Toast.makeText(getApplication().getBaseContext(), "Image posted!", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(getApplication().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } catch (IOException e) {
        Toast.makeText(getApplication().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    }
  }
}
