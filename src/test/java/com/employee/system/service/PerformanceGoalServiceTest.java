package com.employee.system.service;

import com.employee.system.dto.PerformanceGoalDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.PerformanceGoal;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.PerformanceGoalRepository;
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
public class PerformanceGoalServiceTest {

    @Mock
    private PerformanceGoalRepository goalRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PerformanceGoalService performanceGoalService;

    private PerformanceGoalDTO validGoalDTO;
    private PerformanceGoal validGoal;
    private Employee validEmployee;

    @BeforeEach
    public void setUp() {
        validGoalDTO = new PerformanceGoalDTO();
        validGoalDTO.setEmployeeId(1L);
        validGoalDTO.setGoalTitle("Goal Title");
        validGoalDTO.setGoalDescription("Goal Description");
        validGoalDTO.setCategory("Category");
        validGoalDTO.setStartDate(LocalDate.now());
        validGoalDTO.setTargetDate(LocalDate.now().plusMonths(1));
        validGoalDTO.setStatus("DRAFT");
        validGoalDTO.setPriority(1);
        validGoalDTO.setProgressPercentage(0);
        validGoalDTO.setKeyResults("Key Results");
        validGoalDTO.setRemarks("Remarks");

        validEmployee = new Employee();
        validEmployee.setId(1L);
        validEmployee.setFirstName("John");
        validEmployee.setLastName("Doe");

        validGoal = new PerformanceGoal();
        validGoal.setId(1L);
        validGoal.setEmployee(validEmployee);
        validGoal.setGoalTitle("Goal Title");
        validGoal.setGoalDescription("Goal Description");
        validGoal.setCategory("Category");
        validGoal.setStartDate(LocalDate.now());
        validGoal.setTargetDate(LocalDate.now().plusMonths(1));
        validGoal.setStatus("DRAFT");
        validGoal.setPriority(1);
        validGoal.setProgressPercentage(0);
        validGoal.setKeyResults("Key Results");
        validGoal.setRemarks("Remarks");
    }

    @Test
    @DisplayName("givenValidInput_whenCreateGoal_thenReturnSuccess")
    public void givenValidInput_whenCreateGoal_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(validGoal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.createGoal(validGoalDTO);

        // Assert
        assertEquals(validGoalDTO.getId(), result.getId());
        assertEquals(validGoalDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(validGoalDTO.getGoalTitle(), result.getGoalTitle());
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenCreateGoal_thenThrowException")
    public void givenNonExistingEmployeeId_whenCreateGoal_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.createGoal(validGoalDTO));
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidInput_whenGetGoalById_thenReturnSuccess")
    public void givenValidInput_whenGetGoalById_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(validGoal));

        // Act
        PerformanceGoalDTO result = performanceGoalService.getGoalById(1L);

        // Assert
        assertEquals(validGoalDTO.getId(), result.getId());
        assertEquals(validGoalDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(validGoalDTO.getGoalTitle(), result.getGoalTitle());
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenGetGoalById_thenThrowException")
    public void givenNonExistingGoalId_whenGetGoalById_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.getGoalById(1L));
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetGoalsByEmployee_thenReturnSuccess")
    public void givenValidEmployeeId_whenGetGoalsByEmployee_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(goalRepository.findByEmployee(validEmployee)).thenReturn(Arrays.asList(validGoal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByEmployee(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(validGoalDTO.getId(), result.get(0).getId());
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).findByEmployee(validEmployee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetGoalsByEmployee_thenThrowException")
    public void givenNonExistingEmployeeId_whenGetGoalsByEmployee_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.getGoalsByEmployee(1L));
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenActiveStatus_whenGetActiveGoals_thenReturnSuccess")
    public void givenActiveStatus_whenGetActiveGoals_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findByStatus("ACTIVE")).thenReturn(Arrays.asList(validGoal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getActiveGoals();

        // Assert
        assertEquals(1, result.size());
        assertEquals(validGoalDTO.getId(), result.get(0).getId());
        verify(goalRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    @DisplayName("givenValidStatus_whenGetGoalsByStatus_thenReturnSuccess")
    public void givenValidStatus_whenGetGoalsByStatus_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findByStatus("COMPLETED")).thenReturn(Arrays.asList(validGoal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByStatus("COMPLETED");

        // Assert
        assertEquals(1, result.size());
        assertEquals(validGoalDTO.getId(), result.get(0).getId());
        verify(goalRepository, times(1)).findByStatus("COMPLETED");
    }

    @Test
    @DisplayName("givenValidInput_whenUpdateGoal_thenReturnSuccess")
    public void givenValidInput_whenUpdateGoal_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(validGoal));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(validGoal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.updateGoal(1L, validGoalDTO);

        // Assert
        assertEquals(validGoalDTO.getId(), result.getId());
        assertEquals(validGoalDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(validGoalDTO.getGoalTitle(), result.getGoalTitle());
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenUpdateGoal_thenThrowException")
    public void givenNonExistingGoalId_whenUpdateGoal_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.updateGoal(1L, validGoalDTO));
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidInput_whenUpdateGoalProgress_thenReturnSuccess")
    public void givenValidInput_whenUpdateGoalProgress_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(validGoal));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(validGoal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.updateGoalProgress(1L, 50);

        // Assert
        assertEquals(validGoalDTO.getId(), result.getId());
        assertEquals(50, result.getProgressPercentage());
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenUpdateGoalProgress_thenThrowException")
    public void givenNonExistingGoalId_whenUpdateGoalProgress_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.updateGoalProgress(1L, 50));
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidInput_whenDeleteGoal_thenSuccess")
    public void givenValidInput_whenDeleteGoal_thenSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(validGoal));

        // Act
        performanceGoalService.deleteGoal(1L);

        // Assert
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).delete(validGoal);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenDeleteGoal_thenThrowException")
    public void givenNonExistingGoalId_whenDeleteGoal_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.deleteGoal(1L));
        verify(goalRepository, times(1)).findById(1L);
    }
}