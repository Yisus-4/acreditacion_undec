package com.undec.acreditacion.infrastructure.storage;

import com.undec.acreditacion.domain.exceptions.ArchivoInvalidoException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MinioDocumentStorageAdapterTest {

    private MinioClient minioClient;
    private MinioDocumentStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        minioClient = mock(MinioClient.class);
        adapter = new MinioDocumentStorageAdapter(
                minioClient,
                "instituciones-logos",
                "http://localhost:9000"
        );
    }

    @Test
    @DisplayName("uploadFile crea bucket si no existe y sube archivo")
    void uploadFileCreaBucketYGuarda() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        String path = adapter.uploadFile(new byte[]{1, 2, 3}, "logo.png", "image/png");

        assertNotNull(path);
        assertTrue(path.endsWith(".png"));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).setBucketPolicy(any(SetBucketPolicyArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("uploadFile no crea bucket si ya existe")
    void uploadFileNoCreaBucketSiExiste() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        String path = adapter.uploadFile(new byte[]{1, 2, 3}, "logo.jpeg", "image/jpeg");

        assertNotNull(path);
        assertTrue(path.endsWith(".jpeg"));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, never()).setBucketPolicy(any(SetBucketPolicyArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("uploadFile con datos vacíos lanza ArchivoInvalidoException")
    void uploadFileDatosVaciosLanzaExcepcion() {
        assertThrows(ArchivoInvalidoException.class, () ->
                adapter.uploadFile(new byte[0], "logo.png", "image/png"));
        assertThrows(ArchivoInvalidoException.class, () ->
                adapter.uploadFile(null, "logo.png", "image/png"));
    }

    @Test
    @DisplayName("getFileUrl formatea la URL con endpoint y bucket")
    void getFileUrlFormateaCorrectamente() {
        String url = adapter.getFileUrl("test-logo.png");
        assertEquals("http://localhost:9000/instituciones-logos/test-logo.png", url);
    }

    @Test
    @DisplayName("deleteFile delega en removeObject de MinIO")
    void deleteFileDelega() throws Exception {
        adapter.deleteFile("test-logo.png");
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }
}
