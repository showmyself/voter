package personal.lyh.voter.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import personal.lyh.voter.other.MyConstant;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    //创建基于Caffeine的Cache Manager
    @Primary
    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        ArrayList<CaffeineCache> caches = new ArrayList<>();

        //设置可以使用的缓存类别，到时候比如使用cacheManager.getCache("name").put..进行操作

        caches.add(new CaffeineCache("example", Caffeine.newBuilder()
                .initialCapacity(100)//初始容量
                .maximumSize(1000)//最大容量
                .expireAfterWrite(10, TimeUnit.SECONDS)//过期时间
                .build()));

        caches.add(new CaffeineCache(MyConstant.CacheType_EmailCode, Caffeine.newBuilder()
                .initialCapacity(10)
//                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build()));

        caches.add(new CaffeineCache(MyConstant.CacheType_IpUrlLimit, Caffeine.newBuilder()
                .expireAfterWrite(300, TimeUnit.MILLISECONDS)
                .build()));

        caches.add(new CaffeineCache(MyConstant.CacheType_User, Caffeine.newBuilder()
                .expireAfterWrite(4, TimeUnit.MINUTES)
                .build()));
        caches.add(new CaffeineCache(MyConstant.CacheType_Post, Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build()));

        cacheManager.setCaches(caches);
        return cacheManager;
    }


}
