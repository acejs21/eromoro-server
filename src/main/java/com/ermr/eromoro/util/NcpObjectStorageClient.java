package com.ermr.eromoro.util;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@Slf4j
public class NcpObjectStorageClient {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;

    public NcpObjectStorageClient(
            @Value("${ncp.object-storage.endpoint}") String endpoint,
            @Value("${ncp.object-storage.region}") String region,
            @Value("${ncp.object-storage.access-key}") String accessKey,
            @Value("${ncp.object-storage.secret-key}") String secretKey,
            @Value("${ncp.object-storage.bucket}") String bucket
    ) {
        this.bucket = bucket;

        var creds = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));

        // Netty 클라이언트
        SdkAsyncHttpClient netty = NettyNioAsyncHttpClient.builder().build();

        // Path-Style Access 필수(일반적으로 NCP에서 필요)
        S3Configuration s3conf = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        Region sdkRegion = Region.of(region);

        this.s3 = S3Client.builder()
                .region(sdkRegion)
                .credentialsProvider(creds)
                .serviceConfiguration(s3conf)
                .endpointOverride(URI.create(endpoint))
                .build();

        this.presigner = S3Presigner.builder()
                .region(sdkRegion)
                .credentialsProvider(creds)
                .endpointOverride(URI.create(endpoint))
                .build();
    }

    /** 파일 업로드 (바이트/스트림) */
    public PutObjectResponse putObject(String key, byte[] bytes, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        return s3.putObject(req, RequestBody.fromBytes(bytes));
    }

    public PutObjectResponse putObject(String key, InputStream is, long contentLength, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        return s3.putObject(req, RequestBody.fromInputStream(is, contentLength));
    }

    /** 로컬 파일 업로드 */
    public PutObjectResponse putObject(String key, Path file, String contentType) throws Exception {
        return putObject(key, Files.readAllBytes(file), contentType);
    }

    /** 객체 메타 조회 */
    public HeadObjectResponse headObject(String key) {
        HeadObjectRequest req = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3.headObject(req);
    }

    /** 다운로드(바이트) */
    public byte[] getObjectBytes(String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> resp = s3.getObjectAsBytes(req);
        return resp.asByteArray();
    }

    /** 다운로드(로컬 저장) */
    public void downloadTo(String key, Path target) throws Exception {
        byte[] data = getObjectBytes(key);
        Files.createDirectories(target.getParent());
        Files.write(target, data);
    }

    /** 삭제 */
    public void deleteObject(String key) {
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3.deleteObject(req);
    }

    /** 리스트 (prefix로 폴더처럼 조회) */
    public List<S3Object> list(String prefix, int maxKeys) {
        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix == null ? "" : prefix)
                .maxKeys(maxKeys <= 0 ? 1000 : maxKeys)
                .build();
        return s3.listObjectsV2(req).contents();
    }

    /** 사전서명 URL(다운로드용: 지정 시간만 공개 링크) */
    public String presignedGetUrl(String key, Duration ttl) {
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest preq = GetObjectPresignRequest.builder()
                .signatureDuration(ttl == null ? Duration.ofMinutes(10) : ttl)
                .getObjectRequest(get)
                .build();

        return presigner.presignGetObject(preq).url().toString();
    }

    /** 퍼블릭 URL(버킷이 public-read일 때) */
    public String publicUrl(String key) {
        S3Utilities utils = s3.utilities();
        return utils.getUrl(b -> b.bucket(bucket).key(key)).toString();
    }
}
