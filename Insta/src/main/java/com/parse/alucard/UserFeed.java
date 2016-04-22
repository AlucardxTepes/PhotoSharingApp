package com.parse.alucard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeed extends AppCompatActivity {

  LinearLayout mLinearLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_feed);

    mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

    Intent i = getIntent();
    String activeUsername = i.getStringExtra("username");
    setTitle(activeUsername + "'s Feed");

    // find all images for the active username
    ParseQuery<ParseObject> query = new ParseQuery<>("images");
    query.whereEqualTo("username", activeUsername);
    query.orderByDescending("createdAt");

    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> objects, ParseException e) {
        if (e == null) {
          if (objects.size() > 0) {

            for (ParseObject image : objects) {

              ParseFile imageFile = (ParseFile) image.get("image");

              imageFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                  if (e == null) {
                    //convert the downloaded file to an image
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    // create view from image
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageBitmap(imageBitmap);
                    // set view properties
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    // add it to the layout
                    mLinearLayout.addView(imageView);
                    Toast.makeText(getApplicationContext(), "Image set", Toast.LENGTH_SHORT).show();
                  } else {
                    Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                  }
                }
              });
            }
          }
        }else {
          Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });


  }
}
