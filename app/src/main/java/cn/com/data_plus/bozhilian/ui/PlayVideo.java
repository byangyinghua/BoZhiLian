package cn.com.data_plus.bozhilian.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.global.PlayPic;
import cn.com.data_plus.bozhilian.util.LogUtil;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class PlayVideo {
    private static PlayVideo playVideo;
    public static PlayVideo getInstance() {
        if (playVideo == null) {
            playVideo = new PlayVideo();
        }
        return playVideo;
    }
    Dialog dia;
    void PlayVideos(final Activity activity, String url){
        dia = new Dialog(App.context, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.activity_start_dialog);
        dia.setCanceledOnTouchOutside(false); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        dia.onWindowAttributesChanged(lp);
        dia.show();
//        player.setFullScreenOnly(false);
//        player.playInFullScreen(true);
//        player.setPlayerStateListener(this);
//        LogUtil.debug("url:" + url);
//        player.play(url);
    }
}
