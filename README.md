## Android屏幕适配之 SmallestWidth 方式的小工具

* 这个方案的的使用方式和我们平时在布局中引用 dimens 无异，核心点在于生成 dimens.xml 文件，本小工具就是生成各种需要适配的尺寸，可以按需修改代码

### smallestWidth 屏幕适配简介
* [Google官方文档](https://developer.android.com/guide/topics/resources/providing-resources?hl=zh-cn#java)
* smallestWidth意为最小宽度，比如 sw320dp。屏幕的基本尺寸，由可用屏幕区域的最小尺寸指定。设备的 smallestWidth 是屏幕可用高度和宽度的最小尺寸。无论屏幕的当前方向如何，均可使用此限定符确保应用界面的可用宽度至少为 320dp。
* 当应用为多个资源目录提供不同的 smallestWidth 限定符值时，系统会使用最接近（但未超出）设备 smallestWidth 的值。
* smallestWidth 适配以 dp 为单位适配，适配完文件夹类似于👇
    ```
    ├── src/main
    │   ├── res
    │   ├── ├──values
    │   ├── ├──values-sw360dp
    │   ├── ├────────── ├──dimens.xml
    │   ├── ├──values-sw400dp
    │   ├── ├────────── ├──dimens.xml
    │   ├── ├──values-sw480dp
    │   ├── ├────────── ├──dimens.xml
    │   ├── ├──...
    │   ├── ├──values-sw600dp
    │   ├── ├────────── ├──dimens.xml
    ```
* smallestWidth 的原理是开发者先在项目中根据主流屏幕的 最小宽度 (smallestWidth) 生成一系列 values-sw[XX]dp 文件夹 (含有 dimens.xml 文件)，系统会根据当前设备屏幕的 最小宽度 (smallestWidth) 自动匹配对应的 values-sw[XX]dp 文件夹

### smallestWidth 计算
* 假设我们以 1920 * 1080屏幕分辨率 480 dpi 为标准（比如小米5）
    ```
    查看设备的屏幕分辨率命令：  adb shell wm size
    查看设备的DPI命令：        adb shell wm density
    输入 adb shell wm 可以查看 wm 后面可查看的所有支持的信息
    ```
* 最小宽度就是1080 (很显然 1080 > 1920), density = DPI / 160 = 480/160=3; 屏幕的总 dp 宽度 = 屏幕的总 px 宽度 / density =1080/3=360; 
系统会自动帮我们使用 values-sw360dp 文件夹下的 dimens.xml 文件。
    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <resources>
        <dimen name="common_measure_1dp">1dp</dimen>
        <dimen name="common_measure_2dp">1dp</dimen>
        <dimen name="common_measure_3dp">1dp</dimen>
        <dimen name="common_measure_4dp">1dp</dimen>
        ...
    </resources>
    ```
* 小米9的屏幕 (1080x2340，440dpi), 我们适配该屏幕时的计算方式其实就是按比例缩放,
。小米9的屏幕总dp宽度=1080/(440/160)=392, 我们可以创建一个values-sw390dp/dimens.xml的文件，
计算公式：目标宽度/标准宽度 * 对应的数值 ，计算3dp的数值：390/360*3=3.25，大概是这样的
    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <resources>
        <dimen name="common_measure_0_5dp">0.54dp</dimen>
        <dimen name="common_measure_1dp">1.08dp</dimen>
        <dimen name="common_measure_2dp">2.17dp</dimen>
        <dimen name="common_measure_3dp">3.25dp</dimen>
        <dimen name="common_measure_4dp">4.33dp</dimen>
        <dimen name="common_measure_5dp">5.42dp</dimen>
        <dimen name="common_measure_6dp">6.50dp</dimen>
        ...
    </resources>
    ```

### 代码中使用
* 布局文件直接使用dimens.xml文件中的数据这是宽高
    ```
    <TextView
        android:layout_width="@dimen/common_measure_100dp"
        android:layout_height="@dimen/common_measure_50dp"/>
    ```
* 代码中使用，有些布局需要在代码中计算后给出高度
    ScreenUtil.getDimensValue("50"); 即可找出对应的values-sw[XX]]dp/dimens.xml内对应的数据
    ```
    public class ScreenUtil {
        public static float getDimensValue(String dp) {
            String name = "common_measure_"+dp+"dp";
            float value = 0;
            try {
                Context context = AppHelper.getAppContext();
                int id = context.getResources().getIdentifier(name, "dimen", context.getPackageName());
                value = context.getResources().getDimension(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }
    }
    ```

### SWUtils 代码解释
* 我们的设计中需要使用一个0.5dp,所以提供的代码中有0.5dp,由于命名规则的限制，0.5dp我命名为"0_5", 如果不需要，可以移除;
* DIMEN_ITEM_NAME_PRE 对应生成的dimens.xml 中各个数值的命名前缀，如果修改了，java代码中获取对应的数值时也需要修改读取的name,位于ScreenUtil.getDimensValue()内;
* DESIGN_SIZE 为设计的标准尺寸
* SW_LIST 为需要适配的所有的尺寸
* MAX_DP 为生成的dimens.xml文件中最大需要适配到多少dp
