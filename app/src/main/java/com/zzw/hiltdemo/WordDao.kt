package com.zzw.hiltdemo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *
 * @author Created by lenna on 2020/9/7
 */
@Dao
interface WordDao {
    @Query("select * from word_table order by word asc")
    fun getAlphabetizedWords(): LiveData<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Query("delete from word_table")
    suspend fun deleteAll()
}