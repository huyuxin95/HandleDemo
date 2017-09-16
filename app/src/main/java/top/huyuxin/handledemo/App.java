package top.huyuxin.handledemo;

import android.app.Application;
import android.app.PendingIntent;

import cn.vszone.gamepad.ConfigBuilder;
import cn.vszone.gamepad.GamePadManager;
import cn.vszone.tv.gamebox.BuildConfig;


/**
 * @author huyuxin@kobox.tv
 * @version 1.0
 *          <p><strong>Features draft description.主要功能介绍</strong></p>
 * @since 2017/9/15 9:57
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ConfigBuilder mConfigBuilder = new ConfigBuilder();
        mConfigBuilder.configLog(BuildConfig.DEBUG);
        mConfigBuilder.configVirtualGamePadExtand();
        GamePadManager.config(mConfigBuilder);
    }





}
