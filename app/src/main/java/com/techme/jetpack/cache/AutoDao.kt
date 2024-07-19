package com.techme.jetpack.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.techme.jetpack.model.Author

// 定义一个DAO（Data Access Object）接口，用于与数据库交互
@Dao
interface AutoDao {

    // 插入Author实体到数据库
    // onConflict参数设置为REPLACE，意味着如果插入的记录已经存在，则更新该记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(author: Author): Long

    // 查询数据库中的第一条Author记录
    // 返回类型为可空的Author，因为在数据库中可能找不到记录
    @Query("SELECT * FROM author LIMIT 1")
    suspend fun getUser(): Author?

    // 更新数据库中的Author记录
    // onConflict同样设置为REPLACE，以确保更新时处理冲突
    // 返回值表示受影响的行数
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(author: Author): Int

    // 删除数据库中的Author记录
    // 返回值同样表示受影响的行数
    @Delete
    suspend fun delete(author: Author): Int
}