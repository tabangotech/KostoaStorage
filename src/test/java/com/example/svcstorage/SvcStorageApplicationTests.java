package com.example.svcstorage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import io.minio.MinioClient;

@SpringBootTest
class SvcStorageApplicationTests {

    @MockBean
    private MinioClient minioClient;

    @Test
    void contextLoads() {
    }

}
