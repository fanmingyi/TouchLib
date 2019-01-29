#include <jni.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include<android/log.h>


#include <stdint.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <errno.h>
#include <zconf.h>

static struct pollfd *ufds = NULL;
static char *device_names = NULL;
/**
 * 驱动位置目录
 */
static char *devDir = "/dev/input";

/**
 * 驱动文件的描述符
 */
int devFd = -1;
#define TAG "FMY" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型


static int device_name_equals(int deviceFd, const char *device_name) {
    char name[80];
    name[sizeof(name) - 1] = '\0';
    if (ioctl(deviceFd, EVIOCGNAME(sizeof(name) - 1), &name) < 1) {
        fprintf(stderr, "could not get device name for %d, %s, %s\n", deviceFd, device_name,
                strerror(errno));
        return -1;
    }
    //printf("device name: %s\n", name);
    if (strcmp(name, device_name) == 0) {
        return 1;
    }
    return 0;
}

char *open_device_by_name(const char *dirname, char *device_name) {
    ufds = static_cast<pollfd *>(calloc(1, sizeof(ufds[0])));
    char devname[PATH_MAX];
    char *filename;
    DIR *dir;
    struct dirent *de;
    dir = opendir(dirname);
    if (dir == NULL)
        return "";
    strcpy(devname, dirname);
    filename = devname + strlen(devname);
    *filename++ = '/';
    while ((de = readdir(dir))) {
        if (de->d_name[0] == '.' &&
            (de->d_name[1] == '\0' ||
             (de->d_name[1] == '.' && de->d_name[2] == '\0')))
            continue;
        strcpy(filename, de->d_name);
        int fd = open(devname, O_RDWR);
        if (fd < 0) {
            fprintf(stderr, "could not open %s, %s\n", devname, strerror(errno));
            return "";
        }
        //printf("open device: %s\n", devname);
        if (device_name_equals(fd, device_name) == 1) {
            close(fd);
            return devname;
        } else {
            close(fd);
        }
    }
    closedir(dir);
    return "";
}

int open_device_by_name_fd(const char *dirname, const char *device_name) {
    ufds = static_cast<pollfd *>(calloc(1, sizeof(ufds[0])));
    char devname[PATH_MAX];
    char *filename;
    DIR *dir;
    struct dirent *de;
    dir = opendir(dirname);
    if (dir == NULL)
        return -1;
    strcpy(devname, dirname);
    filename = devname + strlen(devname);
    *filename++ = '/';
    while ((de = readdir(dir))) {
        if (de->d_name[0] == '.' &&
            (de->d_name[1] == '\0' ||
             (de->d_name[1] == '.' && de->d_name[2] == '\0')))
            continue;
        strcpy(filename, de->d_name);
        int fd = open(devname, O_RDWR);
        if (fd < 0) {
            fprintf(stderr, "could not open %s, %s\n", devname, strerror(errno));
            return -1;
        }
        //printf("open device: %s\n", devname);
        if (device_name_equals(fd, device_name) == 1) {
            device_names = devname;
            return fd;
        } else {
            close(fd);
        }
    }
    closedir(dir);
    return -1;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_indi_fmy_roottouch_touch_RootTouch_nativeExit(JNIEnv *env, jobject instance) {


    LOGI("设备驱动初 退出");

    int result = close(devFd);
    if (result == 0) {

        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_indi_fmy_roottouch_touch_RootTouch_nativeInit(JNIEnv *env, jobject instance,
                                                   jstring mDevName_) {
    LOGI("设备驱动初始化");

    const char *mDevName = env->GetStringUTFChars(mDevName_, 0);

    devFd = open_device_by_name_fd(devDir, mDevName);

    env->ReleaseStringUTFChars(mDevName_, mDevName);

    if (devFd == -1) {
        LOGI("设备驱动初始化 失败");

        return JNI_FALSE;
    } else {
        LOGI("设备驱动初始化 成功");

        return JNI_TRUE;
    }


}

extern "C"
JNIEXPORT jboolean JNICALL
Java_indi_fmy_roottouch_touch_RootTouch_sendOperationCmd(JNIEnv *env, jobject instance, jlong type,
                                                         jlong code, jlong value) {


    LOGI("向设备驱动发送信息 type: %d code: %d value: %d", type, code, value);
    struct input_event event;

    memset(&event, 0, sizeof(event));

    event.type = type;
    event.code = code;
    event.value = value;

    if (devFd != -1) {
        ssize_t writeSize = write(devFd, &event, sizeof(event));
        if (writeSize == sizeof(event)) {
            return JNI_TRUE;
        } else {
            return JNI_FALSE;
        }
    } else {
        return JNI_FALSE;
    }
}