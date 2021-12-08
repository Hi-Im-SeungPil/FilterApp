package org.jeonfeel.jellybus2;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class CustomLoadingDialog extends Dialog {

    Animation animation;
    ImageView iv_loading;

    public CustomLoadingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.custom_loadiong_dialog);

        animation = AnimationUtils.loadAnimation(context,R.anim.img_moldiv_icon_anim);
        iv_loading = findViewById(R.id.iv_loading);
        iv_loading.startAnimation(animation);

    }

}
