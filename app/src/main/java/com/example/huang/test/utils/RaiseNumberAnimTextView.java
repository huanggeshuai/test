package com.example.huang.test.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by huang on 2018/4/23.
 */

public class RaiseNumberAnimTextView extends android.support.v7.widget.AppCompatTextView {
    private long mDuration = 1000; // 动画持续时间 ms，默认1s

    private ValueAnimator animator;

    private TimeInterpolator mTimeInterpolator = new LinearInterpolator(); // 动画速率

    private AnimEndListener mEndListener; // 动画正常结束监听事件

    public RaiseNumberAnimTextView(Context context) {
        super(context);
    }

    public RaiseNumberAnimTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RaiseNumberAnimTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置动画持续时间，默认为1s。需要在setNumberWithAnim之前设置才有效
     * @param duration
     */
    public void setDuration(long duration) {
        if (duration > 0) {
            mDuration = duration;
        }
    }

    /**
     * 设置动画速率，默认为LinearInterpolator。需要在setNumberWithAnim之前设置才有效
     * @param timeInterpolator
     */
    public void setAnimInterpolator(TimeInterpolator timeInterpolator) {
        mTimeInterpolator = timeInterpolator;
    }

    /**
     * 设置要显示的float数字，带动画显示
     * @param number
     */
    public void setNumberWithAnim(float number) {
        clearAnimator();

        // 设置动画，float值的起始值
        animator = ValueAnimator.ofFloat(0.0f, number);

        startAnimator();
    }

    /**
     * 设置要显示的int数字，带动画显示。
     * @param number
     */
    public void setNumberWithAnim(int number) {
        clearAnimator();

        // 设置动画，int值的起始值
        animator = ValueAnimator.ofInt(0, number);

        startAnimator();
    }

    // 清除动画
    public void clearAnimator() {
        if (null != animator) {
            if (animator.isRunning()) {
                animator.removeAllListeners();
                animator.removeAllUpdateListeners();
                animator.cancel();
            }
            animator = null;
        }
    }

    // 暂停动画
    public void onPause() {
        if (null != animator && animator.isRunning()) {
            animator.pause(); // API 不低于19
        }
    }

    // 继续执行动画
    public void onResume() {
        if (null != animator && animator.isRunning()) {
            animator.resume();
        }
    }

    // 设置时常与过程处理，启动动画
    private void startAnimator() {
        if (null != animator) {
            animator.setDuration(mDuration);

            animator.setInterpolator(mTimeInterpolator);

            // 动画过程中获取当前值，显示
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setText(valueAnimator.getAnimatedValue().toString());
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (null != mEndListener) { // 动画不是中途取消，而是正常结束
                        mEndListener.onAnimFinish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            animator.start();
        }
    }

    public void setAnimEndListener(AnimEndListener listener) {
        mEndListener = listener;
    }

    // 动画显示数字的结束监听，当动画结束显示正确的数字时，可能需要做些处理
    public interface AnimEndListener {
        void onAnimFinish();
    }
}