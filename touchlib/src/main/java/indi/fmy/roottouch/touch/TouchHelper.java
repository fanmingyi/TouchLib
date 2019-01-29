package indi.fmy.roottouch.touch;

import java.util.HashSet;
import java.util.Set;

import static indi.fmy.roottouch.jconst.InputEventCodesConst.ABS_MT_POSITION_X;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.ABS_MT_POSITION_Y;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.ABS_MT_SLOT;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.ABS_MT_TRACKING_ID;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.BTN_TOUCH;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.BTN_TOUCH_DOWN;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.EV_ABS;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.EV_KEY;
import static indi.fmy.roottouch.jconst.InputEventCodesConst.EV_SYN;

public class TouchHelper {

    private Set<Long> fingerSet = new HashSet<>();

    private RootTouch mRootTouch;

    public TouchHelper(RootTouch rootTouch) {
        this.mRootTouch = rootTouch;
    }

    public void exit() {
        Set<Long> fingerSetTemp = new HashSet<>(fingerSet);
        for (Long fingerIndex : fingerSetTemp) {
            touchUp(fingerIndex);
        }
    }

    private boolean sendOperationCmd(long type, long code, long value) {
        return mRootTouch.sendOperationCmd(type, code, value);
    }

    public void touchDown(long x, long y, long finger) {
        fingerSet.add(finger);
        sendOperationCmd(EV_ABS, ABS_MT_SLOT, finger);
        sendOperationCmd(EV_ABS, ABS_MT_TRACKING_ID, finger);
        sendOperationCmd(EV_KEY, BTN_TOUCH, BTN_TOUCH_DOWN);
        sendOperationCmd(EV_ABS, ABS_MT_POSITION_X, x);
        sendOperationCmd(EV_ABS, ABS_MT_POSITION_Y, y);
        sendOperationCmd(EV_SYN, 0, 0);
    }

    public void touchUp(long finger) {
        fingerSet.remove(finger);
        sendOperationCmd(EV_ABS, ABS_MT_SLOT, finger);
        sendOperationCmd(EV_ABS, ABS_MT_TRACKING_ID, -1);
        sendOperationCmd(EV_SYN, 0, 0);


    }

    public void touchMove(long x, long y, long finger) {
        sendOperationCmd(EV_ABS, ABS_MT_SLOT, finger);
        sendOperationCmd(EV_KEY, BTN_TOUCH, BTN_TOUCH_DOWN);
        sendOperationCmd(EV_ABS, ABS_MT_POSITION_X, x);
        sendOperationCmd(EV_ABS, ABS_MT_POSITION_Y, y);
        sendOperationCmd(EV_SYN, 0, 0);
    }

    public void click(long x, long y, long finger) {
        touchDown(x,y,finger);
        touchUp(finger);

    }
}
