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
    private PerformanceReviewService service;

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

    private PerformanceReviewDTO buildReviewDTO(Long employeeId, Long reviewedById) {
        PerformanceReviewDTO dto = new PerformanceReviewDTO();
        dto.setEmployeeId(employeeId);
        dto.setReviewedById(reviewedById);
        dto.setReviewPeriod("2023-Q4");
        dto.setReviewDate(null); // to test defaulting to today
        dto.setOverallRating(4);
        dto.setTechnicalSkillsRating(5);
        dto.setBehavioralRating(3);
        dto.setLeadershipRating(4);
        dto.setTeamworkRating(5);
        dto.setStrengths("Strong problem solving");
        dto.setAreasForImprovement("Time management");
        dto.setComments("Excellent performance");
        dto.setStatus(null); // to test defaulting to DRAFT
        return dto;
    }

    private PerformanceReview buildReview(Long id, Employee employee, Employee manager) {
        PerformanceReview r = new PerformanceReview();
        r.setId(id);
        r.setEmployee(employee);
        r.setReviewedBy(manager);
        r.setReviewPeriod("2023-Q4");
        r.setReviewDate(LocalDate.now());
        r.setOverallRating(4);
        r.setTechnicalSkillsRating(5);
        r.setBehavioralRating(3);
        r.setLeadershipRating(4);
        r.setTeamworkRating(5);
        r.setStrengths("Strong problem solving");
        r.setAreasForImprovement("Time management");
        r.setComments("Excellent performance");
        r.setStatus("COMPLETED");
        return r;
    }

    // -------------------------------------------------------------------------
    // createReview tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given valid ReviewDTO when createReview then return saved DTO with defaults")
    void givenValidReviewDTO_whenCreateReview_thenReturnSavedDTO() {
        // Arrange
        Long empId = 1L;
        Long mgrId = 2L;
        Employee employee = buildEmployee(empId, "John", "Doe");
        Employee manager = buildEmployee(mgrId, "Jane", "Smith");
        PerformanceReviewDTO inputDto = buildReviewDTO(empId, mgrId);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(mgrId)).thenReturn(Optional.of(manager));

        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        PerformanceReview savedEntity = buildReview(10L, employee, manager);
        when(reviewRepository.save(captor.capture())).thenReturn(savedEntity);

        // Act
        PerformanceReviewDTO result = service.createReview(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(empId, result.getEmployeeId());
        assertEquals(mgrId, result.getReviewedById());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("Jane Smith", result.getReviewedByName());
        assertEquals("2023-Q4", result.getReviewPeriod());
        assertEquals(LocalDate.now(), result.getReviewDate());
        assertEquals("DRAFT", result.getStatus());

        // verify that the entity saved contains the defaults
        PerformanceReview captured = captor.getValue();
        assertEquals(LocalDate.now(), captured.getReviewDate());
        assertEquals("DRAFT", captured.getStatus());

        verify(employeeRepository, times(1)).findById(empId);
        verify(employeeRepository, times(1)).findById(mgrId);
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    @DisplayName("Given non‑existing employee id when createReview then throw RuntimeException")
    void givenNonExistingEmployeeId_whenCreateReview_thenThrowRuntimeException() {
        // Arrange
        Long empId = 99L;
        Long mgrId = 2L;
        PerformanceReviewDTO dto = buildReviewDTO(empId, mgrId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createReview(dto));
        assertTrue(ex.getMessage().contains("Employee not found with id: " + empId));

        verify(employeeRepository, times(1)).findById(empId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    @DisplayName("Given non‑existing manager id when createReview then throw RuntimeException")
    void givenNonExistingManagerId_whenCreateReview_thenThrowRuntimeException() {
        // Arrange
        Long empId = 1L;
        Long mgrId = 99L;
        Employee employee = buildEmployee(empId, "John", "Doe");
        PerformanceReviewDTO dto = buildReviewDTO(empId, mgrId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(mgrId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createReview(dto));
        assertTrue(ex.getMessage().contains("Manager not found with id: " + mgrId));

        verify(employeeRepository, times(1)).findById(empId);
        verify(employeeRepository, times(1)).findById(mgrId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    @DisplayName("Given null ReviewDTO when createReview then throw NullPointerException")
    void givenNullReviewDTO_whenCreateReview_thenThrowNullPointerException() {
        // Arrange
        PerformanceReviewDTO dto = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> service.createReview(dto));
        verifyNoInteractions(employeeRepository, reviewRepository);
    }

    // -------------------------------------------------------------------------
    // getReviewById tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing review id when getReviewById then return DTO")
    void givenExistingId_whenGetReviewById_thenReturnDTO() {
        // Arrange
        Long reviewId = 10L;
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review = buildReview(reviewId, employee, manager);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act
        PerformanceReviewDTO result = service.getReviewById(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(manager.getId(), result.getReviewedById());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("Jane Smith", result.getReviewedByName());

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("Given non‑existing review id when getReviewById then throw RuntimeException")
    void givenNonExistingId_whenGetReviewById_thenThrowRuntimeException() {
        // Arrange
        Long reviewId = 99L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getReviewById(reviewId));
        assertTrue(ex.getMessage().contains("Performance review not found with id: " + reviewId));

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    // -------------------------------------------------------------------------
    // getReviewsByEmployee tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing employee id when getReviewsByEmployee then return list of DTOs")
    void givenValidEmployeeId_whenGetReviewsByEmployee_thenReturnListDTO() {
        // Arrange
        Long empId = 1L;
        Employee employee = buildEmployee(empId, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review1 = buildReview(10L, employee, manager);
        PerformanceReview review2 = buildReview(11L, employee, manager);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(reviewRepository.findByEmployee(employee)).thenReturn(List.of(review1, review2));

        // Act
        List<PerformanceReviewDTO> result = service.getReviewsByEmployee(empId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(11L, result.get(1).getId());

        verify(employeeRepository, times(1)).findById(empId);
        verify(reviewRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("Given non‑existing employee id when getReviewsByEmployee then throw RuntimeException")
    void givenNonExistingEmployeeId_whenGetReviewsByEmployee_thenThrowRuntimeException() {
        // Arrange
        Long empId = 99L;
        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getReviewsByEmployee(empId));
        assertTrue(ex.getMessage().contains("Employee not found with id: " + empId));

        verify(employeeRepository, times(1)).findById(empId);
        verifyNoInteractions(reviewRepository);
    }

    // -------------------------------------------------------------------------
    // getReviewsByManager tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing manager id when getReviewsByManager then return list of DTOs")
    void givenValidManagerId_whenGetReviewsByManager_thenReturnListDTO() {
        // Arrange
        Long mgrId = 2L;
        Employee manager = buildEmployee(mgrId, "Jane", "Smith");
        Employee employee = buildEmployee(1L, "John", "Doe");
        PerformanceReview review = buildReview(10L, employee, manager);
        when(employeeRepository.findById(mgrId)).thenReturn(Optional.of(manager));
        when(reviewRepository.findByReviewedBy(manager)).thenReturn(List.of(review));

        // Act
        List<PerformanceReviewDTO> result = service.getReviewsByManager(mgrId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());

        verify(employeeRepository, times(1)).findById(mgrId);
        verify(reviewRepository, times(1)).findByReviewedBy(manager);
    }

    @Test
    @DisplayName("Given non‑existing manager id when getReviewsByManager then throw RuntimeException")
    void givenNonExistingManagerId_whenGetReviewsByManager_thenThrowRuntimeException() {
        // Arrange
        Long mgrId = 99L;
        when(employeeRepository.findById(mgrId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getReviewsByManager(mgrId));
        assertTrue(ex.getMessage().contains("Manager not found with id: " + mgrId));

        verify(employeeRepository, times(1)).findById(mgrId);
        verifyNoInteractions(reviewRepository);
    }

    // -------------------------------------------------------------------------
    // getReviewsByPeriod tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing period when getReviewsByPeriod then return list of DTOs")
    void givenExistingPeriod_whenGetReviewsByPeriod_thenReturnListDTO() {
        // Arrange
        String period = "2023-Q4";
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        PerformanceReview review = buildReview(10L, employee, manager);
        when(reviewRepository.findByReviewPeriod(period)).thenReturn(List.of(review));

        // Act
        List<PerformanceReviewDTO> result = service.getReviewsByPeriod(period);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());

        verify(reviewRepository, times(1)).findByReviewPeriod(period);
    }

    @Test
    @DisplayName("Given period with no reviews when getReviewsByPeriod then return empty list")
    void givenNoReviewsForPeriod_whenGetReviewsByPeriod_thenReturnEmptyList() {
        // Arrange
        String period = "2022-Q1";
        when(reviewRepository.findByReviewPeriod(period)).thenReturn(Collections.emptyList());

        // Act
        List<PerformanceReviewDTO> result = service.getReviewsByPeriod(period);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());