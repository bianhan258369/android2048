package com.game.Model;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.game.Activity.MainActivity;
import com.game.Activity.MainFragment;
import com.game.R;

public class Card extends FrameLayout {

    private ColorDrawable curBackground;
    private MainFragment mainFragment;
    private TextView label;
    private int num = 0;
    private GestureDetector gestureDetector = new GestureDetector(getContext(), (GestureDetector.OnGestureListener) new DoubleTap(this));

    public void afterSelect() {
        label.setBackground(curBackground);
    }

    private class DoubleTap implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
        Card card;

        DoubleTap(Card cur){
            card = cur;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (mainFragment.getShowBorder() && getNum() > 0){
                curBackground = (ColorDrawable)label.getBackground();
                GradientDrawable selected_background = (GradientDrawable) getResources().getDrawable(R.drawable.selected_border);
                selected_background.setColor(curBackground.getColor());
                label.setBackground(selected_background);
                mainFragment.askConfirm(card);
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return mainFragment.getShowBorder();
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public Card(Context context) {
        super(context);
        mainFragment = (MainFragment) ((MainActivity) context).getSupportFragmentManager().getFragments().get(0);
        LayoutParams lp = null;

        View background = new View(getContext());
        lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);
        background.setBackgroundColor(getResources().getColor(
                R.color.normalCardBack));
        addView(background, lp);

        label = new TextView(getContext());
        label.setTextSize(28);
        label.setGravity(Gravity.CENTER);

        TextPaint tp = label.getPaint();
        tp.setFakeBoldText(true);

        lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);
        addView(label, lp);

        setNum(0);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        if (num <= 0) {
            label.setText("");
        } else {
            label.setText(num + "");
        }
        switch (num) {
            case 0:
                label.setBackgroundResource(R.color.normalCardBack);
                break;
            case 2:
                label.setTextColor(getResources().getColor(R.color._2Font));
                label.setBackgroundResource(R.color._2Back);
                break;
            case 4:
                label.setTextColor(getResources().getColor(R.color._4Font));
                label.setBackgroundResource(R.color._4Back);
                break;
            case 8:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._8Back);
                break;
            case 16:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._16Back);
                break;
            case 32:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._32Back);
                break;
            case 64:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._64Back);
                break;
            case 128:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._128Back);
                break;
            case 256:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._256Back);
                break;
            case 512:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._512Back);
                break;
            case 1024:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._1024Back);
                break;
            case 2048:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._2048Back);
                break;
            default:
                label.setTextColor(getResources().getColor(R.color._2Font));
                label.setBackgroundResource(R.color._2Back);
                break;
        }
    }
    public boolean equals(Card o) {
        return getNum() == o.getNum();
    }

    protected Card clone() {
        Card c = new Card(getContext());
        c.setNum(getNum());
        return c;
    }

    public TextView getLabel() {
        return label;
    }
}
