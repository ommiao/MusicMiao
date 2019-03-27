package cn.ommiao.musicmiao.utils;

import com.orhanobut.logger.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class StringUtil {

    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().length() == 0;
    }

    public static boolean writeToFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
        } catch (Exception e) {
            Logger.e("写文件出错" + e.getMessage());
            return false;
        }
        return true;
    }

}
