package com.example.mini.game.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.mini.game.R;

import java.io.InputStream;

/**
 * Created by Kuba on 10/01/2015.
 */
public class GIFView extends View {

    private Movie movie;
    private long movieStart;

    public GIFView(Context context) {
        super(context);
        initializeView();
    }

    private void initializeView() {
        //InputStream is = getContext().getResources().openRawResource(R.raw.loading_screen);
        InputStream is = getContext().getResources().openRawResource(R.raw.gaben_loading);
        movie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (movie != null) {
            int relTime = (int) ((now - movieStart) % movie.duration());
            movie.setTime(relTime);

            float scaleWidth = (float) ((getWidth() / (1f*movie.width())));//add 1f does the trick
            float scaleHeight = (float) ((getHeight() / (1f*movie.height())));

            canvas.scale(scaleWidth, scaleHeight);

            movie.draw(canvas, 0, 0);

            this.invalidate();
        }
    }
}
