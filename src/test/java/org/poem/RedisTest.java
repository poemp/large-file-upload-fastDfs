package org.poem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.poem.utils.RedisUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@SpringBootTest(classes = LargeFileUploaderApp.class)
@RunWith(SpringJUnit4ClassRunner.class)

public class RedisTest {

    @Test
    public void redisTest(){
        String redisKey = "redisKey";
        RedisUtils.set(redisKey, UUID.randomUUID().toString());
        System.err.println(RedisUtils.get(redisKey)+"");
    }
}
