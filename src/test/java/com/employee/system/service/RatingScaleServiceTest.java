package com.employee.system.service;

import com.employee.system.dto.RatingScaleDTO;
import com.employee.system.entity.RatingScale;
import com.employee.system.repository.RatingScaleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingScaleServiceTest {

    @Mock
    private RatingScaleRepository ratingScaleRepository;

    @InjectMocks
    private RatingScaleService ratingScaleService;

    private RatingScale sampleEntity(Long id, Integer value, String label, Boolean active) {
        RatingScale rs = new RatingScale();
        rs.setId(id);
        rs.setRatingValue(value);
        rs.setRatingLabel(label);
        rs.setRatingDescription("desc-" + value);
        rs.setColorCode("#FFFFFF");
        rs.setMinimumScore(0);
        rs.setMaximumScore(100);
        rs.setIsActive(active);
        return rs;
    }

    private RatingScaleDTO sampleDto(Long id, Integer value, String label, Boolean active) {
        RatingScaleDTO dto = new RatingScaleDTO();
        dto.setId(id);
        dto.setRatingValue(value);
        dto.setRatingLabel(label);
        dto.setRatingDescription("desc-" + value);
        dto.setColorCode("#FFFFFF");
        dto.setMinimumScore(0);
        dto.setMaximumScore(100);
        dto.setIsActive(active);
        return dto;
    }

    @Test
    @DisplayName("Given existing id when getRatingScaleById then return DTO")
    void givenExistingId_whenGetRatingScaleById_thenReturnDto() {
        // Arrange
        Long id = 1L;
        RatingScale entity = sampleEntity(id, 5, "Excellent", true);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        RatingScaleDTO result = ratingScaleService.getRatingScaleById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(entity.getRatingValue(), result.getRatingValue());
        verify(ratingScaleRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given non‑existing id when getRatingScaleById then throw RuntimeException")
    void givenNonExistingId_whenGetRatingScaleById_thenThrowRuntimeException() {
        // Arrange
        Long id = 99L;
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ratingScaleService.getRatingScaleById(id));
        assertTrue(ex.getMessage().contains("Rating scale not found with id"));
        verify(ratingScaleRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given existing value when getRatingScaleByValue then return DTO")
    void givenExistingValue_whenGetRatingScaleByValue_thenReturnDto() {
        // Arrange
        Integer value = 3;
        RatingScale entity = sampleEntity(2L, value, "Good", true);
        when(ratingScaleRepository.findByRatingValue(value)).thenReturn(Optional.of(entity));

        // Act
        RatingScaleDTO result = ratingScaleService.getRatingScaleByValue(value);

        // Assert
        assertNotNull(result);
        assertEquals(value, result.getRatingValue());
        verify(ratingScaleRepository, times(1)).findByRatingValue(value);
    }

    @Test
    @DisplayName("Given non‑existing value when getRatingScaleByValue then throw RuntimeException")
    void givenNonExistingValue_whenGetRatingScaleByValue_thenThrowRuntimeException() {
        // Arrange
        Integer value = 10;
        when(ratingScaleRepository.findByRatingValue(value)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ratingScaleService.getRatingScaleByValue(value));
        assertTrue(ex.getMessage().contains("Rating scale not found with value"));
        verify(ratingScaleRepository, times(1)).findByRatingValue(value);
    }

    @Test
    @DisplayName("Given active scales when getAllActiveRatingScales then return DTO list")
    void givenActiveScales_whenGetAllActiveRatingScales_thenReturnDtoList() {
        // Arrange
        RatingScale rs1 = sampleEntity(1L, 1, "Poor", true);
        RatingScale rs2 = sampleEntity(2L, 2, "Fair", true);
        when(ratingScaleRepository.findAllActive()).thenReturn(List.of(rs1, rs2));

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllActiveRatingScales();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(1L)));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(2L)));
        verify(ratingScaleRepository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Given no active scales when getAllActiveRatingScales then return empty list")
    void givenNoActiveScales_whenGetAllActiveRatingScales_thenReturnEmptyList() {
        // Arrange
        when(ratingScaleRepository.findAllActive()).thenReturn(Collections.emptyList());

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllActiveRatingScales();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ratingScaleRepository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Given all scales when getAllRatingScales then return DTO list")
    void givenAllScales_whenGetAllRatingScales_thenReturnDtoList() {
        // Arrange
        RatingScale rs1 = sampleEntity(1L, 1, "Poor", true);
        RatingScale rs2 = sampleEntity(2L, 2, "Fair", false);
        when(ratingScaleRepository.findAll()).thenReturn(List.of(rs1, rs2));

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllRatingScales();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Poor", result.get(0).getRatingLabel());
        assertEquals("Fair", result.get(1).getRatingLabel());
        verify(ratingScaleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given no scales when getAllRatingScales then return empty list")
    void givenNoScales_whenGetAllRatingScales_thenReturnEmptyList() {
        // Arrange
        when(ratingScaleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllRatingScales();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ratingScaleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given valid DTO when createRatingScale then return saved DTO with default active true")
    void givenValidDto_whenCreateRatingScale_thenReturnSavedDtoWithDefaults() {
        // Arrange
        RatingScaleDTO inputDto = sampleDto(null, 4, "Very Good", null);
        RatingScale savedEntity = sampleEntity(10L, 4, "Very Good", true);
        when(ratingScaleRepository.save(any(RatingScale.class))).thenReturn(savedEntity);

        // Act
        RatingScaleDTO result = ratingScaleService.createRatingScale(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertTrue(result.getIsActive());
        verify(ratingScaleRepository, times(1)).save(any(RatingScale.class));
    }

    @Test
    @DisplayName("Given DTO with null isActive when createRatingScale then default isActive true")
    void givenDtoWithNullIsActive_whenCreateRatingScale_thenDefaultIsActiveTrue() {
        // Arrange
        RatingScaleDTO inputDto = sampleDto(null, 2, "Fair", null);
        RatingScale captured = new RatingScale();
        when(ratingScaleRepository.save(any(RatingScale.class))).thenAnswer(invocation -> {
            RatingScale arg = invocation.getArgument(0);
            captured.setId(20L);
            captured.setRatingValue(arg.getRatingValue());
            captured.setIsActive(arg.getIsActive());
            return captured;
        });

        // Act
        RatingScaleDTO result = ratingScaleService.createRatingScale(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(20L, result.getId());
        assertTrue(result.getIsActive());
        assertEquals(2, result.getRatingValue());
        verify(ratingScaleRepository, times(1)).save(any(RatingScale.class));
    }

    @Test
    @DisplayName("Given existing id when updateRatingScale then return updated DTO")
    void givenExistingId_whenUpdateRatingScale_thenReturnUpdatedDto() {
        // Arrange
        Long id = 5L;
        RatingScale existing = sampleEntity(id, 1, "Poor", true);
        RatingScaleDTO updateDto = sampleDto(null, 5, "Excellent", false);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ratingScaleRepository.save(any(RatingScale.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RatingScaleDTO result = ratingScaleService.updateRatingScale(id, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(5, result.getRatingValue());
        assertEquals("Excellent", result.getRatingLabel());
        assertFalse(result.getIsActive());
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Given non‑existing id when updateRatingScale then throw RuntimeException")
    void givenNonExistingId_whenUpdateRatingScale_thenThrowRuntimeException() {
        // Arrange
        Long id = 99L;
        RatingScaleDTO dto = sampleDto(null, 3, "Good", true);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ratingScaleService.updateRatingScale(id, dto));
        assertTrue(ex.getMessage().contains("Rating scale not found with id"));
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given existing id when deleteRatingScale then repository delete is invoked")
    void givenExistingId_whenDeleteRatingScale_thenRepositoryDeleteInvoked() {
        // Arrange
        Long id = 7L;
        RatingScale entity = sampleEntity(id, 2, "Fair", true);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(ratingScaleRepository).delete(entity);

        // Act
        ratingScaleService.deleteRatingScale(id);

        // Assert
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Given non‑existing id when deleteRatingScale then throw RuntimeException")
    void givenNonExistingId_whenDeleteRatingScale_thenThrowRuntimeException() {
        // Arrange
        Long id = 88L;
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ratingScaleService.deleteRatingScale(id));
        assertTrue(ex.getMessage().contains("Rating scale not found with id"));
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Given null DTO when createRatingScale then throw NullPointerException")
    void givenNullDto_whenCreateRatingScale_thenThrowNullPointerException() {
        // Arrange
        RatingScaleDTO nullDto = null;

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> ratingScaleService.createRatingScale(nullDto));
        verifyNoInteractions(ratingScaleRepository);
    }
}