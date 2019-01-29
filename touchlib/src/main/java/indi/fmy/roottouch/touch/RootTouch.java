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


    public void touchDown(long x, long y, long finger) {
        check();
        touchHelper.touchDown(x, y, finger);
    }

    public void touchUp(long finger) {
        check();
        touchHelper.touchUp(finger);
    }

    public void check() {
        if (!initSuccess || isExit) {
            if (isExit) {
                throw new OperationInvalid("当前对象已经退出，请重新init");
            } else {
                throw new OperationInvalid("当前对象初始化未成功");
            }
        }
    }

    public void touchMove(long x, long y, long finger) {
        check();
        touchHelper.touchMove(x, y, finger);
    }

    public void click(long x, long y, long finger){
        check();
        touchHelper.click(x, y, finger);

    }

    public boolean exit() {
        isExit=true;
        touchHelper.exit();
        return nativeExit();
    }


    static {
        System.loadLibrary("native-lib");
    }


    static class OperationInvalid extends RuntimeException {

        public OperationInvalid(String message) {
            super(message);
        }
    }
}
