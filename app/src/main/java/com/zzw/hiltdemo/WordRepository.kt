package com.zzw.hiltdemo

import androidx.lifecycle.LiveData

/**
 *
 * @author Created by lenna on 2020/9/7
 */
class WordRepository(private val wordDao: WordDao) {
    val allWords: LiveData<List<Word>> = wordDao.getAlphabetizedWords()
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
}