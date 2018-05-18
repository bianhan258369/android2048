package com.game.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.game.Model.Card;
import com.game.R;
import com.game.Utils.AnimLayer;
import com.game.Utils.GameView;
import com.game.Utils.ToastUtil;

public class MainFragment extends Fragment {

    private int score = 0;
    private TextView tvScore, tvBestScore;
    private LinearLayout root = null;
    private GameView gameView;
    private AnimLayer animLayer = null;
    public static final String SP_KEY_BEST_SCORE = "bestScore";
    public Tool tool;
    private View rootView;
    private String curTool = "";

    public MainFragment setNeedProp(boolean needProp) {
        this.needProp = needProp;
        return this;
    }

    private boolean needProp;


    public static MainFragment mainFragment;
    private boolean showBorder = false;

    public MainFragment() {
        mainFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //首先将布局放进来 因为是fragment  所以特殊一点  这么放
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //然后获取一个容器  放中间的gameview  因为开源库的原因更改成fragment
        root = (LinearLayout) rootView.findViewById(R.id.container);

        //设置颜色
        root.setBackgroundColor(0xfffaf8ef);

        //初始化控件
        tvScore = (TextView) rootView.findViewById(R.id.tvScore);
        tvBestScore = (TextView) rootView.findViewById(R.id.tvBestScore);

        gameView = ((GameView) rootView.findViewById(R.id.gameView)).setMainFragment(this);

        animLayer = (AnimLayer) rootView.findViewById(R.id.animLayer);

        if (needProp) {
            tool = new Tool((ImageButton) rootView.findViewById(R.id.button_doubleNumber),
                    (ImageButton) rootView.findViewById(R.id.button_removeNumber),
                    (ImageButton) rootView.findViewById(R.id.button_makeChaos),
                    (TextView) rootView.findViewById(R.id.num_doubleNumber),
                    (TextView) rootView.findViewById(R.id.num_removeNumber),
                    (TextView) rootView.findViewById(R.id.num_makeChaos));
            rootView.findViewById(R.id.tools).setVisibility(View.VISIBLE);
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
        switch (curTool) {
            case "Double":
                builder.setTitle("确认信息").setMessage("确定将当前选中数字翻倍吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                card.afterSelect();
                                card.setNum(card.getNum() * 2);
                                showBorder = false;
                                tool.useTool("dN");
                            }
                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        card.afterSelect();
                        dialogInterface.dismiss();
                    }
                }).show();
                break;
            case "Remove":
                builder.setTitle("确认信息").setMessage("确定移除当前数字吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                card.afterSelect();
                                card.setNum(0);
                                showBorder = false;
                                tool.useTool("rN");
                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                card.afterSelect();
                                dialogInterface.dismiss();
                            }
                        }).show();
                break;
            default:
                break;
        }
    }

    public void reset() {
        tool.reset();
    }

    class Tool {
        ImageButton dN, rN, mC;
        TextView dN_, rN_, mC_;
        int dN_num = 3, rN_num = 3, mC_num = 3;
        Drawable enabled, disabled;

        public Tool(ImageButton dN, ImageButton rN, ImageButton mC, TextView dN_, TextView rN_, TextView mC_) {
            this.dN = dN;
            this.rN = rN;
            this.mC = mC;
            this.dN_ = dN_;
            this.rN_ = rN_;
            this.mC_ = mC_;
            enabled = getResources().getDrawable(R.drawable.tips_circle);
            disabled = convertDrawableToGrayScale();
            init();
        }

        public void reset(){

        }

        public void useTool(String name){
            switch (name){
                case "dN":
                    dN_num--;
                    dN_.setText(dN_num + "");
                    if (dN_num == 0){
                        dN.setEnabled(false);
                        dN.setBackground(disabled);
                        ToastUtil.makeText(getActivity(), "翻倍道具已达使用上限！", Toast.LENGTH_SHORT);
                    }
                    break;
                case "rN":
                    rN_num--;
                    rN_.setText(rN_num + "");
                    if (rN_num == 0){
                        rN.setEnabled(false);
                        rN.setBackground(disabled);
                        ToastUtil.makeText(getActivity(), "删除道具已达使用上限！", Toast.LENGTH_SHORT);
                    }
                    break;
                case "mC":
                    mC_num--;
                    mC_.setText(mC_num + "");
                    if (mC_num == 0){
                        mC.setEnabled(false);
                        mC.setBackground(disabled);
                        ToastUtil.makeText(getActivity(), "翻倍道具已达使用上限！", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }

        private void init() {
            dN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBorder = true;
                    curTool = "Double";
                }
            });

            rN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gameView.canRemove()){
                        showBorder = true;
                        curTool = "Remove";
                    }else {
                        ToastUtil.makeText(getActivity(), "至少要有两个数才能使用该道具!", Toast.LENGTH_SHORT);
                    }
                }
            });

            mC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameView.makeChaos();
                    useTool("mC");
                }
            });
        }

        private Drawable convertDrawableToGrayScale() {
            Drawable drawable = getResources().getDrawable(R.drawable.tips_circle);
            if (drawable == null)
                return null;

            Drawable res = drawable.mutate();
            res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            return res;
        }
    }

}
