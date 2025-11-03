package com.arnas.klatrebackend.service;

import com.arnas.klatrebackend.interfaces.repositories.ImageRepositoryInterface;
import com.arnas.klatrebackend.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private ImageRepositoryInterface imageRepository;

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "uploadDir", tempDir.toString());
    }

    @Test
    void testStoreImageMetadata_Success() {
        long userId = 1L;
        String contentType = "image/jpeg";
        long size = 1024L;
        String expectedImageId = "img-123";

        when(imageRepository.storeImageMetaData(contentType, size, userId))
            .thenReturn(expectedImageId);

        String result = imageService.storeImageMetadata(userId, contentType, size);

        assertEquals(expectedImageId, result);
        verify(imageRepository).storeImageMetaData(contentType, size, userId);
    }

    @Test
    void testGetImageMetadata_Success() {
        String imageId = "img-123";
        var mockImage = mock(com.arnas.klatrebackend.dataclasses.Image.class);

        when(imageRepository.getImageMetadata(imageId)).thenReturn(mockImage);

        var result = imageService.getImageMetadata(imageId);

        assertNotNull(result);
        assertEquals(mockImage, result);
        verify(imageRepository).getImageMetadata(imageId);
    }

    @Test
    void testGetImageMetadata_NotFound() {
        String imageId = "img-999";

        when(imageRepository.getImageMetadata(imageId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            imageService.getImageMetadata(imageId);
        });
    }

    @Test
    void testGetImageMetadataById_Success() {
        String imageId = "img-123";
        var mockImage = mock(com.arnas.klatrebackend.dataclasses.Image.class);

        when(imageRepository.getImageMetadata(imageId)).thenReturn(mockImage);

        var result = imageService.getImageMetadataById(imageId);

        assertNotNull(result);
        assertEquals(mockImage, result);
        verify(imageRepository).getImageMetadata(imageId);
    }

    @Test
    void testGetImageMetadataById_NotFound() {
        String imageId = "img-999";

        when(imageRepository.getImageMetadata(imageId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            imageService.getImageMetadataById(imageId);
        });
    }

    @Test
    void testStoreImageFile_Success() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        long userId = 1L;
        String imageId = "img-123";
        String contentType = "image/png";
        long size = 2048L;

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getSize()).thenReturn(size);
        when(imageRepository.storeImageMetaData(contentType, size, userId))
            .thenReturn(imageId);

        String result = imageService.storeImageFile(mockFile, userId);

        assertEquals(imageId, result);
        verify(imageRepository).storeImageMetaData(contentType, size, userId);
        verify(mockFile).transferTo(any(File.class));
    }

    @Test
    void testStoreImageFile_EmptyFile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        long userId = 1L;

        when(mockFile.isEmpty()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            imageService.storeImageFile(mockFile, userId);
        });

        verify(imageRepository, never()).storeImageMetaData(anyString(), anyLong(), anyLong());
    }

    @Test
    void testGetImage_Success() {
        String imageId = "img-123";
        var mockImage = mock(com.arnas.klatrebackend.dataclasses.Image.class);

        when(imageRepository.getImageById(imageId)).thenReturn(mockImage);

        var result = imageService.getImage(imageId);

        assertNotNull(result);
        assertEquals(mockImage, result);
        verify(imageRepository).getImageById(imageId);
    }

    @Test
    void testGetImage_NotFound() {
        String imageId = "img-999";

        when(imageRepository.getImageById(imageId)).thenReturn(null);

        var result = imageService.getImage(imageId);

        assertNull(result);
        verify(imageRepository).getImageById(imageId);
    }

    @Test
    void testDeleteImage_Success() {
        String imageId = "img-123";

        imageService.deleteImage(imageId);

        verify(imageRepository).deleteImage(imageId);
    }

    @Test
    void testInit_CreatesDirectory() {
        String newUploadDir = tempDir.resolve("new-upload-dir").toString();
        ReflectionTestUtils.setField(imageService, "uploadDir", newUploadDir);

        assertDoesNotThrow(() -> {
            imageService.init();
        });

        assertTrue(new File(newUploadDir).exists());
    }

    @Test
    void testInit_DirectoryAlreadyExists() {
        assertDoesNotThrow(() -> {
            imageService.init();
            imageService.init();
        });

        assertTrue(new File(tempDir.toString()).exists());
    }
}