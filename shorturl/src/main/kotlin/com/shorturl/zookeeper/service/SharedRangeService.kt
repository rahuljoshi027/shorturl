package com.shorturl.zookeeper.service

import com.shorturl.zookeeper.model.Range
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong
import org.apache.curator.framework.recipes.shared.SharedCountListener
import org.apache.curator.framework.recipes.shared.SharedCountReader
import org.apache.curator.framework.state.ConnectionState
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.retry.RetryNTimes
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class SharedRangeService(
    @Value("\${kafka.zookeeper.address}") private val zooKeeper: String,
    @Value("\${kafka.zookeeper.counter.path}") private val zooKeeperCounterPath: String
) : SharedCountListener {
    private val client: CuratorFramework
    private val count: DistributedAtomicLong
    private val range: Range

    init {
        client = CuratorFrameworkFactory.newClient(
            zooKeeper,
            ExponentialBackoffRetry(1000, 3)
        )
        client.start()
        count = DistributedAtomicLong(
            client, zooKeeperCounterPath,
            RetryNTimes(10, 10)
        )
        range = Range(0, 0, AtomicLong(0))
    }

    fun nextCounter(): Long {
        synchronized(this) {
            if (range.next.get() == range.end) {
                range.next.set(newRange())
            }
        }
        return range.next.incrementAndGet()

    }

    private fun newRange(): Long {
        var value = count.increment()
        if (value.succeeded()) {
            range.start = (value.preValue() * 10000) + 1
            range.end = value.postValue() * 10000
        }
        return range.start
    }


    override fun stateChanged(client: CuratorFramework?, newState: ConnectionState?) {
    }

    override fun countHasChanged(sharedCount: SharedCountReader?, newCount: Int) {
    }
}