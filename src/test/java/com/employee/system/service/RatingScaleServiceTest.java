package com.employee.system.service;

import com.employee.system.dto.RatingScaleDTO;
import com.employee.system.entity.RatingScale;
import com.employee.system.repository.RatingScaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingScaleServiceTest {

    @Mock
    private RatingScaleRepository ratingScaleRepository;

    @InjectMocks
    private RatingScaleService ratingScaleService;

    private RatingScale ratingScale;
    private RatingScaleDTO ratingScaleDTO;

    @BeforeEach
    public void setUp() {
        ratingScale = new RatingScale();
        ratingScale.setId(1L);
        ratingScale.setRatingValue(5);
        ratingScale.setRatingLabel("Excellent");
        ratingScale.setRatingDescription("Top performance");
        ratingScale.setColorCode("green");
        ratingScale.setMinimumScore(90);
        ratingScale.setMaximumScore(100);
        ratingScale.setIsActive(true);

        ratingScaleDTO = new RatingScaleDTO();
        ratingScaleDTO.setId(1L);
        ratingScaleDTO.setRatingValue(5);
        ratingScaleDTO.setRatingLabel("Excellent");
        ratingScaleDTO.setRatingDescription("Top performance");
        ratingScaleDTO.setColorCode("green");
        ratingScaleDTO.setMinimumScore(90);
        ratingScaleDTO.setMaximumScore(100);
        ratingScaleDTO.setIsActive(true);
    }

    @Test
    @DisplayName("givenValidId_whenGetRatingScaleById_thenReturnRatingScaleDTO")
    public void givenValidId_whenGetRatingScaleById_thenReturnRatingScaleDTO() {
        // Arrange
        when(ratingScaleRepository.findById(1L)).thenReturn(Optional.of(ratingScale));

        // Act
        RatingScaleDTO result = ratingScaleService.getRatingScaleById(1L);

        // Assert
        assertEquals(ratingScaleDTO, result);
        verify(ratingScaleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenNonExistingId_whenGetRatingScaleById_thenThrowRuntimeException")
    public void givenNonExistingId_whenGetRatingScaleById_thenThrowRuntimeException() {
        // Arrange
        when(ratingScaleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ratingScaleService.getRatingScaleById(1L));
        verify(ratingScaleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidValue_whenGetRatingScaleByValue_thenReturnRatingScaleDTO")
    public void givenValidValue_whenGetRatingScaleByValue_thenReturnRatingScaleDTO() {
        // Arrange
        when(ratingScaleRepository.findByRatingValue(5)).thenReturn(Optional.of(ratingScale));

        // Act
        RatingScaleDTO result = ratingScaleService.getRatingScaleByValue(5);

        // Assert
        assertEquals(ratingScaleDTO, result);
        verify(ratingScaleRepository, times(1)).findByRatingValue(5);
    }

    @Test
    @DisplayName("givenNonExistingValue_whenGetRatingScaleByValue_thenThrowRuntimeException")
    public void givenNonExistingValue_whenGetRatingScaleByValue_thenThrowRuntimeException() {
        // Arrange
        when(ratingScaleRepository.findByRatingValue(5)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ratingScaleService.getRatingScaleByValue(5));
        verify(ratingScaleRepository, times(1)).findByRatingValue(5);
    }

    @Test
    @DisplayName("givenAllActiveRatingScales_whenGetAllActiveRatingScales_thenReturnRatingScaleDTOList")
    public void givenAllActiveRatingScales_whenGetAllActiveRatingScales_thenReturnRatingScaleDTOList() {
        // Arrange
        List<RatingScale> ratingScales = Arrays.asList(ratingScale);
        when(ratingScaleRepository.findAllActive()).thenReturn(ratingScales);

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllActiveRatingScales();

        // Assert
        assertEquals(1, result.size());
        assertEquals(ratingScaleDTO, result.get(0));
        verify(ratingScaleRepository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("givenAllRatingScales_whenGetAllRatingScales_thenReturnRatingScaleDTOList")
    public void givenAllRatingScales_whenGetAllRatingScales_thenReturnRatingScaleDTOList() {
        // Arrange
        List<RatingScale> ratingScales = Arrays.asList(ratingScale);
        when(ratingScaleRepository.findAll()).thenReturn(ratingScales);

        // Act
        List<RatingScaleDTO> result = ratingScaleService.getAllRatingScales();

        // Assert
        assertEquals(1, result.size());
        assertEquals(ratingScaleDTO, result.get(0));
        verify(ratingScaleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("givenValidDTO_whenCreateRatingScale_thenReturnCreatedRatingScaleDTO")
    public void givenValidDTO_whenCreateRatingScale_thenReturnCreatedRatingScaleDTO() {
        // Arrange
        when(ratingScaleRepository.save(any(RatingScale.class))).thenReturn(ratingScale);

        // Act
        RatingScaleDTO result = ratingScaleService.createRatingScale(ratingScaleDTO);

        // Assert
        assertEquals(ratingScaleDTO, result);
        verify(ratingScaleRepository, times(1)).save(any(RatingScale.class));
    }

    @Test
    @DisplayName("givenNullDTO_whenCreateRatingScale_thenThrowIllegalArgumentException")
    public void givenNullDTO_whenCreateRatingScale_thenThrowIllegalArgumentException() {
        // Arrange
        RatingScaleDTO nullDTO = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ratingScaleService.createRatingScale(nullDTO));
        verify(ratingScaleRepository, never()).save(any(RatingScale.class));
    }

    @Test
    @DisplayName("givenValidDTO_whenUpdateRatingScale_thenReturnUpdatedRatingScaleDTO")
    public void givenValidDTO_whenUpdateRatingScale_thenReturnUpdatedRatingScaleDTO() {
        // Arrange
        when(ratingScaleRepository.findById(1L)).thenReturn(Optional.of(ratingScale));
        when(ratingScaleRepository.save(any(RatingScale.class))).thenReturn(ratingScale);

        // Act
        RatingScaleDTO result = ratingScaleService.updateRatingScale(1L, ratingScaleDTO);

        // Assert
        assertEquals(ratingScaleDTO, result);
        verify(ratingScaleRepository, times(1)).findById(1L);
        verify(ratingScaleRepository, times(1)).save(any(RatingScale.class));
    }

    @Test
    @DisplayName("givenNonExistingId_whenUpdateRatingScale_thenThrowRuntimeException")
    public void givenNonExistingId_whenUpdateRatingScale_thenThrowRuntimeException() {
        // Arrange
        when(ratingScaleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ratingScaleService.updateRatingScale(1L, ratingScaleDTO));
        verify(ratingScaleRepository, times(1)).findById(1L);
        verify(ratingScaleRepository, never()).save(any(RatingScale.class));
    }

    @Test
    @DisplayName("givenValidId_whenDeleteRatingScale_thenNoException")
    public void givenValidId_whenDeleteRatingScale_thenNoException() {
        // Arrange
        when(ratingScaleRepository.findById(1L)).thenReturn(Optional.of(ratingScale));

        // Act & Assert
        assertDoesNotThrow(() -> ratingScaleService.deleteRatingScale(1L));
        verify(ratingScaleRepository, times(1)).findById(1L);
        verify(ratingScaleRepository, times(1)).delete(any(RatingScale.class));
    }

    @Test
    @DisplayName("givenNonExistingId_whenDeleteRatingScale_thenThrowRuntimeException")
    public void givenNonExistingId_whenDeleteRatingScale_thenThrowRuntimeException() {
        // Arrange
        when(ratingScaleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ratingScaleService.deleteRatingScale(1L));
        verify(ratingScaleRepository, times(1)).findById(1L);
        verify(ratingScaleRepository, never()).delete(any(RatingScale.class));
    }
}