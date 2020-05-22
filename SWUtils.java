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
 * 
 * 
 * OPPO A37M: 720x1280, 320            - 360  
 * OPPO A57: 720x1280, 320             - 360  
 * 小米5: 1080x1920, 480                - 360
 * 小米CC9 Pro: 1080x2340, 440          - 392
 * 小米10 Pro: 1080x2340, 440           - 392
 * 小米8 SE : 1080x2244, 440            - 392
 * 小米CC9e:720x1560, 320               - 360
 * 小米MIX2 : 1080x2160, 440            - 392
 * 华为荣耀9X: 1080x2340, 480            - 360
 * 华为nova4: 1080x2310, 480            - 360
 * 锤子坚果pro 2s: 1080x2160, 400       - 432
 * nubia X: 1080x2280, 480             - 360
 * 金立GN3001: 720x1280, 320            - 360 
 * 一加 OnePlus 7 Pro:1440x3120, 560    - 411
 * 美图M6/T8/T9: 1080x1920, 480         - 360
 * LG Nexus 5X: 1080x1920, 420         - 411
 * 魅族 16th: 1080x2160, 480            - 360
 * 三星 Galaxy Note4: 1440x2560, 640    - 360
 * 华为Mate 20 Pro: 1440x3120, 640      - 360
 * 诺基亚NOKIA TA-1131: 1080x2246, 420   - 411
 * vivo X30: 1080x2400, 480             - 360
 * 华为P40 Pro: 1200x2640, 480          - 400
 * vivo X30: 1080x2400, 480             - 360
 * 酷派 Coolpad Y75                     - 360
 */
public class SWUtils {

    // 是否输出日志
    private final boolean DEBUG = true;

    // dimen name 前缀
    private final String DIMEN_ITEM_NAME_PRE = "common_measure_";

     // 设计的标准尺寸
    private final float DESIGN_SIZE = 360.0f;

    // 需要适配的所有尺寸，基本可以适配市面上的大部分机型
    private final int[] SW_LIST = new int[]{360, 390, 400, 410, 430, 480, 500, 600, 720, 800};
    // 如果担心适配不全，可以每隔20设置一个档位
    // private final int[] SW_LIST = new int[]{360, 380, 390, 400, 410, 420, 430, 440, 460, 480, 
    //     500, 520, 540, 560, 580, 600, 620, 640, 660, 680, 700, 720, 740, 760, 780, 800,
    //     820, 840, 860, 880, 900, 920, 940, 960, 980, 1000, 1020, 1040, 1060, 1080, 1100,
    //     1120, 1140, 1160, 1180, 1200, 1220, 1240, 1260, 1280, 1300};
    // private final int[] SW_LIST = new int[]{480};

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
        // if (DEBUG) {
            // System.out.println(info);
        // }
        return info;
    }

    /**
     * 根据 SW_LIST 以及 DESIGN_SIZE 计算出每个尺寸对应的实际数据
     * 在当前文件目录下生成 outputs 输出文件夹
     */
    private void formatSW() {

        File dir = new File(getClass().getResource("/").getPath());
        File outDir = new File(dir, "outputs");
        deleteDir(outDir);

        for (int i = 0; i < SW_LIST.length; i++) {
            StringBuilder outputs = new StringBuilder();
            outputs.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            outputs.append("<resources>\n");
            int cur = SW_LIST[i];
            File swDir = new File(outDir, "values-sw" + cur + "dp");
            swDir.mkdirs();
            File swFile = new File(swDir, "dimens.xml");

            outputs.append(getValue(cur, 0.5f, "0_5"));
            for (int j = 0; j <= MAX_DP; j++) {
                outputs.append(getValue(cur, j, j+""));
            }

            outputs.append("</resources>");
            writeFile(swFile, outputs.toString());
        }

    }

    private void deleteDir(File dir) {
        if (dir.exists()) {
            if (dir.isFile()) {
                dir.delete();
                if (DEBUG) {
                    System.out.println("Delete " + dir.getAbsolutePath() + " Success");
                }
            } else {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i]);
                }
            }
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
