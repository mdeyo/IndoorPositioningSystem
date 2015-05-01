package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class UploadImage extends Activity {

    TextView textTargetUri;
    ImageView targetImage, holderImage;
    Button buttonLoadImage, buttonSaveImage;
    String chosenName;
    EditText projectNameInput;
    Bitmap chosenImage;
    SharedPreferences prefs;
    List<String> projectNamesList = new ArrayList<String>();
    String projectKey = "com.example.matthew.indoornav.projectNames";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        buttonLoadImage = (Button) findViewById(R.id.loadimage);
        buttonSaveImage = (Button) findViewById(R.id.saveimage);
        textTargetUri = (TextView) findViewById(R.id.targeturi);
        targetImage = (ImageView) findViewById(R.id.targetimage);
        holderImage = (ImageView) findViewById(R.id.holder_image);
        projectNameInput = (EditText) findViewById(R.id.editName);

        //TODO uncomment this after testing
        //buttonSaveImage.setVisibility(View.GONE);

        prefs = getBaseContext().getSharedPreferences("com.example.matthew.indoornav", Context.MODE_PRIVATE);

        /////////////// projects //////////////////////
        String projectString = prefs.getString(projectKey, null);
        if (projectString != null) {
            String[] projectArray = projectString.split(" ");
            int length = projectArray.length;

            for (int i = 0; i < length; i++) {
                String name = projectArray[i];
                name = name.replaceAll("_", " ");
                projectNamesList.add(name);
            }
        }
        ////////////////////////////////////////////////////

        buttonLoadImage.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);

            }
        });

        buttonSaveImage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                chosenName = projectNameInput.getText().toString();
                if (!chosenName.equals("") && !chosenName.equals(" ") && !chosenName.equals("  ")) {
                    projectNamesList.add(chosenName);
                }

                String projectNamesString = "";
                for (int i = 0; i < projectNamesList.size(); i++) {
                    String Projectname = projectNamesList.get(i);
                    if (!Projectname.equals("") && !Projectname.equals(" ")) {
                        Projectname = Projectname.replaceAll(" ", "_");
                        projectNamesString = projectNamesString + " " + Projectname;
                    }
                }

                prefs.edit().putString(projectKey, projectNamesString).commit();
                saveImage(getApplicationContext(), chosenImage, chosenName);

                Intent nextScreen = new Intent(getApplicationContext(), checkUpload.class);
                startActivity(nextScreen);

            }
        });
    }

    public void saveImage(Context context, Bitmap b, String name) {

        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            textTargetUri.setText(targetUri.toString());
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(bitmap);
                chosenImage = bitmap;
                buttonSaveImage.setVisibility(View.VISIBLE);
                holderImage.setVisibility(View.INVISIBLE);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
