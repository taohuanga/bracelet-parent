package os.bracelets.parents.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import aio.health2world.utils.Logger;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.bean.RemindBean;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 数据操作类
 * Created by Administrator on 2018/7/4 0004.
 */

public class DBManager {

    static final String TAG = "DBManager";

    private static final DBManager ourInstance = new DBManager();

    private final DBOpenHelper dbHelper;
    //提醒
    private Dao<RemindBean, Integer> remindDao;

    public static DBManager getInstance() {
        return ourInstance;
    }

    private DBManager() {
        dbHelper = new DBOpenHelper(MyApplication.getInstance());
    }


    public Dao<RemindBean, Integer> getRemindDao() {
        if (remindDao == null) {
            try {
                remindDao = dbHelper.getDao(RemindBean.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return remindDao;
    }

    /**
     * 将居民信息保存到本地
     */
    public void saveRemindList(List<RemindBean> list) {
        try {
            DatabaseConnection connection = getRemindDao().startThreadConnection();
            Savepoint savepoint = connection.setSavePoint(null);
            for (RemindBean remind : list) {
                getRemindDao().createOrUpdate(remind);
            }
            connection.commit(savepoint);
            getRemindDao().endThreadConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前登录者信息
     */
    public List<RemindBean> getRemindList() {
        List<RemindBean> list = null;
        try {
            list = getRemindDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void closeDB() {
        dbHelper.close();
    }

    public void deleteTable() {
        //truncate(drop) table if exists
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from t_resident");
        db.execSQL("delete from t_doctor");
    }

    /**
     * 导出数据库到一体机的外部存储
     */
    public void exportDb() {
        //判断SD卡是否存在
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals
                (android.os.Environment.getExternalStorageState());
        if (!sdExist) {
//            Logger.e(TAG, "SD卡不存在，请加载SD卡！");
            return;
        }
        Observable.just(DBOpenHelper.DATABASE_NAME)
                .map(new Func1<String, File>() {
                    @Override
                    public File call(String dbName) {
                        File dbFile = MyApplication.getInstance().getDatabasePath(dbName);
                        if (!dbFile.exists()) {
                            throw new RuntimeException("database file not exists");
                        } else
                            return dbFile;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(TAG, "export db error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(File file) {
                        //获取sd卡路径
                        String dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        //数据库所在目录
                        dbDir += "/health2world";
                        String dbPath = dbDir + "/" + DBOpenHelper.DATABASE_NAME;//数据库路径
                        //判断目录是否存在，不存在则创建该目录
                        File dirFile = new File(dbDir);
                        if (!dirFile.exists())
                            dirFile.mkdirs();
                        InputStream fis = null;
                        OutputStream fos = null;
                        try {
                            fis = new FileInputStream(file);
                            fos = new FileOutputStream(new File(dbPath));
                            byte[] buf = new byte[1024];
                            int length;
                            while ((length = fis.read(buf)) != -1) {
                                fos.write(buf, 0, length);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fis != null)
                                    fis.close();
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Logger.e(TAG, "export db success: " + dbPath);
                    }
                });
    }
}
