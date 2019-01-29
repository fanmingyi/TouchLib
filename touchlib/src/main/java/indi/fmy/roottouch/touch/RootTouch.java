package indi.fmy.roottouch.touch;

import android.content.Context;
import android.text.TextUtils;

import indi.fmy.roottouch.utils.InputDevicesUtil;
import indi.fmy.roottouch.utils.ShellUtils;

public class RootTouch {

    private Context mcontext;

    private String mDevName;

    private boolean initSuccess = false;

    private boolean isExit = false;


    private TouchHelper touchHelper;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        exit();
    }

    public RootTouch(Context context) {
        this.mcontext = context;
    }

    /**
     * 初始化判断是否成功
     *
     * @return
     */
    public boolean init() {


        touchHelper = new TouchHelper(this);

        boolean havePermission = ShellUtils.checkRootPermission();


        if (!havePermission) {
            initSuccess = false;
            return initSuccess;
        }

        ShellUtils.execCommand("chmod 777 /dev", true);
        ShellUtils.execCommand("chmod 777 /dev/input", true);
        ShellUtils.execCommand("chmod 777 /dev/input/*", true);
        ShellUtils.execCommand("setenforce 0", true);
        mDevName = InputDevicesUtil.getTouchDeviceName();

        if (TextUtils.isEmpty(mDevName)) {
            initSuccess = false;
            return initSuccess;
        }

        initSuccess = nativeInit(mDevName);

        return initSuccess;

    }

    native boolean nativeInit(String mDevName);

    native boolean nativeExit();

    native boolean sendOperationCmd(long type, long code, long value);


    public boolean touchDown(long x, long y, long finger) {
        check();
        return touchHelper.touchDown(x, y, finger);
    }

    public boolean touchUp(long finger) {
        check();
        return touchHelper.touchUp(finger);
    }


    public boolean touchMove(long x, long y, long finger) {
        check();
        return touchHelper.touchMove(x, y, finger);
    }

    public boolean click(long x, long y, long finger) {
        check();
        return touchHelper.click(x, y, finger);

    }

    public boolean touchSwip(long startX, long startY, long endX, long endY, long finger, long duration) {
        check();
        return touchHelper.touchSwip(startX, startY, endX, endY, finger, duration);
    }

    public boolean exit() {
        isExit = true;
        touchHelper.exit();
        return nativeExit();
    }


    static {
        System.loadLibrary("native-lib");
    }


    static class TouchOperationInvalid extends RuntimeException {

        public TouchOperationInvalid(String message) {
            super(message);
        }
    }


    public void check() {
        if (!initSuccess || isExit) {
            if (isExit) {
                throw new TouchOperationInvalid("当前对象已经退出，请重新init");
            } else {
                throw new TouchOperationInvalid("当前对象初始化未成功");
            }
        }
    }
}
