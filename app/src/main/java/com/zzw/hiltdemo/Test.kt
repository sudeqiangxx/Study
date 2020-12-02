package com.zzw.hiltdemo

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

/**
 *
 * @author Created by lenna on 2020/12/2
 */

/* 大顶堆，存储左半边元素 */
@RequiresApi(Build.VERSION_CODES.N)
private val left: PriorityQueue<Int> = PriorityQueue { o1, o2 -> o2 - o1 }

/* 小顶堆，存储右半边元素，并且右半边元素都大于左半边 */
private val right: PriorityQueue<Int> = PriorityQueue()

/* 当前数据流读入的元素个数 */
private var N = 0

fun insert(value: Int?) {
    /* 插入要保证两个堆存于平衡状态 */
    if (N % 2 == 0) {
        /* N 为偶数的情况下插入到右半边。
         * 因为右半边元素都要大于左半边，但是新插入的元素不一定比左半边元素来的大，
         * 因此需要先将元素插入左半边，然后利用左半边为大顶堆的特点，取出堆顶元素即为最大元素，此时插入右半边 */
        left.add(value)
        right.add(left.poll())
    } else {
        right.add(value)
        left.add(right.poll())
    }
    N++

}

fun getMedian(): Double? {
    return if (N % 2 == 0) (left.peek() + right.peek()) / 2.0 else right.peek()*1.0
}




/**
 *
 * @author Created by lenna on 2020/12/2
 * 字符流中第一次只出现一次的字符
 */
/**start*/

private var cnts: IntArray = IntArray(128)
private val queue: Queue<Char> = LinkedList()

fun insertQueue(ch: Char) {
    cnts[ch.toInt()]++
    queue.add(ch)
    while (!queue.isEmpty() && cnts[queue.peek().toInt()] > 1) {
        queue.poll()
    }
}

fun firstAppearingOnce(): Char {
    return if (queue.isEmpty()) '#' else queue.peek()
}

/**end*/

/**start滑动窗口的最大值 利用大顶堆*/

@RequiresApi(Build.VERSION_CODES.N)
fun maxInWindows(num: IntArray, size: Int): ArrayList<Int> {
    val ret: ArrayList<Int> = arrayListOf()
    if (size > num.size || size < 1) {
        return ret
    }
    var heap = PriorityQueue<Int> { o1, o2 -> o2 - o1 }
    var i: Int = 0
    for (i in 0 until size) {
        heap.add(num[i])
    }
    ret.add(heap.peek())
    run {
        var i = 0
        var j = i + size
        while (j < num.size) {
            /* 维护一个大小为 size 的大顶堆 */heap.remove(num[i])
            heap.add(num[j])
            ret.add(heap.peek())
            i++
            j++
        }
    }

    return ret
}

/***
 * 查找当前有序数组中两个数之和等于目标值target
 */
fun findNumberWithSum(nums: Array<Int>, target: Int): ArrayList<Int> {
    var i: Int = 0
    var j: Int = nums.size - 1
    while (i < j) {
        var cur: Int = nums[i] + nums[j]

        if (cur == target) return arrayListOf(nums[i], nums[j])
        if (cur < target) {
            i++
        } else {
            j--
        }
    }

    return arrayListOf()
}