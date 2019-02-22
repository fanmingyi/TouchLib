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
import static java.lang.Math.abs;

public class TouchHelper {

    private Set<Long> fingerSet = new HashSet<>();

    public Set<Long> getFingerSet() {
        return fingerSet;
    }

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

    public boolean touchDown(long x, long y, long finger) {
        fingerSet.add(finger);

        Set<Boolean> relSet = new HashSet<>();
        boolean rel;

        rel = sendOperationCmd(EV_ABS, ABS_MT_SLOT, finger);
        relSet.add(rel);

        rel = sendOperationCmd(EV_ABS, ABS_MT_TRACKING_ID, finger);
        relSet.add(rel);

        rel = sendOperationCmd(EV_KEY, BTN_TOUCH, BTN_TOUCH_DOWN);
        relSet.add(rel);

        rel = sendOperationCmd(EV_ABS, ABS_MT_POSITION_X, x);
        relSet.add(rel);

        rel = sendOperationCmd(EV_ABS, ABS_MT_POSITION_Y, y);
        relSet.add(rel);


        rel = sendOperationCmd(EV_SYN, 0, 0);
        relSet.add(rel);

        return !relSet.contains(Boolean.FALSE);

    }

    public boolean touchUp(long finger) {
        fingerSet.remove(finger);

        Set<Boolean> relSet = new HashSet<>();
        boolean rel;


        rel = sendOperationCmd(EV_ABS, ABS_MT_SLOT, finger);
        relSet.add(rel);

        rel = sendOperationCmd(EV_ABS, ABS_MT_TRACKING_ID, -1);
        relSet.add(rel);

        rel = sendOperationCmd(EV_SYN, 0, 0);
        relSet.add(rel);


        return !relSet.contains(Boolean.FALSE);


    }

    public boolean touchMove(long x, long y, long finger) {

        Set<Boolean> relSet = new HashSet<>();
        boolean rel;


        rel = sendOperationCmd(EV_ABS, ABS_MT_SLOT, finger);
        relSet.add(rel);
        rel = sendOperationCmd(EV_KEY, BTN_TOUCH, BTN_TOUCH_DOWN);
        relSet.add(rel);
        rel = sendOperationCmd(EV_ABS, ABS_MT_POSITION_X, x);
        relSet.add(rel);
        rel = sendOperationCmd(EV_ABS, ABS_MT_POSITION_Y, y);
        relSet.add(rel);
        rel = sendOperationCmd(EV_SYN, 0, 0);
        relSet.add(rel);
        return !relSet.contains(Boolean.FALSE);
    }

    public boolean click(long x, long y, long finger) {

        Set<Boolean> relSet = new HashSet<>();
        boolean rel;

        rel = touchDown(x, y, finger);
        relSet.add(rel);

        rel = touchUp(finger);
        relSet.add(rel);

        return !relSet.contains(Boolean.FALSE);

    }


    public boolean touchSwip(long startX, long startY, long endX, long endY, long finger, long duration) {

        Set<Boolean> relSet = new HashSet<>();
        boolean rel;

        rel = touchDown(startX, startY, finger);

        relSet.add(rel);
        double xiDistance = abs(startX - endX);

        double yiDistance = abs(startY - endY);

        double xDelta = xiDistance / duration;

        double yDelta = yiDistance / duration;

        for (long i = 0; i < duration; i++) {
            rel = touchMove((long) (xDelta * i + startX), (long) (yDelta * i + startY), finger);
            relSet.add(rel);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


        rel = touchUp(finger);
        relSet.add(rel);

        return !relSet.contains(Boolean.FALSE);
    }
}
