package indi.fmy.roottouch.utils;

import android.view.InputDevice;

/**
 * Created by Stardust on 2017/8/1.
 */

public class InputDevicesUtil {

    public static String getTouchDeviceName() {
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (supportSource(device, InputDevice.SOURCE_TOUCHSCREEN) || supportSource(device, InputDevice.SOURCE_TOUCHPAD)) {
                return device.getName();
            }
        }
        return null;
    }

    private static boolean supportSource(InputDevice device, int source) {
        return (device.getSources() & source) == source;
    }


}
