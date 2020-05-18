import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

/**
 * Created by slack on 20/5/18 11:30.
 * 
 * Android 屏幕适配方式： SmallestWidth 屏幕的基本尺寸，由可用屏幕区域的最小尺寸指定
 * 官方：https://developer.android.com/guide/topics/resources/providing-resources?hl=zh-cn#java
 * 
 * 以dp为单位，
 * 这个是一个生成工具，比如以屏幕宽360dp为设计标准，生成出其他尺寸对应的dp数据
 * 以 1080x1920屏幕分辨率， 480DPI 为设计标准，
 * density = DPI / 160 = 480/160=3
 * 屏幕的总 dp 宽度 = 屏幕的总 px 宽度 / density =1080/3=360
 * 
 */
public class SWUtils {

    // 是否输出日志
    private final boolean DEBUG = true;

    // dimen name 前缀
    private final String DIMEN_ITEM_NAME_PRE = "common_measure_";

     // 设计的标准尺寸
    private final float DESIGN_SIZE = 360.0f;

    // 需要适配的所有尺寸
    private final int[] SW_LIST = new int[]{360, 380, 400, 420, 440, 460, 480, 600};

    // 最大适配到的数据, 比如最大适配到500dp
    private final int MAX_DP = 600;

    //构造方法的字符格式这里如果小数不足2位,会以0补足， 如果需要更多小数位数，修改此处
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    
    /**
     * @param src  需要转换的 sw 
     * @param value 当前的 需要转换的数值
     * @param name 显示的名字 ， 0.5这种不好处理，需要一个指定的唯一的名字
     * @return
     */
    private String getValue(int src, float value, String name) {
        float result = src / DESIGN_SIZE * value;
        String formatResult = decimalFormat.format(result);
        String info = "    <dimen name=\"" + DIMEN_ITEM_NAME_PRE + name + "dp\">" + formatResult + "dp</dimen>\n";
        if (DEBUG) {
            System.out.println(info);
        }
        return info;
    }

    /**
     * 根据 SW_LIST 以及 DESIGN_SIZE 计算出每个尺寸对应的实际数据
     * 在当前文件目录下生成 outputs 输出文件夹
     */
    private void formatSW() {

        File dir = new File(getClass().getResource("/").getPath());
        File outDir = new File(dir, "outputs");

        StringBuilder outputs = new StringBuilder();

        for (int i = 0; i < SW_LIST.length; i++) {
            outputs.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            outputs.append("<resources>\n");
            int cur = SW_LIST[i];
            File swDir = new File(outDir, "values-sw" + cur + "dp");
            swDir.mkdirs();
            File swFile = new File(swDir, "dimens.xml");

            outputs.append(getValue(cur, 0.5f, "0_5"));
            for (int j = 1; j <= MAX_DP; j++) {
                outputs.append(getValue(cur, j, j+""));
            }

            outputs.append("</resources>");
            writeFile(swFile, outputs.toString());
        }

    }

    private void writeFile(File file, String info) {
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(info);
            writer.flush();
            writer.close();
            if (DEBUG) {
                System.out.println("Write " + file.getAbsolutePath() + " Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SWUtils utils = new SWUtils();
        utils.formatSW();
    }
}
