package com.yzhao12.artslots;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.util.Random;
import android.view.WindowManager;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setTransitions();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        myGestureDetector m = new myGestureDetector();
        detector = new GestureDetector(this, m);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void spin(View v){
        final ImageButton go = (ImageButton) findViewById(R.id.go);
        go.setEnabled(false);

        if (numCoins < 5) {
            Toast.makeText(this, "Not Enough Coins", Toast.LENGTH_SHORT).show();
            return;
        }

        numCoins -= 5;
        String coins = getString(R.string.coins) + Integer.toString(numCoins);
        coinDisplay.setText(coins);

        flip1.setDisplayedChild(rand.nextInt(6));
        flip2.setDisplayedChild(rand.nextInt(6));
        flip3.setDisplayedChild(rand.nextInt(6));

        flip1.startFlipping();
        flip2.startFlipping();
        flip3.startFlipping();

        int oneStop = (int) ((2 + rand.nextDouble()) * 1000);
        int twoStop = (int) ((3 + rand.nextDouble()) * 1000);
        int threeStop = (int) ((4 + rand.nextDouble()) * 1000);

        handler.postDelayed(makeStop(flip1), oneStop);
        handler.postDelayed(makeStop(flip2), twoStop);
        handler.postDelayed(makeStop(flip3), threeStop);

        Runnable updateCoins = new Runnable() {
            @Override
            public void run() {
                calcCoins();
                go.setEnabled(true);
            }
        };

        handler.postDelayed(updateCoins, threeStop + 1);
    }

    public void calcCoins(){
        if (flip1.getDisplayedChild() == flip2.getDisplayedChild() && flip2.getDisplayedChild() == flip3.getDisplayedChild()){
            numCoins += winValues[flip1.getDisplayedChild()];
            Animation fall = AnimationUtils.loadAnimation(this, R.anim.falldown);
            winToast.setAnimation(fall);
            fall.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    all.removeView(winToast);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            all.addView(winToast);
            fall.start();
        }

        String coins = getString(R.string.coins) + Integer.toString(numCoins);
        coinDisplay.setText(coins);
    }

    public Runnable makeStop(final ViewFlipper flipper) {
        Runnable stopFlip = new Runnable() {
            @Override
            public void run() {
                flipper.stopFlipping();
            }
        };
        return stopFlip;
    }


    public void setTransitions() {
        flip1.setInAnimation(this, R.anim.infromtop);
        flip2.setInAnimation(this, R.anim.infromtop);
        flip3.setInAnimation(this, R.anim.infromtop);

        flip1.setOutAnimation(this, R.anim.outtobot);
        flip2.setOutAnimation(this, R.anim.outtobot);
        flip3.setOutAnimation(this, R.anim.outtobot);
    }


    public void init() {
        flip1 = (ViewFlipper)findViewById(R.id.flip1);
        flip2 = (ViewFlipper)findViewById(R.id.flip2);
        flip3 = (ViewFlipper)findViewById(R.id.flip3);

        coinDisplay = (TextView)findViewById(R.id.coins);
        String coins = getString(R.string.coins) + Integer.toString(numCoins);
        coinDisplay.setText(coins);

        all = (RelativeLayout)findViewById(R.id.all);

        winToast = new ImageView(this);
        winToast.setImageDrawable(getResources().getDrawable(R.drawable.confetti1));
        winToast.setX(0);
        winToast.setY(0);

        values = new ImageView(this);
//        values.setImageDrawable(getResources().getDrawable());
    }



    private int numCoins = 100;
    private int[] winValues = {2, 5, 10, 25, 15, 20};

    private RelativeLayout all;
    private ImageView winToast;
    private TextView coinDisplay;
    private ViewFlipper flip1;
    private ViewFlipper flip2;
    private ViewFlipper flip3;
    private ImageView values;
    private boolean spinning = false;

    private Random rand = new Random();
    private Handler handler = new Handler();
    private GestureDetector detector;


    class myGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getY() < e2.getY() && spinning == false) {
                Log.d(TAG, "Up to Down swipe performed");

                spin(findViewById(R.id.go));
            }

            if (e1.getY() > e2.getY()) {
                Log.d(TAG, "Down to Up swipe performed");

            }

            return true;
        }
    }
}
