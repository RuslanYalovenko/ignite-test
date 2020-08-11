/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com;

import com.entity.Diff;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicyFactory;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.Map;

import static com.util.Constants.DIFF_CACHE;
import static com.util.Constants.DOCUMENTS_CACHE;

@Configuration
public class StorageConfig {

    @Bean
    public Ignite ignite() {
        IgniteConfiguration config = new IgniteConfiguration();

        config.setIgniteInstanceName("diffNode");

        CacheConfiguration documentsCache = new CacheConfiguration(DOCUMENTS_CACHE);
        documentsCache.setIndexedTypes(Long.class, Map.class);
        documentsCache.setOnheapCacheEnabled(true);
        documentsCache.setEvictionPolicyFactory(new LruEvictionPolicyFactory(10000));
        documentsCache.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_DAY));

        CacheConfiguration diffCache = new CacheConfiguration(DIFF_CACHE);
        diffCache.setIndexedTypes(Long.class, Diff.class);
        diffCache.setOnheapCacheEnabled(true);
        diffCache.setEvictionPolicyFactory(new LruEvictionPolicyFactory(10000));

        config.setCacheConfiguration(documentsCache, diffCache);

        return Ignition.start(config);
    }

}
