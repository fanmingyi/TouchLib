# TouchLib
Android模拟触摸屏幕库(Android simulates a touch screen)
## 描述
在Root情况下也许你有模拟触摸屏幕的需求，如按键精灵那样的功能。

## 如何使用

1. 创建触摸对象

```java
  RootTouch  rootTouch = new RootTouch(Context context);
```

2. 初始化
> 点击事件
```java
rootTouch.init()
```
此函数会返回是否成功初始化，如成功初始化返回true。如果初始化失败那么调用触摸Api将会抛出`TouchOperationInvalid`异常

3. 调用对应的触摸事件

```java
rootTouch.click(x,y,finger)
```
使用第finger个手指点击屏幕(x,y)坐标。
finger参数为一个long类型，表示你用哪只手指点击。因为我们知道Android 是可以多点触摸的
如下代码
```java
//使用第0个手指点击屏幕(100,100)处
rootTouch.click(100,100,0)
//使用第1个手指点击屏幕(100,200)处
rootTouch.click(100,200,1)
```
> 滑动事件

```java
touchSwip(long startX, long startY, long endX, long endY, long finger, long duration) 
```
在duration(毫秒)设定的时间内匀速滑动屏幕从（starX，starY）到(endX，endY）
finger为使用哪个手指滑动

> 自定义事件

当上述的模拟触摸功能不满足需求时,你可以自定义触摸行为

> 模拟手指按下
```java
 public boolean touchDown(long x, long y, long finger);
```
> 模拟手指移动
```java
 public boolean touchMove(long x, long y, long finger);
```
> 模拟手指弹起
```java
  public boolean touchUp(long finger);
```

> 自定义事件案例

我想手指在3秒内从（100，100）滑动到（200，200）
```java

 long starX=100;
 long starY=100;
 long endX=200;
 long endY=200;
 long duration=3000
 
rootTouch.touchDown(startX, startY, 1);


double xiDistance = abs(startX - endX);

double yiDistance = abs(startY - endY);


double xDelta = xiDistance / duration;

double yDelta = yiDistance / duration;

for (long i = 0; i < duration; i++) {
     rootTouch.touchMove((long) (xDelta * i + startX),(long)(yDelta * i + startY), finger);
     try {
         Thread.sleep(1);
     } catch (InterruptedException e) {
                e.printStackTrace();
     }


 }


rootTouch.touchUp(finger);

```

4. 当你确定不再触摸屏幕时释放资源

```java
rootTouch.exit();
```

