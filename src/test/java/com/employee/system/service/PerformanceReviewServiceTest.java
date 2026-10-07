package com.employee.system.service;

import com.employee.system.dto.PerformanceReviewDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.PerformanceReview;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.PerformanceReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerformanceReviewServiceTest {

    @Mock
    private PerformanceReviewRepository reviewRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PerformanceReviewService performanceReviewService;

    private Employee employee;
    private Employee manager;
    private PerformanceReviewDTO reviewDTO;
    private PerformanceReview performanceReview;

    @BeforeEach
    public void setUp() {
        employee = new Employee(1L, "John", "Doe", "john.doe@example.com");
        manager = new Employee(2L, "Jane", "Smith", "jane.smith@example.com");
        reviewDTO = new PerformanceReviewDTO();
        reviewDTO.setEmployeeId(1L);
        reviewDTO.setReviewedById(2L);
        reviewDTO.setReviewPeriod("Q1");
        reviewDTO.setReviewDate(LocalDate.now());
        reviewDTO.setOverallRating(4);
        reviewDTO.setTechnicalSkillsRating(4);
        reviewDTO.setBehavioralRating(4);
        reviewDTO.setLeadershipRating(4);
        reviewDTO.setTeamworkRating(4);
        reviewDTO.setStrengths("Good communication");
        reviewDTO.setAreasForImprovement("None");
        reviewDTO.setComments("Great performance");
        reviewDTO.setStatus("SUBMITTED");

        performanceReview = new PerformanceReview();
        performanceReview.setId(1L);
        performanceReview.setEmployee(employee);
        performanceReview.setReviewedBy(manager);
        performanceReview.setReviewPeriod(reviewDTO.getReviewPeriod());
        performanceReview.setReviewDate(reviewDTO.getReviewDate());
        performanceReview.setOverallRating(reviewDTO.getOverallRating());
        performanceReview.setTechnicalSkillsRating(reviewDTO.getTechnicalSkillsRating());
        performanceReview.setBehavioralRating(reviewDTO.getBehavioralRating());
        performanceReview.setLeadershipRating(reviewDTO.getLeadershipRating());
        performanceReview.setTeamworkRating(reviewDTO.getTeamworkRating());
        performanceReview.setStrengths(reviewDTO.getStrengths());
        performanceReview.setAreasForImprovement(reviewDTO.getAreasForImprovement());
        performanceReview.setComments(reviewDTO.getComments());
        performanceReview.setStatus(reviewDTO.getStatus());
    }

    @Test
    @DisplayName("givenValidInput_whenCreateReview_thenReturnSuccess")
    public void givenValidInput_whenCreateReview_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(reviewRepository.save(any(PerformanceReview.class))).thenReturn(performanceReview);

        // Act
        PerformanceReviewDTO result = performanceReviewService.createReview(reviewDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(2L, result.getReviewedById());
        assertEquals("Q1", result.getReviewPeriod());
        assertEquals(LocalDate.now(), result.getReviewDate());
        assertEquals(4, result.getOverallRating());
        assertEquals(4, result.getTechnicalSkillsRating());
        assertEquals(4, result.getBehavioralRating());
        assertEquals(4, result.getLeadershipRating());
        assertEquals(4, result.getTeamworkRating());
        assertEquals("Good communication", result.getStrengths());
        assertEquals("None", result.getAreasForImprovement());
        assertEquals("Great performance", result.getComments());
        assertEquals("SUBMITTED", result.getStatus());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(2L);
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenCreateReview_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenCreateReview_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceReviewService.createReview(reviewDTO));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(0)).findById(2L);
        verify(reviewRepository, times(0)).save(any(PerformanceReview.class));
    }

    @Test
    @DisplayName("givenNonExistingReviewedById_whenCreateReview_thenThrowRuntimeException")
    public void givenNonExistingReviewedById_whenCreateReview_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceReviewService.createReview(reviewDTO));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(2L);
        verify(reviewRepository, times(0)).save(any(PerformanceReview.class));
    }

    @Test
    @DisplayName("givenNullInput_whenCreateReview_thenThrowNullPointerException")
    public void givenNullInput_whenCreateReview_thenThrowNullPointerException() {
        // Arrange
        reviewDTO.setEmployeeId(null);
        reviewDTO.setReviewedById(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> performanceReviewService.createReview(reviewDTO));
        verify(employeeRepository, times(0)).findById(anyLong());
        verify(reviewRepository, times(0)).save(any(PerformanceReview.class));
    }

    @Test
    @DisplayName("givenValidId_whenGetReviewById_thenReturnSuccess")
    public void givenValidId_whenGetReviewById_thenReturnSuccess() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(performanceReview));

        // Act
        PerformanceReviewDTO result = performanceReviewService.getReviewById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(2L, result.getReviewedById());
        assertEquals("Q1", result.getReviewPeriod());
        assertEquals(LocalDate.now(), result.getReviewDate());
        assertEquals(4, result.getOverallRating());
        assertEquals(4, result.getTechnicalSkillsRating());
        assertEquals(4, result.getBehavioralRating());
        assertEquals(4, result.getLeadershipRating());
        assertEquals(4, result.getTeamworkRating());
        assertEquals("Good communication", result.getStrengths());
        assertEquals("None", result.getAreasForImprovement());
        assertEquals("Great performance", result.getComments());
        assertEquals("SUBMITTED", result.getStatus());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenNonExistingId_whenGetReviewById_thenThrowRuntimeException")
    public void givenNonExistingId_whenGetReviewById_thenThrowRuntimeException() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceReviewService.getReviewById(1L));
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetReviewsByEmployee_thenReturnSuccess")
    public void givenValidEmployeeId_whenGetReviewsByEmployee_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(reviewRepository.findByEmployee(employee)).thenReturn(Arrays.asList(performanceReview));

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(2L, result.get(0).getReviewedById());
        assertEquals("Q1", result.get(0).getReviewPeriod());
        assertEquals(LocalDate.now(), result.get(0).getReviewDate());
        assertEquals(4, result.get(0).getOverallRating());
        assertEquals(4, result.get(0).getTechnicalSkillsRating());
        assertEquals(4, result.get(0).getBehavioralRating());
        assertEquals(4, result.get(0).getLeadershipRating());
        assertEquals(4, result.get(0).getTeamworkRating());
        assertEquals("Good communication", result.get(0).getStrengths());
        assertEquals("None", result.get(0).getAreasForImprovement());
        assertEquals("Great performance", result.get(0).getComments());
        assertEquals("SUBMITTED", result.get(0).getStatus());
        verify(employeeRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetReviewsByEmployee_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenGetReviewsByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceReviewService.getReviewsByEmployee(1L));
        verify(employeeRepository, times(1)).findById(1L);
        verify(reviewRepository, times(0)).findByEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("givenValidManagerId_whenGetReviewsByManager_thenReturnSuccess")
    public void givenValidManagerId_whenGetReviewsByManager_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(reviewRepository.findByReviewedBy(manager)).thenReturn(Arrays.asList(performanceReview));

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByManager(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(2L, result.get(0).getReviewedById());
        assertEquals("Q1", result.get(0).getReviewPeriod());
        assertEquals(LocalDate.now(), result.get(0).getReviewDate());
        assertEquals(4, result.get(0).getOverallRating());
        assertEquals(4, result.get(0).getTechnicalSkillsRating());
        assertEquals(4, result.get(0).getBehavioralRating());
        assertEquals(4, result.get(0).getLeadershipRating());
        assertEquals(4, result.get(0).getTeamworkRating());
        assertEquals("Good communication", result.get(0).getStrengths());
        assertEquals("None", result.get(0).getAreasForImprovement());
        assertEquals("Great performance", result.get(0).getComments());
        assertEquals("SUBMITTED", result.get(0).getStatus());
        verify(employeeRepository, times(1)).findById(2L);
        verify(reviewRepository, times(1)).findByReviewedBy(manager);
    }

    @Test
    @DisplayName("givenNonExistingManagerId_whenGetReviewsByManager_thenThrowRuntimeException")
    public void givenNonExistingManagerId_whenGetReviewsByManager_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceReviewService.getReviewsByManager(2L));
        verify(employeeRepository, times(1)).findById(2L);
        verify(reviewRepository, times(0)).findByReviewedBy(any(Employee.class));
    }

    @Test
    @DisplayName("givenValidPeriod_whenGetReviewsByPeriod_thenReturnSuccess")
    public void givenValidPeriod_whenGetReviewsByPeriod_thenReturnSuccess() {
        // Arrange
        when(reviewRepository.findByReviewPeriod("Q1")).thenReturn(Arrays.asList(performanceReview));

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByPeriod("Q1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(2L, result.get(0).getReviewedById());
        assertEquals("Q1", result.get(0).getReviewPeriod());
        assertEquals(LocalDate.now(), result.get(0).getReviewDate());
        assertEquals(4, result.get(0).getOverallRating());
        assertEquals(4, result.get(0).getTechnicalSkillsRating());
        assertEquals(4, result.get(0).getBehavioralRating());
        assertEquals(4, result.get(0).getLeadershipRating());
        assertEquals(4, result.get(0).getTeamworkRating());
        assertEquals("Good communication", result.get(0).getStrengths());
        assertEquals("None", result.get(0).getAreasForImprovement());
        assertEquals("Great performance", result.get(0).getComments());
        assertEquals("SUBMITTED", result.get(0).getStatus());
        verify(reviewRepository, times(1)).findByReviewPeriod("Q1");
    }

    @Test
    @DisplayName("givenInvalidPeriod_whenGetReviewsByPeriod_thenReturnEmptyList")
    public void givenInvalidPeriod_whenGetReviewsByPeriod_thenReturnEmptyList() {
        // Arrange
        when(reviewRepository.findByReviewPeriod("Q2")).thenReturn(Arrays.asList());

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByPeriod("Q2");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findByReviewPeriod("Q2");
    }
}