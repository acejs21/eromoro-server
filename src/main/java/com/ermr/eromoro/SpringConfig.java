package com.ermr.eromoro;

import com.ermr.eromoro.util.NcpObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public NcpObjectStorageClient ncpObjectStorageClient(
            @Value("${ncp.object-storage.endpoint}") String endpoint,
            @Value("${ncp.object-storage.region}") String region,
            @Value("${ncp.object-storage.access-key}") String accessKey,
            @Value("${ncp.object-storage.secret-key}") String secretKey,
            @Value("${ncp.object-storage.bucket}") String bucket
    ) {
        return new NcpObjectStorageClient(endpoint, region, accessKey, secretKey, bucket);
    }
}
