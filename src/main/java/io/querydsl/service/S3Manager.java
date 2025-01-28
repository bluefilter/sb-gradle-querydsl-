package io.querydsl.service;

import com.amazonaws.services.s3.AmazonS3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;


import java.util.List;
import java.util.Optional;

@Service
public class S3Manager {

    private final AmazonS3 amazonS3;

    // Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(S3Manager.class);

    public S3Manager(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * S3 버킷 생성
     *
     * @param bucketName 생성할 버킷 이름
     * @return 생성된 Bucket 객체
     */
    public Bucket createBucket(String bucketName) {
        if (amazonS3.doesBucketExistV2(bucketName)) {
            logger.warn("버킷이 이미 존재합니다: {}", bucketName);
            throw new IllegalArgumentException("Bucket already exists: " + bucketName);
        }

        logger.info("버킷 생성 요청: {}", bucketName);
        return amazonS3.createBucket(new CreateBucketRequest(bucketName));
    }

    /**
     * S3 버킷 삭제
     *
     * @param bucketName 삭제할 버킷 이름
     */
    public void deleteBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            logger.warn("존재하지 않는 버킷입니다: {}", bucketName);
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }

        logger.info("버킷 삭제 요청: {}", bucketName);
        amazonS3.deleteBucket(bucketName);
    }

    /**
     * 모든 S3 버킷 조회
     *
     * @return Bucket 객체 리스트
     */
    public List<Bucket> listBuckets() {
        List<Bucket> buckets = amazonS3.listBuckets();
        logger.info("전체 버킷 개수: {}", buckets.size());
        return buckets;
    }

    /**
     * 특정 S3 버킷 조회
     *
     * @param bucketName 조회할 버킷 이름
     * @return Optional<Bucket>
     */
    public Optional<Bucket> getBucket(String bucketName) {
        return listBuckets().stream()
                .filter(bucket -> bucket.getName().equals(bucketName))
                .findFirst();
    }

    /**
     * 버킷에 대한 존재 여부 확인
     *
     * @param bucketName 확인할 버킷 이름
     * @return 존재 여부 (true/false)
     */
    public boolean doesBucketExist(String bucketName) {
        boolean exists = amazonS3.doesBucketExistV2(bucketName);
        logger.info("버킷 존재 여부 [{}]: {}", bucketName, exists);
        return exists;
    }

}