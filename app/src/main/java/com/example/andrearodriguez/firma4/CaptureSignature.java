package com.example.andrearodriguez.firma4;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by andrearodriguez on 11/9/15.
 */
public class CaptureSignature extends Activity {

//    Declaro variables

    signature mSignature;
    Paint paint;
    LinearLayout liLa_mContent;
    Button btn_clear, btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capturesignature);

//        Declaro boton salvar en estado deshabilitado.
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setEnabled(false);
        btn_clear = (Button) findViewById(R.id.btn_clear);

//        Declaro Layaut donde se va a gurdar la firma
        liLa_mContent = (LinearLayout) findViewById(R.id.mysignature);

        mSignature = new signature(this, null);
        liLa_mContent.addView(mSignature);
//        liLa_mContent.setBackgroundColor(14737632);

        btn_save.setOnClickListener(onButtonClick);
        btn_clear.setOnClickListener(onButtonClick);
    }

//    Llamo las acciones de borrar y guardar
    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == btn_clear) {
                mSignature.clear();
            } else if (v == btn_save) {
                mSignature.save();
            }
        }
    };


//    Clase que da el grosor y el color a la firma
    public class signature extends View {

//    Grosor de la linea de la firma es 4f

        static final float STROKE_WIDTH = 4f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();

        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();


//    Atributos de la firma, y caracteristicas del pincel
            public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

//    Borro lo que este en el path y deshabilito el boton salvar
        public void clear() {
            path.reset();
            invalidate();
            btn_save.setEnabled(false);
        }
//      Salvo la forma
        public void save() {
//            Paso lo que esta en el Linealayaout a un bitmao
            Bitmap returnedBitmap = Bitmap.createBitmap(liLa_mContent.getWidth(),
                    liLa_mContent.getHeight(), Bitmap.Config.ARGB_8888);

//            Gurado lo que esta en el lineallayout en un archivo canvas
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = liLa_mContent.getBackground();


            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else {

//                FileOutputStream bitMapReSize = openFileOutput("Firmas1.png", Context.MODE_PRIVATE);
//                Para salvar en un lugar donde el cliente no tenga acceso

//                Se almacena el bitmap en una variable outputstream
                OutputStream bitMapReSize;

//                Se crea la ruta para gurdar el archivo
                File filepath= Environment.getExternalStorageDirectory();
//                Crea carpeta Save Firmas en el celular
                File dir = new File (filepath.getAbsolutePath() + "/Save Firmas");
                dir.mkdirs();

//                Se da un nombre al archivo que se va a gurdar
                File file = new File (dir, "Firma10.jpeg");
//                Mensaje de que se gusro don exito la firma
                Toast.makeText(CaptureSignature.this, "Firma Salvada en SD", Toast.LENGTH_SHORT).show();
                try {
//                  Se gurda el archico en la ruta determinada arriba
                    bitMapReSize = new FileOutputStream(file);
//                    Se da un color de fondo a la imagen de la firma
                    canvas.drawColor(Color.rgb(216,222,222));
//                    Se dibuja lo que esta en el canvas
                    liLa_mContent.draw(canvas);
//                  Se almacena en un bitearray el bitmap
                    ByteArrayOutputStream bitMostrar = new ByteArrayOutputStream();
//                    Se comprime la imagen y se disminuye al maximo la calidad para que el archvivo qued epequeño
                    returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 1, bitMapReSize);
                    returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 2, bitMostrar);
//                    Se muestra la firma gurdada
                Intent intent = new Intent();
                intent.putExtra("byteArray", bitMostrar.toByteArray());
                setResult(1, intent);
                finish();
//                    Se termina el proceso de gurdado y visualizacion
                    bitMapReSize.flush();
                    bitMapReSize.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
//            Dibuja la firma
            canvas.drawPath(path, paint);
        }
//La clase que gurda los movimientos de la firma
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            btn_save.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }


    }
}