package com.employee.system.service;

import com.employee.system.dto.RatingScaleDTO;
import com.employee.system.entity.RatingScale;
import com.employee.system.repository.RatingScaleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingScaleServiceTest {

    @Mock
    private RatingScaleRepository ratingScaleRepository;

    @InjectMocks
    private RatingScaleService ratingScaleService;

    /* ---------- getRatingScaleById ---------- */

    @Test
    @DisplayName("Given existing id when getRatingScaleById then return DTO")
    void givenExistingId_whenGetRatingScaleById_thenReturnDTO() {
        // Arrange
        Long id = 1L;
        RatingScale entity = new RatingScale();
        entity.setId(id);
        entity.setRatingValue(5);
        entity.setRatingLabel("Excellent");
        entity.setRatingDescription("Top performance");
        entity.setColorCode("#00FF00");
        entity.setMinimumScore(90);
        entity.setMaximumScore(100);
        entity.setIsActive(true);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        RatingScaleDTO result = ratingScaleService.getRatingScaleById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(entity.getRatingValue(), result.getRatingValue());
        assertEquals(entity.getRatingLabel(), result.getRatingLabel());
        assertEquals(entity.getRatingDescription(), result.getRatingDescription());
        assertEquals(entity.getColorCode(), result.getColorCode());
        assertEquals(entity.getMinimumScore(), result.getMinimumScore());
        assertEquals(entity.getMaximumScore(), result.getMaximumScore());
        assertEquals(entity.getIsActive(), result.getIsActive());
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

    /* ---------- getRatingScaleByValue ---------- */

    @Test
    @DisplayName("Given existing value when getRatingScaleByValue then return DTO")
    void givenExistingValue_whenGetRatingScaleByValue_thenReturnDTO() {
        // Arrange
        Integer value = 3;
        RatingScale entity = new RatingScale();
        entity.setId(2L);
        entity.setRatingValue(value);
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

    /* ---------- getAllActiveRatingScales ---------- */

    @Test
    @DisplayName("Given active scales when getAllActiveRatingScales then return DTO list")
    void givenActiveScales_whenGetAllActiveRatingScales_thenReturnDTOList() {
        // Arrange
        RatingScale rs1 = new RatingScale();
        rs1.setId(1L);
        rs1.setRatingValue(1);
        RatingScale rs2 = new RatingScale();
        rs2.setId(2L);
        rs2.setRatingValue(2);
        when(ratingScaleRepository.findAllActive()).thenReturn(List.of(rs1, rs2));

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllActiveRatingScales();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(rs1.getId(), result.get(0).getId());
        assertEquals(rs2.getId(), result.get(1).getId());
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

    /* ---------- getAllRatingScales ---------- */

    @Test
    @DisplayName("Given all scales when getAllRatingScales then return DTO list")
    void givenAllScales_whenGetAllRatingScales_thenReturnDTOList() {
        // Arrange
        RatingScale rs = new RatingScale();
        rs.setId(5L);
        rs.setRatingValue(5);
        when(ratingScaleRepository.findAll()).thenReturn(List.of(rs));

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllRatingScales();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rs.getId(), result.get(0).getId());
        verify(ratingScaleRepository, times(1)).findAll();
    }

    /* ---------- createRatingScale ---------- */

    @Test
    @DisplayName("Given valid DTO when createRatingScale then return saved DTO")
    void givenValidDTO_whenCreateRatingScale_thenReturnSavedDTO() {
        // Arrange
        RatingScaleDTO dto = new RatingScaleDTO();
        dto.setRatingValue(4);
        dto.setRatingLabel("Good");
        dto.setRatingDescription("Meets expectations");
        dto.setColorCode("#FFFF00");
        dto.setMinimumScore(70);
        dto.setMaximumScore(89);
        dto.setIsActive(false);

        RatingScale savedEntity = new RatingScale();
        savedEntity.setId(10L);
        savedEntity.setRatingValue(dto.getRatingValue());
        savedEntity.setRatingLabel(dto.getRatingLabel());
        savedEntity.setRatingDescription(dto.getRatingDescription());
        savedEntity.setColorCode(dto.getColorCode());
        savedEntity.setMinimumScore(dto.getMinimumScore());
        savedEntity.setMaximumScore(dto.getMaximumScore());
        savedEntity.setIsActive(dto.getIsActive());

        when(ratingScaleRepository.save(any(RatingScale.class))).thenReturn(savedEntity);

        // Act
        RatingScaleDTO result = ratingScaleService.createRatingScale(dto);

        // Assert
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(dto.getRatingValue(), result.getRatingValue());
        assertEquals(dto.getIsActive(), result.getIsActive());

        ArgumentCaptor<RatingScale> captor = ArgumentCaptor.forClass(RatingScale.class);
        verify(ratingScaleRepository, times(1)).save(captor.capture());
        RatingScale captured = captor.getValue();
        assertEquals(dto.getRatingValue(), captured.getRatingValue());
        assertEquals(dto.getIsActive(), captured.getIsActive());
    }

    @Test
    @DisplayName("Given DTO with null isActive when createRatingScale then default isActive true")
    void givenDTOWithNullIsActive_whenCreateRatingScale_thenDefaultIsActiveTrue() {
        // Arrange
        RatingScaleDTO dto = new RatingScaleDTO();
        dto.setRatingValue(2);
        dto.setIsActive(null); // explicitly null

        RatingScale savedEntity = new RatingScale();
        savedEntity.setId(11L);
        savedEntity.setRatingValue(dto.getRatingValue());
        savedEntity.setIsActive(true); // defaulted by service

        when(ratingScaleRepository.save(any(RatingScale.class))).thenReturn(savedEntity);

        // Act
        RatingScaleDTO result = ratingScaleService.createRatingScale(dto);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsActive());
        ArgumentCaptor<RatingScale> captor = ArgumentCaptor.forClass(RatingScale.class);
        verify(ratingScaleRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getIsActive());
    }

    @Test
    @DisplayName("Given null DTO when createRatingScale then throw NullPointerException")
    void givenNullDTO_whenCreateRatingScale_thenThrowNullPointerException() {
        // Arrange
        RatingScaleDTO dto = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ratingScaleService.createRatingScale(dto));
        verifyNoInteractions(ratingScaleRepository);
    }

    /* ---------- updateRatingScale ---------- */

    @Test
    @DisplayName("Given existing id and valid DTO when updateRatingScale then return updated DTO")
    void givenExistingIdAndValidDTO_whenUpdateRatingScale_thenReturnUpdatedDTO() {
        // Arrange
        Long id = 3L;
        RatingScale existing = new RatingScale();
        existing.setId(id);
        existing.setRatingValue(1);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(existing));

        RatingScaleDTO dto = new RatingScaleDTO();
        dto.setRatingValue(5);
        dto.setRatingLabel("Outstanding");
        dto.setRatingDescription("Exceeds all expectations");
        dto.setColorCode("#FF0000");
        dto.setMinimumScore(95);
        dto.setMaximumScore(100);
        dto.setIsActive(false);

        RatingScale saved = new RatingScale();
        saved.setId(id);
        saved.setRatingValue(dto.getRatingValue());
        saved.setRatingLabel(dto.getRatingLabel());
        saved.setRatingDescription(dto.getRatingDescription());
        saved.setColorCode(dto.getColorCode());
        saved.setMinimumScore(dto.getMinimumScore());
        saved.setMaximumScore(dto.getMaximumScore());
        saved.setIsActive(dto.getIsActive());

        when(ratingScaleRepository.save(any(RatingScale.class))).thenReturn(saved);

        // Act
        RatingScaleDTO result = ratingScaleService.updateRatingScale(id, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getRatingValue(), result.getRatingValue());
        assertEquals(dto.getIsActive(), result.getIsActive());

        verify(ratingScaleRepository, times(1)).findById(id);
        ArgumentCaptor<RatingScale> captor = ArgumentCaptor.forClass(RatingScale.class);
        verify(ratingScaleRepository, times(1)).save(captor.capture());
        RatingScale updatedEntity = captor.getValue();
        assertEquals(dto.getRatingValue(), updatedEntity.getRatingValue());
        assertEquals(dto.getIsActive(), updatedEntity.getIsActive());
    }

    @Test
    @DisplayName("Given null DTO when updateRatingScale then throw NullPointerException")
    void givenNullDTO_whenUpdateRatingScale_thenThrowNullPointerException() {
        // Arrange
        Long id = 4L;
        RatingScale existing = new RatingScale();
        existing.setId(id);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ratingScaleService.updateRatingScale(id, null));
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given non‑existing id when updateRatingScale then throw RuntimeException")
    void givenNonExistingId_whenUpdateRatingScale_thenThrowRuntimeException() {
        // Arrange
        Long id = 999L;
        RatingScaleDTO dto = new RatingScaleDTO();
        dto.setRatingValue(3);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ratingScaleService.updateRatingScale(id, dto));
        assertTrue(ex.getMessage().contains("Rating scale not found with id"));
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, never()).save(any());
    }

    /* ---------- deleteRatingScale ---------- */

    @Test
    @DisplayName("Given existing id when deleteRatingScale then repository delete called")
    void givenExistingId_whenDeleteRatingScale_thenRepositoryDeleteCalled() {
        // Arrange
        Long id = 5L;
        RatingScale existing = new RatingScale();
        existing.setId(id);
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.of(existing));

        // Act
        ratingScaleService.deleteRatingScale(id);

        // Assert
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, times(1)).delete(existing);
    }

    @Test
    @DisplayName("Given non‑existing id when deleteRatingScale then throw RuntimeException")
    void givenNonExistingId_whenDeleteRatingScale_thenThrowRuntimeException() {
        // Arrange
        Long id = 777L;
        when(ratingScaleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ratingScaleService.deleteRatingScale(id));
        assertTrue(ex.getMessage().contains("Rating scale not found with id"));
        verify(ratingScaleRepository, times(1)).findById(id);
        verify(ratingScaleRepository, never()).delete(any());
    }
}