package com.lianglu.weyue.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Liang_Lu on 2017/12/12.
 * 保存书籍工具类
 */

public class BookSaveUtils {
    private static volatile BookSaveUtils sInstance;

    public static BookSaveUtils getInstance(){
        if (sInstance == null){
            synchronized (BookSaveUtils.class){
                if (sInstance == null){
                    sInstance = new BookSaveUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName, String fileName, String content) {
        File file = BookManager.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

}
