package com.wintereye;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Arrays;
import java.util.List;

public class Guide extends AppCompatActivity {

    private final List<String> GUIDE_TEXTS = Arrays.asList(
            "WinterEye helps you to recommend activities nearby based on the current location weather conditions..",
            "want to find out current weather information...",
            "...or find out what activity you can do this evening..",
            "You can tell us whether you enjoyed the activity...");

    private final int SWAP_TIME = 100;
    private final float BUTTON_DISTANCE_DP = 55;

    private static int stage = 1;

    private static int seenStage = 1;

    private TextView guide_text;
    private ImageButton current_button;
    private ImageButton ellipse_button1;
    private ImageButton ellipse_button2;
    private ImageButton ellipse_button3;
    private Button finish_button;
    private ConstraintLayout mainLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);

        stage = 1;

        findViewById(R.id.finish_button).setVisibility(View.INVISIBLE);

        // Open the access page
        finish_button = findViewById(R.id.finish_button);
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccess();
            }
        });

        mainLayout = findViewById(R.id.guide_main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disabledButtons();
                if (stage == 1) {
                    stage = 2;
                    swap(current_button, ellipse_button1, true);
                } else if (stage == 2) {
                    stage = 3;
                    swap(current_button, ellipse_button2, true);
                } else if (stage == 3) {
                    stage = 4;
                    swap(current_button, ellipse_button3, true);
                } else {
                    enabledButtons();
                }
                seenStage = Math.max(seenStage, stage);
                if (stage == 4) {
                    findViewById(R.id.finish_button).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.finish_button).setVisibility(View.INVISIBLE);
                }
                guide_text.setText(GUIDE_TEXTS.get(stage-1));
                setGuideImage();
            }
        });

        current_button = findViewById(R.id.current);

        current_button = findViewById(R.id.current);
        ellipse_button1 = findViewById(R.id.ellipse1);
        ellipse_button2 = findViewById(R.id.ellipse2);
        ellipse_button3 = findViewById(R.id.ellipse3);
        guide_text = findViewById(R.id.guide_text);

        Handler waitHandler = new Handler();

        ellipse_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabledButtons();
                if (stage == 1) {
                    stage = 2;
                    seenStage = Math.max(seenStage, stage);
                    swap(current_button, ellipse_button1, true);
                } else if (stage == 2) {
                    stage = 1;
                    swap(ellipse_button1, current_button, true);
                } else if (stage == 3) {
                    stage = 1;
                    swap(ellipse_button2, current_button, false);
                    waitHandler.postDelayed(new Runnable() {
                        public void run() {
                            swap(ellipse_button1, current_button, true);
                        }
                    }, SWAP_TIME);
                } else if (stage == 4) {
                    stage = 1;
                    swap(ellipse_button3, current_button, false);
                    waitHandler.postDelayed(new Runnable() {
                        public void run() {
                            swap(ellipse_button2, current_button, false);
                            Handler newWaitHandler = new Handler();
                            newWaitHandler.postDelayed(new Runnable() {
                                public void run() {
                                    swap(ellipse_button1, current_button, true);
                                }
                            }, SWAP_TIME);
                        }
                    }, SWAP_TIME);
                }
                if (stage == 4) {
                    findViewById(R.id.finish_button).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.finish_button).setVisibility(View.INVISIBLE);
                }
                guide_text.setText(GUIDE_TEXTS.get(stage-1));
                setGuideImage();
            }
        });

        ellipse_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabledButtons();
                if (stage == 1) {
                    if (seenStage >= 2) {
                        stage = 3;
                        swap(current_button, ellipse_button1, false);
                        waitHandler.postDelayed(new Runnable() {
                            public void run() {
                                swap(current_button, ellipse_button2, true);
                            }
                        }, SWAP_TIME);
                    } else {
                        mainLayout.performClick();
                    }
                } else if (stage == 2) {
                    stage = 3;
                    seenStage = Math.max(seenStage, 3);
                    swap(current_button, ellipse_button2, true);
                } else if (stage == 3) {
                    stage = 2;
                    swap(ellipse_button2, current_button, true);
                } else if (stage == 4) {
                    stage = 2;
                    swap(ellipse_button3, current_button, false);
                    waitHandler.postDelayed(new Runnable() {
                        public void run() {
                            swap(ellipse_button2, current_button, true);
                        }
                    }, SWAP_TIME);
                }
                if (stage == 4) {
                    findViewById(R.id.finish_button).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.finish_button).setVisibility(View.INVISIBLE);
                }
                guide_text.setText(GUIDE_TEXTS.get(stage-1));
                setGuideImage();
            }
        });

        ellipse_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabledButtons();
                if (stage == 1 && seenStage >= 3) {
                    stage = 4;
                    swap(current_button, ellipse_button1, false);
                    waitHandler.postDelayed(new Runnable() {
                        public void run() {
                            swap(current_button, ellipse_button2, false);
                            Handler newWaitHandler = new Handler();
                            newWaitHandler.postDelayed(new Runnable() {
                                public void run() {
                                    swap(current_button, ellipse_button3, true);
                                }
                            }, SWAP_TIME);
                        }
                    }, SWAP_TIME);
                } else if (stage == 2 && seenStage >= 3) {
                    stage = 4;
                    swap(current_button, ellipse_button2, false);
                    Handler newWaitHandler = new Handler();
                    newWaitHandler.postDelayed(new Runnable() {
                        public void run() {
                            swap(current_button, ellipse_button3, true);
                        }
                    }, SWAP_TIME);
                } else if (seenStage < 3) {
                    mainLayout.performClick();
                } else if (stage == 3) {
                    seenStage = 4;
                    stage = 4;
                    swap(current_button, ellipse_button3, true);
                } else if (stage == 4) {
                    stage = 3;
                    swap(ellipse_button3, current_button, true);
                }
                if (stage == 4) {
                    findViewById(R.id.finish_button).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.finish_button).setVisibility(View.INVISIBLE);
                }
                guide_text.setText(GUIDE_TEXTS.get(stage-1));
                setGuideImage();
            }
        });
    }
    public void openAccess() {
        startActivity(new Intent(this,Access.class));
    }
    private void setGuideImage() {
        String uri = "drawable/guide_image_" + this.stage;

        ImageView guideImage = findViewById(R.id.guide_image);

        if (getScreenHeight() < 1440 && getScreenHeight() < getScreenWidth() && (this.stage == 2 || this.stage == 3)) {
            int padding_px = Math.round(convertDpToPixel(40, guideImage.getContext()));
            guideImage.setPadding(padding_px, padding_px, padding_px, padding_px);
            setMargins(guideImage, 0, Math.round(convertDpToPixel(-50, guideImage.getContext())), 0, 0);
        } else {
            guideImage.setPadding(0, 0, 0, 0);
            setMargins(guideImage, 0, 0, 0, 0);
        }

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // >= API 21
            guideImage.setImageDrawable(getResources().getDrawable(imageResource, getApplicationContext().getTheme()));
        } else {
            guideImage.setImageDrawable(getResources().getDrawable(imageResource));
        }
    }
    private void swap(ImageButton leftBtn, ImageButton rightBtn, boolean isLastAnimation) {

        float leftBtnDestination = leftBtn.getTranslationX() + convertDpToPixel(BUTTON_DISTANCE_DP, leftBtn.getContext());
        ObjectAnimator toRightAnimation = ObjectAnimator.ofFloat(leftBtn, "translationX", leftBtnDestination);
        toRightAnimation.setDuration(SWAP_TIME);

        float RightBtnDestination = rightBtn.getTranslationX() - convertDpToPixel(BUTTON_DISTANCE_DP, rightBtn.getContext());
        ObjectAnimator toLeftAnimation = ObjectAnimator.ofFloat(rightBtn, "translationX", RightBtnDestination);
        toLeftAnimation.setDuration(SWAP_TIME);

        if (isLastAnimation) {
            toRightAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {
                }
                @Override
                public void onAnimationEnd(Animator animator) {
                    enabledButtons();
                }
                @Override
                public void onAnimationCancel(@NonNull Animator animator) {
                }
                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {
                }
            });
        }

        toRightAnimation.start();
        toLeftAnimation.start();
    }
    private void disabledButtons() {
        ellipse_button1.setEnabled(false);
        ellipse_button2.setEnabled(false);
        ellipse_button3.setEnabled(false);
        mainLayout.setEnabled(false);
    }
    private void enabledButtons() {
        ellipse_button1.setEnabled(true);
        ellipse_button2.setEnabled(true);
        ellipse_button3.setEnabled(true);
        mainLayout.setEnabled(true);
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    public float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
