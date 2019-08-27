package os.bracelets.parents.utils;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import aio.health2world.utils.ToastUtil;

/**
 * Created by zhouyezi on 16/8/24.
 */

public class FileUtils {

    /**
     * 需要知道当前SD卡的目录，Environment.getExternalStorageDierctory()
     */


    private static String path;

    public FileUtils(String path) { // 目录名/sdcard
        this.path = Environment.getExternalStorageDirectory() + File.separator + path;
    }

    public String getPath() {
        return path;
    }

    // 将字符串写入到文本文件中
    public synchronized File writeTxtToFile(String strcontent, String fileName) {
        File file = null;
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(fileName);

        String strFilePath = path + File.separator + fileName;
        // 每次写入时，都换行写
//        String strContent = strcontent + "\r\n";
        // 每次写入时，都换列写
        String strContent = strcontent + ",";
        try {
            file = new File(path, fileName);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes("GB2312"));
            raf.close();
//            Writer outTxt = new OutputStreamWriter(new FileOutputStream(file,true), "ANSI");
//            outTxt.write("文件内容格式不正确, 此文件已被系统删除! ");
//            outTxt.close();
        } catch (Exception e) {
            ToastUtil.showShort("文件写入失败,请检查是否有读写权限！");
        }
        return file;
    }

    // 生成文件
    public File makeFilePath(String fileName) {
        File file = null;
        makeRootDirectory();
        try {
            file = new File(path, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static File makeRootDirectory() {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
        return file;
    }

    public synchronized List<File> getFile() {
        List<File> fileList = new ArrayList<File>();
        File file = makeRootDirectory();
        File[] fileArray = file.listFiles();
        if (fileArray == null) return fileList;
        for (File f : fileArray) {
            if (f.isFile()) {
                fileList.add(f);
            } else {
                getFile();
            }
        }
        return fileList;
    }


    /**
     * 删除单个文件
     *
     * @return 文件删除成功返回true，否则返回false
     */
    public synchronized boolean deleteFile(String fileName) {
        File file = new File(path, fileName);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
//        dir.delete();// 删除目录本身
    }


    // 判断sd卡上的文件夹是否存在
    public boolean isFileExist(String fileName) {
        File file = new File(path, fileName);
        return file.exists();
    }


    /**
     * 将一个inputstream里面的数据写入SD卡中 第一个参数为目录名 第二个参数为文件名
     */
    public File write2SDFromInput(String fileName, InputStream inputstream) {
        File file = null;
        OutputStream output = null;
        try {
            file = makeFilePath(fileName);
            output = new FileOutputStream(file);
// 4k为单位，每4K写一次
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            while ((temp = inputstream.read(buffer)) != -1) {
// 获取指定信,防止写入没用的信息
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return file;
    }

    public static String file2Base64(String filePath) {
        FileInputStream objFileIS = null;
        try {
            objFileIS = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
        byte[] byteBufferString = new byte[1024];
        try {
            for (int readNum; (readNum = objFileIS.read(byteBufferString)) != -1; ) {
                objByteArrayOS.write(byteBufferString, 0, readNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String videodata = Base64.encodeToString(objByteArrayOS.toByteArray(), Base64.DEFAULT);
        return videodata;
    }
}