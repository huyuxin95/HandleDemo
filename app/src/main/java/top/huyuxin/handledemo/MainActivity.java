package top.huyuxin.handledemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import cn.vszone.gamepad.GamePadManager;
import cn.vszone.gamepad.OnPlayerListener;
import cn.vszone.gamepad.bean.Player;
import cn.vszone.gamepad.utils.InputDeviceUtils;
import cn.vszone.tv.gamebox.R;

public class MainActivity extends AppCompatActivity  {
    public  static  final  String TAG=MainActivity.class.getSimpleName();
    OnPlayerListenerImp mOnPlayerLilstener;
    DpadKeyEventLinster dpadKeyEventLinster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GamePadManager.getInstance(getApplicationContext());
         mOnPlayerLilstener=new OnPlayerListenerImp(this);
        dpadKeyEventLinster=new DpadKeyEventLinster();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GamePadManager.getInstance(getApplicationContext()).registOnPlayerListener(mOnPlayerLilstener);
        JoystickDevice.setOnDpadKeyListener(dpadKeyEventLinster);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在游戏模式下 当窗口可操作时 设备SDK模式为游戏模式,并注销事件监听
        GamePadManager.getInstance(getApplicationContext()).unregistOnPlayerListener(mOnPlayerLilstener);
        JoystickDevice.setOnDpadKeyListener(dpadKeyEventLinster);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GamePadManager.getInstance().switchContext(null);
        GamePadManager.getInstance().destory();
    }

    /**
     *事件分发的处理
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        //将事件分发的key传入手柄sdk     返回的值就是经过手柄SDK映射完成后的值
        Log.d(TAG, "dispatchGenericMotionEvent:" + ev.toString());
        ev = GamePadManager.getInstance(this).dispatchGenericMotionEvent(ev);
//        if (!InputDeviceUtils.isValueMotionEvent(ev)) {
//            return true;
//        }
        Log.d(TAG, "dispatchGenericMotionEvent:------映射完成后" + ev.toString());
        SparseArray<Float> map = new SparseArray<Float>();
        map.put(0, ev.getAxisValue(0));
        map.put(1, ev.getAxisValue(1));
        map.put(15, ev.getAxisValue(15));
        map.put(16, ev.getAxisValue(16));
        JoystickDevice.getInstance().handleMotionEvent(0, map);
        return super.dispatchGenericMotionEvent(ev);
    }

    /**
     *事件分发的处理
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //将事件分发的key传入手柄sdk
        Log.d(TAG, "dispatchKeyEvent:" + event.toString());
        event = GamePadManager.getInstance(this).dispatchKeyEvent(event);
        // 不向下分发无意义的事件
        if (!InputDeviceUtils.isValueKeyEvent(event)) {
            return true;
        }
        //利用反射将 KEYCODE_DPAD_CENTER  KEYCODE_ENTER 这两个键值,修改为KEYCODE_BUTTON_A达到适配的效果
        //KEYCODE_DPAD_CENTER  KEYCODE_ENTER为部分遥控器的OK键,修改为手柄的KEYCODE_BUTTON_A达到确定的效果
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            Reflect.on(event).set("mKeyCode", KeyEvent.KEYCODE_BUTTON_A);
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Reflect.on(event).set("mKeyCode", KeyEvent.KEYCODE_BUTTON_A);
        }
        Log.d(TAG, "dispatchKeyEvent:------映射完成后" + event.toString());

        JoystickDevice.getInstance().handleKeyEvent(0,event);
        return super.dispatchKeyEvent(event);
    }



    class OnPlayerListenerImp implements OnPlayerListener {
        WeakReference<Context> wrf;

        public OnPlayerListenerImp(Context context) {
            wrf=new WeakReference<Context>(context);
        }

        // 手柄接入调用
        //player手柄信息
        @Override
        public void OnPlayerEnter(Player player) {
            Log.d(TAG,"OnPlayerEnter:player,info:"+new Gson().toJson(player));
            Toast.makeText(wrf.get(),player.getGamePad().name+"设备接入成功!",Toast.LENGTH_LONG).show();
        }

        // 手柄断开连接信息
        //player手柄信息
        @Override
        public void OnPlayerExit(Player player) {
            // 有手柄拔出
            Log.d(TAG,"player,info:"+new Gson().toJson(player));
            Toast.makeText(wrf.get(),player.getGamePad().name+"设备断开连接!",Toast.LENGTH_LONG).show();
        }

        /**
         * 通过摇杆控制游戏
         * @param player 玩家
         * @param ev
         * @return
         */
        @Override
        public boolean onPlayerMotionEvent(Player player, MotionEvent ev) {
            Log.d(TAG,"OnPlayerListenerImp:MotionEvent:"+ev);
            //摇杆的响应不能有延迟 通常不超过1ms为最佳   否则会造成摇杆事件丢失
            return true;
        }
        /**
         * 通过按键控制游戏
         * @param ev
         * @return
         */
        @Override
        public boolean onPlayerKeyEvent(Player player, KeyEvent ev) {
            //按键的响应不能有延迟,通常不超过2ms为最佳  否则会造成按键丢失
            Log.d(TAG,"OnPlayerListenerImp:KeyEvent:"+ev);
            Log.d(TAG, "dispatchKeyEvent:------映射完成后" + ev.toString());

            JoystickDevice.getInstance().handleKeyEvent(0,ev);
            return true;
        }
    }

    /**
     *
     */
    class DpadKeyEventLinster implements JoystickDevice.OnDpadKeyEventLinster{
        @Override
        public void onDpadKey(int player, KeyEvent event) {
            Log.e(TAG,"DpadKeyEventLinster,event:"+event);
        }
    }



}
