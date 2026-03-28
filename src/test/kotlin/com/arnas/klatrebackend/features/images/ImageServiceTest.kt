package com.arnas.klatrebackend.features.images

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class ImageServiceTest {

    @Mock
    private lateinit var imageRepository: ImageRepository

    @InjectMocks
    private lateinit var imageService: ImageServiceDefault

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(imageService, "uploadDir", tempDir.toString())
    }

    @Test
    fun testStoreImageMetadata_Success() {
        val userId = 1L
        val contentType = "image/jpeg"
        val size = 1024L
        val expectedImageId = "img-123"

        `when`(imageRepository.storeImageMetaData(contentType, size, userId))
            .thenReturn(expectedImageId)

        val result = imageService.storeImageMetadata(userId, contentType, size)

        assertEquals(expectedImageId, result)
        verify(imageRepository).storeImageMetaData(contentType, size, userId)
    }

    @Test
    fun testGetImageMetadata_Success() {
        val imageId = "img-123"
        val mockImage = mock(Image::class.java)

        `when`(imageRepository.getImageMetadata(imageId)).thenReturn(mockImage)

        val result = imageService.getImageMetadata(imageId)

        assertNotNull(result)
        assertEquals(mockImage, result)
        verify(imageRepository).getImageMetadata(imageId)
    }

    @Test
    fun testGetImageMetadata_NotFound() {
        val imageId = "img-999"

        `when`(imageRepository.getImageMetadata(imageId)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            imageService.getImageMetadata(imageId)
        }
    }

    @Test
    fun testGetImageMetadataById_Success() {
        val imageId = "img-123"
        val mockImage = mock(Image::class.java)

        `when`(imageRepository.getImageMetadata(imageId)).thenReturn(mockImage)

        val result = imageService.getImageMetadataById(imageId)

        assertNotNull(result)
        assertEquals(mockImage, result)
        verify(imageRepository).getImageMetadata(imageId)
    }

    @Test
    fun testGetImageMetadataById_NotFound() {
        val imageId = "img-999"

        `when`(imageRepository.getImageMetadata(imageId)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            imageService.getImageMetadataById(imageId)
        }
    }

    @Test
    fun testStoreImageFile_Success() {
        val mockFile = mock(MultipartFile::class.java)
        val userId = 1L
        val imageId = "img-123"
        val contentType = "image/png"
        val size = 2048L

        `when`(mockFile.isEmpty).thenReturn(false)
        `when`(mockFile.contentType).thenReturn(contentType)
        `when`(mockFile.size).thenReturn(size)
        `when`(imageRepository.storeImageMetaData(contentType, size, userId))
            .thenReturn(imageId)

        val result = imageService.storeImageFile(mockFile, userId)

        assertEquals(imageId, result)
        verify(imageRepository).storeImageMetaData(contentType, size, userId)
        verify(mockFile).transferTo(any(File::class.java))
    }

    @Test
    fun testStoreImageFile_EmptyFile() {
        val mockFile = mock(MultipartFile::class.java)
        val userId = 1L

        `when`(mockFile.isEmpty).thenReturn(true)

        assertThrows(RuntimeException::class.java) {
            imageService.storeImageFile(mockFile, userId)
        }

        verify(imageRepository, never()).storeImageMetaData(anyString(), anyLong(), anyLong())
    }

    @Test
    fun testGetImage_Success() {
        val imageId = "img-123"
        val mockImage = mock(Image::class.java)

        `when`(imageRepository.getImageById(imageId)).thenReturn(mockImage)

        val result = imageService.getImage(imageId)

        assertNotNull(result)
        assertEquals(mockImage, result)
        verify(imageRepository).getImageById(imageId)
    }

    @Test
    fun testGetImage_NotFound() {
        val imageId = "img-999"

        `when`(imageRepository.getImageById(imageId)).thenReturn(null)

        val result = imageService.getImage(imageId)

        assertNull(result)
        verify(imageRepository).getImageById(imageId)
    }

    @Test
    fun testDeleteImage_Success() {
        val imageId = "img-123"

        imageService.deleteImage(imageId)

        verify(imageRepository).deleteImage(imageId)
    }

    @Test
    fun testInit_CreatesDirectory() {
        val newUploadDir = tempDir.resolve("new-upload-dir").toString()
        ReflectionTestUtils.setField(imageService, "uploadDir", newUploadDir)

        assertDoesNotThrow { }

        assertTrue(File(newUploadDir).exists())
    }

    @Test
    fun testInit_DirectoryAlreadyExists() {
        assertDoesNotThrow { }

        assertTrue(File(tempDir.toString()).exists())
    }
}

