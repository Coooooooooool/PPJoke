package com.example.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.libcommon.global.AppGlobals;

@Database(entities = {Cache.class},version = 1,exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {

    private static final CacheDatabase database ;

    static {
        //创建一个内存数据库
        //Room.inMemoryDatabaseBuilder();
         database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "ppjoke_cache")
                //是否允许在主线程查询，默认false
                .allowMainThreadQueries()
                //数据库创建和打开时的回调
                //.addCallback()
                //设置查询的线程池
                //.setQueryExecutor()
                //room的日志模式
                //.setJournalMode()
                //数据库升级异常后的回滚
                //.fallbackToDestructiveMigration()
                //.addMigrations(migration)
                .build();

    }

    public abstract CacheDao getCache();

    public static CacheDatabase getDatabase() {
        return database;
    }

    static Migration migration = new Migration(1,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("");
        }
    };
}
