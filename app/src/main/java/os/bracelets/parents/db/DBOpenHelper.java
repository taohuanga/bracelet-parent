package os.bracelets.parents.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import os.bracelets.parents.bean.RemindBean;

/**
 * Created by lishiyou on 2017/6/29.
 */

public class DBOpenHelper extends OrmLiteSqliteOpenHelper {

    public static final String TAG = "DBOpenHelper";
    /*** 数据库名称**/
    public static String DATABASE_NAME = "bracelets.db";
    /*** 数据库版本**/
    public static int DATABASE_VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, RemindBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int newVersion, int oldVersion) {
        try {
            TableUtils.dropTable(connectionSource, RemindBean.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
    }

}
