package com.game.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.Model.Card;
import com.game.R;
import com.game.Utils.AnimLayer;
import com.game.Utils.GameView;

public class MainFragment extends Fragment {

    private int score = 0;
    private TextView tvScore, tvBestScore;
    private LinearLayout root = null;
    private GameView gameView;
    private AnimLayer animLayer = null;
    public static final String SP_KEY_BEST_SCORE = "bestScore";
    public Tool tool;
    public CountDownTimer countDownTimer;
    public TextView timer;
    String mode;


    public static MainFragment mainFragment;
    private boolean showBorder = false;

    public MainFragment() {
        mainFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //首先将布局放进来 因为是fragment  所以特殊一点  这么放
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //然后获取一个容器  放中间的gameview  因为开源库的原因更改成fragment
        root = (LinearLayout) rootView.findViewById(R.id.container);

        //设置颜色
        root.setBackgroundColor(0xfffaf8ef);

        //初始化控件
        tvScore = (TextView) rootView.findViewById(R.id.tvScore);
        tvBestScore = (TextView) rootView.findViewById(R.id.tvBestScore);

        gameView = (GameView) rootView.findViewById(R.id.gameView);

        animLayer = (AnimLayer) rootView.findViewById(R.id.animLayer);

        tool = new Tool((Button) rootView.findViewById(R.id.button_doubleNumber), (Button) rootView.findViewById(R.id.button_removeNumber), (Button) rootView.findViewById(R.id.button_makeChaos));

        timer = (TextView) rootView.findViewById(R.id.timer);
        timer.setVisibility(View.INVISIBLE);

        if(mode.equals("Timer")){
            timer.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(300000,1000) {
                @Override
                public void onTick(long l) {
                    timer.setText(String.valueOf(l / 1000));
                }

                @Override
                public void onFinish() {
                    gameView.finish();
                }
            };
            countDownTimer.start();
        }

        return rootView;
    }

    public static MainFragment getMainFragment() {
        return mainFragment;
    }

    public void clearScore() {
        score = 0;
        showScore();
    }

    public void showScore() {
        tvScore.setText(score + "");
    }

    public void startGame() {
        gameView.startGame();
        if(mode.equals("Timer")) countDownTimer.start();
    }

    public void addScore(int s) {
        score += s;
        showScore();
        int maxScore = Math.max(score, getBestScore());
        saveBestScore(maxScore);
        showBestScore(maxScore);
    }

    public void saveBestScore(int s) {

        // 获取  偏好编辑器
        SharedPreferences.Editor e = getActivity().getPreferences(getActivity().MODE_PRIVATE).edit();

        //往编辑器中放东西
        e.putInt(SP_KEY_BEST_SCORE, s);

        //提交
        e.commit();
    }

    //获取最高分
    public int getBestScore() {
        return getActivity().getPreferences(getActivity().MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
    }

    public void showBestScore(int s) {
        tvBestScore.setText(s + "");
    }

    public AnimLayer getAnimLayer() {
        return animLayer;
    }

    public int getScore() {
        return score;
    }

    public boolean getShowBorder() {
        return showBorder;
    }

    public void askConfirm(final Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确认信息").setMessage("确定将当前各自中的数字翻倍吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        card.setNum(card.getNum() * 2);
                        card.afterSelect();
                        showBorder = false;
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                card.afterSelect();
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    class Tool {
        Button dN, rN, mC;

        public Tool(Button dN, Button rN, Button mC) {
            this.dN = dN;
            this.rN = rN;
            this.mC = mC;
            init();
        }

        private void init() {
            dN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBorder = true;
                }
            });

            rN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBorder = true;
                }
            });

            mC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

}
