package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Window;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;

public class ImageViewTest extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_view_test);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView1);

        Resources resource = this.getResources();
        XmlPullParser parser = resource.getXml(R.layout.activity_image_view_test);
        AttributeSet attributes = Xml.asAttributeSet(parser);

        final MyImageView miv = new MyImageView(this, attributes);
//
//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(final View view, final MotionEvent event) {
//                return miv.onTouchEvent(event);
//            }
//        });


        imageView.setOnTouchListener(new Touch());

        Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.build_image);
        imageView.setImageBitmap(map);

    }

}

