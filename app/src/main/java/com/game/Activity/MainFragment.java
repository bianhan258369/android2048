package com.game.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private String curTool = "";
    private FloatingActionButton fab_quit_tool, fab_show_tools;
    private LinearLayout tools_layout;

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

        gameView = ((GameView) rootView.findViewById(R.id.gameView)).setMainFragment(this);

        animLayer = (AnimLayer) rootView.findViewById(R.id.animLayer);


        tools_layout = (LinearLayout) rootView.findViewById(R.id.tools);

        fab_quit_tool = (FloatingActionButton) rootView.findViewById(R.id.fab_quick_tool);

        fab_show_tools = (FloatingActionButton) rootView.findViewById(R.id.fab_tools);

        fab_quit_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBorder = false;
                tools_layout.setVisibility(View.GONE);
                fab_show_tools.setVisibility(View.VISIBLE);
            }
        });

        fab_show_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_show_tools.setVisibility(View.GONE);
                tools_layout.setVisibility(View.VISIBLE);
            }
        });

        timer = (TextView) rootView.findViewById(R.id.timer);
        timer.setVisibility(View.INVISIBLE);

        if (mode.equals("Prop")) {
            tool = new Tool((FloatingActionButton) rootView.findViewById(R.id.button_doubleNumber),
                    (FloatingActionButton) rootView.findViewById(R.id.button_removeNumber),
                    (FloatingActionButton) rootView.findViewById(R.id.button_makeChaos),
                    (TextView) rootView.findViewById(R.id.num_doubleNumber),
                    (TextView) rootView.findViewById(R.id.num_removeNumber),
                    (TextView) rootView.findViewById(R.id.num_makeChaos));
            fab_show_tools.setVisibility(View.VISIBLE);
        } else if (mode.equals("Timer")) {
            timer.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(180000, 1000) {
                @Override
                public void onTick(long l) {
                    long timeLeft = l / 1000;
                    String minute = String.valueOf(timeLeft / 60);
                    String second = String.valueOf(timeLeft % 60);
                    timer.setText(minute + " : " + second);
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
        tvScore.setText(String.valueOf(score));
    }

    public void startGame() {
        gameView.startGame();
        if (mode.equals("Timer")) countDownTimer.start();
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
        tvBestScore.setText(String.valueOf(s));
    }

    public AnimLayer getAnimLayer() {
        return animLayer;
    }

    public int getScore() {
        return score;
    }

    public void reset() {
        if (mode.equals("Prop"))
            tool.reset();
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

    public void setMode(String mode) {
        this.mode = mode;
    }

    class Tool {
        boolean dN_first = true, rN_first = true, mC_first = true;
        FloatingActionButton dN, rN, mC;
        TextView dN_, rN_, mC_;
        int dN_num = 3, rN_num = 3, mC_num = 3;

        public Tool(FloatingActionButton dN, FloatingActionButton rN, FloatingActionButton mC, TextView dN_, TextView rN_, TextView mC_) {
            this.dN = dN;
            this.rN = rN;
            this.mC = mC;
            this.dN_ = dN_;
            this.rN_ = rN_;
            this.mC_ = mC_;
            dN_first = rN_first = mC_first = true;
            init();
        }

        void reset() {
            dN.setEnabled(true);
            dN.setAlpha(1f);
            rN.setEnabled(true);
            rN.setAlpha(1f);
            mC.setEnabled(true);
            mC.setAlpha(1f);
            dN_num = rN_num = mC_num = 3;
            dN_.setText(String.valueOf(dN_num));
            rN_.setText(String.valueOf(rN_num));
            mC_.setText(String.valueOf(mC_num));
            dN_first = rN_first = mC_first = true;
        }

        public void useTool(String name) {
            tools_layout.setVisibility(View.GONE);
            fab_show_tools.setVisibility(View.VISIBLE);
            switch (name) {
                case "dN":
                    dN_num--;
                    dN_.setText(String.valueOf(dN_num));
                    if (dN_num == 0) {
                        dN.setEnabled(false);
                        dN.setAlpha(0.5f);
                        ToastUtil.makeText(getActivity(), "翻倍道具已达使用上限！", Toast.LENGTH_SHORT);
                    }
                    break;
                case "rN":
                    rN_num--;
                    rN_.setText(String.valueOf(rN_num));
                    if (rN_num == 0) {
                        rN.setEnabled(false);
                        rN.setAlpha(0.5f);
                        ToastUtil.makeText(getActivity(), "删除道具已达使用上限！", Toast.LENGTH_SHORT);
                    }
                    break;
                case "mC":
                    mC_num--;
                    mC_.setText(String.valueOf(mC_num));
                    if (mC_num == 0) {
                        mC.setEnabled(false);
                        mC.setAlpha(0.5f);
                        ToastUtil.makeText(getActivity(), "翻倍道具已达使用上限！", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }

        private void init() {
            dN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dN_first) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("道具信息")
                                .setMessage("使用该道具可将选定位置数字翻倍。点击最右侧返回按钮退出道具使用状态。")
                                .setPositiveButton("我明白了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        dN_first = false;
                    }
                    showBorder = true;
                    curTool = "Double";
                }
            });

            rN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rN_first) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("道具信息")
                                .setMessage("使用该道具可移除选定位置的数字。点击最右侧返回按钮退出道具使用状态。")
                                .setPositiveButton("我明白了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        rN_first = false;
                    }
                    if (gameView.canRemove()) {
                        showBorder = true;
                        curTool = "Remove";
                    } else {
                        ToastUtil.makeText(getActivity(), "至少要有两个数才能使用该道具!", Toast.LENGTH_SHORT);
                    }

                }
            });

            mC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mC_first) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("道具信息")
                                .setMessage("使用该道具将随机打乱当前所有数字。点击最右侧返回按钮退出道具使用状态。")
                                .setPositiveButton("我明白了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        gameView.makeChaos();
                                        useTool("mC");
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        mC_first = false;
                    } else {
                        gameView.makeChaos();
                        useTool("mC");
                    }
                }
            });
        }
    }
}
