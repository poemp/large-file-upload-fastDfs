package org.poem;

import com.google.gson.Gson;
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
        Object o = RedisUtils.get("4534d8fd-0c84-44d3-93bb-fa284a1e103d");
        if(o != null){
            System.err.println(new Gson().toJson(o));
        }
    }
}
