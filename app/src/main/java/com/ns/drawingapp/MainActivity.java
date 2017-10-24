package com.ns.drawingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AlteredCharSequence;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawingView;
    private ImageButton ibCurrent, ibDraw, ibErase, ibNew, ibSave, ibFamily, ibSmallBrush, ibMediumBrush, ibLargeBrush;
    private LinearLayout llColors;
    private float smallBrush, mediumBrush, largerBrush;
    private Dialog brushDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWidget();
        setEvent();

    }

    private void setEvent() {
        ibDraw.setOnClickListener(this);
        ibErase.setOnClickListener(this);
        ibNew.setOnClickListener(this);
        ibSave.setOnClickListener(this);
        ibFamily.setOnClickListener(this);

    }

    private void getWidget() {
        drawingView = (DrawingView) findViewById(R.id.vDrawing);
        llColors = (LinearLayout) findViewById(R.id.llColors);
        ibCurrent = (ImageButton) llColors.getChildAt(0);
        ibCurrent.setImageDrawable(getResources().getDrawable(R.drawable.pain_selected));
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largerBrush = getResources().getInteger(R.integer.large_size);
        ibDraw = (ImageButton) findViewById(R.id.bntBrush);
        ibErase = (ImageButton) findViewById(R.id.bntErase);
        ibNew = (ImageButton) findViewById(R.id.bntNew);
        ibSave = (ImageButton) findViewById(R.id.bntSave);
        ibFamily = (ImageButton) findViewById(R.id.btnFamily);

    }

    public void paintClicked(View view){
        //use chosen color
        //check if color is different current color
        if(view != ibCurrent){
            ImageButton ibSelected = (ImageButton) view;
            String selectedColor = ibSelected.getTag().toString();
            drawingView.setErase(false);
            drawingView.setBrushSize(drawingView.getLastBrushSize());
            drawingView.setColor(selectedColor);
            ibSelected.setImageDrawable(getResources().getDrawable(R.drawable.pain_selected));
            ibCurrent.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            ibCurrent = ibSelected;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.bntBrush:
                setDialog(0);
                break;
            case R.id.bntErase:
                setDialog(1);
                break;
            case R.id.bntNew:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawingView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;
            case R.id.bntSave:
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save Drawing");
                saveDialog.setMessage("Do you want to save this drawing?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawingView.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawingView.getDrawingCache(),
                                UUID.randomUUID().toString()+".png", "drawing");
                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        drawingView.destroyDrawingCache();
                        dialog.dismiss();
                    }
                });
                saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;
            case R.id.btnFamily:
                int i = new Random().nextInt(3);
                Bitmap bitmap = null;
                if( i == 1)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.supo);
                else if(i == 2)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.su);
                else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_susu);
                drawingView.drawImage(bitmap);
                break;
            default:
                break;
        }
    }

    private void setDialog(int kind) {
        brushDialog = new Dialog(this);
        brushDialog.setContentView(R.layout.brush_chooser);
        ibSmallBrush = (ImageButton) brushDialog.findViewById(R.id.ibSmallBrush);
        ibMediumBrush = (ImageButton)brushDialog.findViewById(R.id.ibmediumBrush);
        ibLargeBrush = (ImageButton) brushDialog.findViewById(R.id.iblargerBrush);
        if(kind == 0){
            brushDialog.setTitle("Brush Size:");
            ibSmallBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(false);
                    drawingView.setBrushSize(smallBrush);
                    drawingView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ibMediumBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(false);
                    drawingView.setBrushSize(mediumBrush);
                    drawingView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ibLargeBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(false);
                    drawingView.setBrushSize(largerBrush);
                    drawingView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
        }
        else if(kind == 1){
            brushDialog.setTitle("erase Size:");
            ibSmallBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(true);
                    drawingView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ibMediumBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(true);
                    drawingView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ibLargeBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(true);
                    drawingView.setBrushSize(largerBrush);
                    brushDialog.dismiss();
                }
            });
        }
        brushDialog.show();
    }
}
