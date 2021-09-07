package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.net.URISyntaxException;

@Configuration
@EnableCaching
public class CacheConfig {

    protected String defaultEhCacheFile = "ehcache.xml";

    @Bean(name = "cacheManager")
    public CacheManager cacheManager() throws IOException, URISyntaxException {
        CachingProvider cachingProvider = Caching.getCachingProvider();

        //default cache
        ClassPathResource cachePathResource = new ClassPathResource(defaultEhCacheFile);

        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager(
                cachePathResource.getURL().toURI(),
                this.getClass().getClassLoader()
        );

        return new JCacheCacheManager(cacheManager);
    }
}
