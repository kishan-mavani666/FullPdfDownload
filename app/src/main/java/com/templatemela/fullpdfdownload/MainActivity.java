package com.templatemela.fullpdfdownload;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takeScreenShot();
    }

    private void takeScreenShot() {
        NestedScrollView scrollView = findViewById(R.id.scrollview);
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = scrollView.getMeasuredWidth();
                int height = scrollView.getChildAt(0).getMeasuredHeight();
                ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                params.height = height;
                params.width = width;
                Log.e("rytrsyrtdydr", "height==" + height);
                Log.e("rytrsyrtdydr", "width==" + width);
                saveImageToPDF(scrollView, getBitmapFromView(scrollView, height, width));
            }
        });

    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    /*save image to pdf*/
    public void saveImageToPDF(View title, Bitmap bitmap) {
        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File path = new File(externalStorageDirectory.getAbsolutePath() + File.separator + getString(R.string.app_name));
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            File mFile = new File(path + "/" + String.format("%d.pdf", new Object[]{Long.valueOf(System.currentTimeMillis())}));
            if (!mFile.exists()) {
                int height = bitmap.getHeight();
                Log.e("Height", "$" + bitmap.getHeight());
                Log.e("Height", "===$" + bitmap.getWidth());
                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), height, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                title.draw(canvas);
                canvas.drawBitmap(bitmap, null, new Rect(0, bitmap.getHeight() + 100, bitmap.getWidth(), bitmap.getHeight() + 100), null);
                document.finishPage(page);
                try {
                    mFile.createNewFile();
                    OutputStream out = new FileOutputStream(mFile);
                    document.writeTo(out);
                    document.close();
                    out.close();
                    Toast.makeText(this, "Pdf Saved at:" + mFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}