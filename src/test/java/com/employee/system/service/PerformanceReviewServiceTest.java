```java
package com.employee.system.service;

import com.employee.system.dto.PerformanceReviewDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.PerformanceReview;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.PerformanceReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceReviewServiceTest {

    @Mock
    private PerformanceReviewRepository reviewRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PerformanceReviewService performanceReviewService;

    // -------------------------------------------------------------------------
    // Helper methods to build test objects
    // -------------------------------------------------------------------------

    private Employee buildEmployee(Long id, String firstName, String lastName) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(firstName);
        e.setLastName(lastName);
        return e;
    }

    private PerformanceReview buildReview(Long id, Employee employee, Employee manager) {
        PerformanceReview r = new PerformanceReview();
        r.setId(id);
        r.setEmployee(employee);
        r.setReviewedBy(manager);
        r.setReviewPeriod("2023-Q1");
        r.setReviewDate(LocalDate.of(2023, 3, 31));
        r.setOverallRating(4);
        r.setTechnicalSkillsRating(4);
        r.setBehavioralRating(4);
        r.setLeadershipRating(4);
        r.setTeamworkRating(4);
        r.setStrengths("Strengths");
        r.setAreasForImprovement("Improvements");
        r.setComments("Comments");
        r.setStatus("COMPLETED");
        r.setCreatedAt(LocalDate.now().atStartOfDay());
        r.setUpdatedAt(LocalDate.now().atStartOfDay());
        return r;
    }

    private PerformanceReviewDTO buildDto(Long employeeId, Long reviewedById) {
        PerformanceReviewDTO dto = new PerformanceReviewDTO();
        dto.setEmployeeId(employeeId);
        dto.setReviewedById(reviewedById);
        dto.setReviewPeriod("2023-Q1");
        dto.setReviewDate(LocalDate.of(2023, 3, 31));
        dto.setOverallRating(4);
        dto.setTechnicalSkillsRating(4);
        dto.setBehavioralRating(4);
        dto.setLeadershipRating(4);
        dto.setTeamworkRating(4);
        dto.setStrengths("Strengths");
        dto.setAreasForImprovement("Improvements");
        dto.setComments("Comments");
        dto.setStatus("COMPLETED");
        return dto;
    }

    // -------------------------------------------------------------------------
    // createReview
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given valid DTO when createReview then return saved DTO")
    void givenValidReviewDTO_whenCreateReview_thenReturnSavedDTO() {
        // Arrange
        PerformanceReviewDTO inputDto = buildDto(1L, 2L);
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview savedReview = buildReview(10L, employee, manager);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(reviewRepository.save(any(PerformanceReview.class))).thenReturn(savedReview);

        // Act
        PerformanceReviewDTO result = performanceReviewService.createReview(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals(2L, result.getReviewedById());
        assertEquals("Jane Smith", result.getReviewedByName());
        assertEquals("2023-Q1", result.getReviewPeriod());
        assertEquals(LocalDate.of(2023, 3, 31), result.getReviewDate());
        assertEquals("COMPLETED", result.getStatus());
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    @DisplayName("Given non‑existing employee id when createReview then throw RuntimeException")
    void givenNonExistingEmployeeId_whenCreateReview_thenThrowRuntimeException() {
        // Arrange
        PerformanceReviewDTO inputDto = buildDto(99L, 2L);
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> performanceReviewService.createReview(inputDto));
        assertTrue(ex.getMessage().contains("Employee not found"));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given non‑existing manager id when createReview then throw RuntimeException")
    void givenNonExistingManagerId_whenCreateReview_thenThrowRuntimeException() {
        // Arrange
        PerformanceReviewDTO inputDto = buildDto(1L, 99L);
        Employee employee = buildEmployee(1L, "John", "Doe");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> performanceReviewService.createReview(inputDto));
        assertTrue(ex.getMessage().contains("Manager not found"));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given null reviewDate and status when createReview then defaults are applied")
    void givenNullReviewDateAndStatus_whenCreateReview_thenSetDefaults() {
        // Arrange
        PerformanceReviewDTO inputDto = buildDto(1L, 2L);
        inputDto.setReviewDate(null);
        inputDto.setStatus(null);
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        when(reviewRepository.save(captor.capture())).thenAnswer(invocation -> {
            PerformanceReview r = captor.getValue();
            r.setId(20L);
            return r;
        });

        // Act
        PerformanceReviewDTO result = performanceReviewService.createReview(inputDto);

        // Assert
        PerformanceReview captured = captor.getValue();
        assertNotNull(captured.getReviewDate(), "Review date should be defaulted to now");
        assertEquals("DRAFT", captured.getStatus(), "Status should default to DRAFT");
        assertEquals("DRAFT", result.getStatus());
        assertEquals(20L, result.getId());
    }

    // -------------------------------------------------------------------------
    // getReviewById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing review id when getReviewById then return DTO")
    void givenExistingReviewId_whenGetReviewById_thenReturnDTO() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review = buildReview(10L, employee, manager);
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        // Act
        PerformanceReviewDTO result = performanceReviewService.getReviewById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("Jane Smith", result.getReviewedByName());
        verify(reviewRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Given non‑existing review id when getReviewById then throw RuntimeException")
    void givenNonExistingReviewId_whenGetReviewById_thenThrowRuntimeException() {
        // Arrange
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> performanceReviewService.getReviewById(99L));
        assertTrue(ex.getMessage().contains("Performance review not found"));
        verify(reviewRepository, times(1)).findById(99L);
    }

    // -------------------------------------------------------------------------
    // getReviewsByEmployee
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing employee id when getReviewsByEmployee then return list of DTOs")
    void givenExistingEmployeeId_whenGetReviewsByEmployee_thenReturnListDTO() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review = buildReview(10L, employee, manager);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(reviewRepository.findByEmployee(employee)).thenReturn(List.of(review));

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        verify(employeeRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("Given non‑existing employee id when getReviewsByEmployee then throw RuntimeException")
    void givenNonExistingEmployeeId_whenGetReviewsByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> performanceReviewService.getReviewsByEmployee(99L));
        assertTrue(ex.getMessage().contains("Employee not found"));
        verify(reviewRepository, never()).findByEmployee(any());
    }

    // -------------------------------------------------------------------------
    // getReviewsByManager
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing manager id when getReviewsByManager then return list of DTOs")
    void givenExistingManagerId_whenGetReviewsByManager_thenReturnListDTO() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review = buildReview(10L, employee, manager);
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(reviewRepository.findByReviewedBy(manager)).thenReturn(List.of(review));

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByManager(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        verify(employeeRepository, times(1)).findById(2L);
        verify(reviewRepository, times(1)).findByReviewedBy(manager);
    }

    @Test
    @DisplayName("Given non‑existing manager id when getReviewsByManager then throw RuntimeException")
    void givenNonExistingManagerId_whenGetReviewsByManager_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> performanceReviewService.getReviewsByManager(99L));
        assertTrue(ex.getMessage().contains("Manager not found"));
        verify(reviewRepository, never()).findByReviewedBy(any());
    }

    // -------------------------------------------------------------------------
    // getReviewsByPeriod
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given period when getReviewsByPeriod then return matching DTO list")
    void givenPeriod_whenGetReviewsByPeriod_thenReturnListDTO() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review = buildReview(10L, employee, manager);
        when(reviewRepository.findByReviewPeriod("2023-Q1")).thenReturn(List.of(review));

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByPeriod("2023-Q1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2023-Q1", result.get(0).getReviewPeriod());
        verify(reviewRepository, times(1)).findByReviewPeriod("2023-Q1");
    }

    @Test
    @DisplayName("Given unknown period when getReviewsByPeriod then return empty list")
    void givenUnknownPeriod_whenGetReviewsByPeriod_thenReturnEmptyList() {
        // Arrange
        when(reviewRepository.findByReviewPeriod("UNKNOWN")).thenReturn(Collections.emptyList());

        // Act
        List<PerformanceReviewDTO> result = performanceReviewService.getReviewsByPeriod("UNKNOWN");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findByReviewPeriod("UNKNOWN");
    }

    // -------------------------------------------------------------------------
    // updateReview
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given valid update DTO when updateReview then return updated DTO")
    void givenValidUpdateDTO_whenUpdateReview_thenReturnUpdatedDTO() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview existing = buildReview(10L, employee, manager);
        PerformanceReviewDTO updateDto = new PerformanceReviewDTO();
        updateDto.setOverallRating(5);
        updateDto.setTechnicalSkillsRating(5);
        updateDto.setBehavioralRating(5);
        updateDto.setLeadershipRating(5);
        updateDto.setTeamworkRating(5);
        updateDto.setStrengths("Updated strengths");
        updateDto.setAreasForImprovement("Updated improvements");
        updateDto.setComments("Updated comments");
        updateDto.setStatus("APPROVED");

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(PerformanceReview.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PerformanceReviewDTO result = performanceReviewService.updateReview(10L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getOverallRating());
        assertEquals("Updated strengths", result.getStrengths());
        assertEquals("APPROVED", result.getStatus());
        verify(reviewRepository, times(1)).findById(10L);
        verify(reviewRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Given non‑existing review id when updateReview then throw RuntimeException")
    void givenNonExistingReviewId_whenUpdateReview_thenThrowRuntimeException() {
        // Arrange
        PerformanceReviewDTO updateDto = new PerformanceReviewDTO();
        when(reviewRepository.findById(99L)).then