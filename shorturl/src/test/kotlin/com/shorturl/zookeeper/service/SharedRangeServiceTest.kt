package com.shorturl.zookeeper.service

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryOneTime
import org.apache.curator.test.TestingServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@DirtiesContext
@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SharedRangeServiceTest(@Autowired private val sharedRangeService: SharedRangeService) {

    lateinit var client: CuratorFramework

    @BeforeAll
    fun setup() {
        val server = TestingServer(2181)
        val builder: CuratorFrameworkFactory.Builder = CuratorFrameworkFactory.builder()
        client = builder.connectString(server.connectString).retryPolicy(RetryOneTime(1)).build()
        client.start()
    }

    @Test
    fun shouldReturnValidOutputOnCalling() {
        val actual = sharedRangeService.nextCounter()
        Assertions.assertEquals(2L, actual)
    }

    @AfterAll
    fun close() {
        client.zookeeperClient.zooKeeper.close()
        client.close()
    }
}