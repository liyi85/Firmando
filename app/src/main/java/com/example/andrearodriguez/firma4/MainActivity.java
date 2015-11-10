package com.example.andrearodriguez.firma4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    Button btn_getSignature;
    ImageView signImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Declaro variables
        btn_getSignature = (Button) findViewById(R.id.btn_getSign);
        signImage = (ImageView) findViewById(R.id.iv_showSign);

        btn_getSignature.setOnClickListener(onButtonClick);

    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            Llamada a la clase de capturar la firma
            Intent i = new Intent(MainActivity.this, CaptureSignature.class);
            startActivityForResult(i, 0);


        }
    };

//    Imprimo la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == 1) {
            Bitmap b = BitmapFactory.decodeByteArray(
                    data.getByteArrayExtra("byteArray"), 0,
                    data.getByteArrayExtra("byteArray").length);
            signImage.setImageBitmap(b);
            Log.i("tamaño H", b.getHeight() + " x " + "tamaño W" + b.getHeight()+"");
        }
    }
}



