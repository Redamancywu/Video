package com.techme.jetpack.cache

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techme.jetpack.model.Author
import com.techme.jetpack.utils.AppGlobals

@Database(entities = [Author::class], version = 1, exportSchema = false)
abstract class CacheManger : RoomDatabase() {
    abstract val authorDao: AutoDao
    companion object {
        private val database =
            Room.databaseBuilder(AppGlobals.getApplication(), CacheManger::class.java, "cache.db")
                //是否允许在主线程进行怎删改查的操作
                .allowMainThreadQueries()
                //设置数据库操作数据的线程池对象
                //.setQueryExecutor(AppGlobals.getApplication().mainExecutor)
                //监听数据库打开 创建的回调
                //.addCallback()
                //.setJournalMode()
                //添加数据库升级的具体实现逻辑
            .addMigrations(object : Migration(1,2){
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE author ADD COLUMN name AVATAR2 VARCHAR NOT NULL DEFAULT '0'")
                }

            })
                .build()


        @JvmStatic
        fun get(): CacheManger {
            return database
        }
    }


}