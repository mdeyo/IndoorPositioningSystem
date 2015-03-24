package com.example.matthew.newapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.util.ArrayList;


public class checkUpload extends Activity {

    String projectKey = "com.example.matthew.indoornav.projectNames";
    SharedPreferences prefs;
    ArrayList<String> projectNamesList = new ArrayList<String>();
    ListView projects;
    Button saveButton;
    String currentName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_upload);

        final ImageView image = (ImageView) findViewById(R.id.chosenImage);
        projects = (ListView) findViewById(R.id.list_projects_check);
        saveButton = (Button) findViewById(R.id.button_save);

///////////////////
        Resources resource = this.getResources();
        XmlPullParser parser = resource.getXml(R.layout.activity_check_upload);
        AttributeSet attributes = Xml.asAttributeSet(parser);

        final MyImageView miv = new MyImageView(this, attributes);

//        image.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(final View view, final MotionEvent event) {
//                return miv.onTouchEvent(event);
//            }
//        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //miv.SaveImage(getApplicationContext(),currentName,);
            }
        });


        image.setOnTouchListener(new Touch());

        prefs = this.getBaseContext().getSharedPreferences("com.example.matthew.indoornav", Context.MODE_PRIVATE);
        projectNamesList.clear();

        /////////////// projects //////////////////////
        String projectString = prefs.getString(projectKey, null);
        Log.d("from prefs", projectString);
        if (projectString != null) {
            String[] projectArray = projectString.split(" ");
            int length = projectArray.length;

            for (int i = 0; i < length; i++) {
                String name = projectArray[i];
                name = name.replaceAll("_", " ");
                if(!name.equals(" ") && !name.equals("") && !name.equals(null)) {
                    projectNamesList.add(name);
                }
            }
        }

        final CustomProjectListAdapter adpt = new CustomProjectListAdapter(this, R.layout.project_list_item, projectNamesList);
        projects.setAdapter(adpt);
        ////////////////////////////////////////////////////

        projects.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                        final int selectedPosition = position;
                        final String currentString = projects.getItemAtPosition(position).toString();
                        currentName = currentString;

                        Bitmap b = getImageBitmap(getApplicationContext(),currentString);
                        //image.setImageBitmap(b);

                        //Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.build_image);
                        image.setImageBitmap(b);

                    }
                }
        );

        projects.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> a, View v, final int position, long id) {

                        final int selectedPosition = position;
                        AlertDialog.Builder adb = new AlertDialog.Builder(checkUpload.this);
                        adb.setCancelable(false);

                        final String currentString = projects.getItemAtPosition(position).toString();

                        adb.setTitle("Project:" + currentString);
                        //adb.setMessage(routers);

                        adb.setPositiveButton("Cancel", null);
                        adb.setNegativeButton("Delete this project", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                projectNamesList.remove(selectedPosition);

                                String projectNamesString="";
                                for(int i=0;i<projectNamesList.size();i++){
                                    String Projectname = projectNamesList.get(i);
                                    Projectname = Projectname.replaceAll(" ", "_");
                                    projectNamesString=projectNamesString+" "+Projectname;
                                }

                                prefs.edit().putString(projectKey, projectNamesString).commit();
                                adpt.notifyDataSetChanged();
                            }
                        });

                        adb.show();
                        return true;

                    }
                });

    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_check_upload, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public Bitmap getImageBitmap(Context context,String name){
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            //String targetUri = fis.getFD().toString();
            //title.setText(targetUri);
            return b;

        }
        catch(Exception e){
        }
        return null;
    }
}
