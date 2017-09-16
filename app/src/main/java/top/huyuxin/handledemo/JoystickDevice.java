package top.huyuxin.handledemo;

import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;

/**
 * 摇杆事件转换为按键
 */
public class JoystickDevice {

    final public static int       STICK_NONE           = 0;
    final public static int       STICK_UP_LEFT        = 1;
    final public static int       STICK_UP             = 2;
    final public static int       STICK_UP_RIGHT       = 3;
    final public static int       STICK_LEFT           = 4;
    final public static int       STICK_RIGHT          = 5;
    final public static int       STICK_DOWN_LEFT      = 6;
    final public static int       STICK_DOWN           = 7;
    final public static int       STICK_DOWN_RIGHT     = 8;
    public static final int       MAXPLAYER            = 4;

    private static JoystickDevice mJoystickDevice;
    /**
     * 记录上次的action，用于针对性处理ACTION_UP，ACTION_DOWN
     */
    private int                   mLastAction          = STICK_NONE;
    /**
     * STICK_* 事件到KEYCODE_DPAD_UP转换表
     * sStickCodeList[]的值为{UP,LEFT,RIGHT,DOWN}是否被按下
     */
    private boolean[][]           sStickCodeList       = { { false, false, false, false }, // STICK_NONE
            { true, true, false, false }, // STICK_UP_LEFT
            { true, false, false, false }, // STICK_UP
            { true, false, true, false }, // STICK_UP_RIGHT
            { false, true, false, false }, // STICK_LEFT
            { false, false, true, false }, // STICK_RIGHT
            { false, true, false, true }, // STICK_DOWN_LEFT
            { false, false, false, true }, // STICK_DOWN
            { false, false, true, true }, // STICK_DOWN_RIGHT
                                                       };
    // joyStick到方向的转换表,用于把将坐标转化成键值
    private int[][]               sJoyStick2ValueTable = { { STICK_UP_LEFT, STICK_LEFT, STICK_DOWN_LEFT },
            { STICK_UP, STICK_NONE, STICK_DOWN }, { STICK_UP_RIGHT, STICK_RIGHT, STICK_DOWN_RIGHT }, };

    private int[]                 mLastActionList;

    public JoystickDevice() {
        mLastActionList = new int[MAXPLAYER];
        for (int i = 0; i < MAXPLAYER; i++) {
            mLastActionList[i] = STICK_NONE;
        }
    }

    public static JoystickDevice getInstance() {
        if (mJoystickDevice == null) {
            mJoystickDevice = new JoystickDevice();
        }
        return mJoystickDevice;
    }

    public void handleMotionEvent(int player, SparseArray<Float> map) {
        int a, b;
        int x, y;
        // 注意: 改用角度计算会更准确
        // 参考AlibabaExDeviceEventHandler.convertJoyStickValueToDirection
        try {
            a = Math.round(map.get(0));
            b = Math.round(map.get(1));

            int stick = sJoyStick2ValueTable[a + 1][b + 1];
            if (STICK_NONE == stick) {
                // 摇杆中可能没有AXIS_HAT_X, AXIS_HAT_Y 这里会造成空指针
                x = Math.round(map.get(15) == null ? 0 : map.get(15));
                y = Math.round(map.get(16) == null ? 0 : map.get(16));
                stick = sJoyStick2ValueTable[x + 1][y + 1];
            }
            handleDpadKeyEvent(player, stick);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JoystickDevice","Joystick map error!");
        }
    }

    public void handleKeyEvent(int player,KeyEvent event) {
        mDpadKeyEventListener.onDpadKey(player,event);
    }

    /**
     * 向接口发送键值
     * 
     * @param event
     * @param action
     *            必须是STICK_NONE~STICK_DOWN_RIGHT
     */
    private void handleDpadKeyEvent(int player, int action) {
        mLastAction = mLastActionList[player];
        Log.d("JoystickDevice","dispatchDpadKeyEvent event:" + player + " act:" + mLastAction + "->" + action);
        if (mLastAction == action || mDpadKeyEventListener == null) {
            return;
        }
        // 8向处理：检测每个方向的变化并触发事件
        // KEYCODE_DPAD_UP
        if (sStickCodeList[action][0] != sStickCodeList[mLastAction][0]) {
            int keAction = sStickCodeList[action][0] ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
            mDpadKeyEventListener.onDpadKey(player, new KeyEvent(keAction, KeyEvent.KEYCODE_DPAD_UP));
        }
        // KEYCODE_DPAD_LEFT
        if (sStickCodeList[action][1] != sStickCodeList[mLastAction][1]) {
            int keAction = sStickCodeList[action][1] ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
            mDpadKeyEventListener.onDpadKey(player, new KeyEvent(keAction, KeyEvent.KEYCODE_DPAD_LEFT));
        }
        // KEYCODE_DPAD_RIGHT
        if (sStickCodeList[action][2] != sStickCodeList[mLastAction][2]) {
            int keAction = sStickCodeList[action][2] ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
            mDpadKeyEventListener.onDpadKey(player, new KeyEvent(keAction, KeyEvent.KEYCODE_DPAD_RIGHT));
        }
        // KEYCODE_DPAD_DOWN
        if (sStickCodeList[action][3] != sStickCodeList[mLastAction][3]) {
            int keAction = sStickCodeList[action][3] ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
            mDpadKeyEventListener.onDpadKey(player, new KeyEvent(keAction, KeyEvent.KEYCODE_DPAD_DOWN));
        }
        mLastActionList[player] = action; // 更新mLastAction
    }

    private static OnDpadKeyEventLinster mDpadKeyEventListener = null;

    /*
     * 用于回调键值
     */
    public interface OnDpadKeyEventLinster {

        public void onDpadKey(int player, KeyEvent event);
    }

    public static void setOnDpadKeyListener(OnDpadKeyEventLinster linster) {
        mDpadKeyEventListener = linster;
    }
}
