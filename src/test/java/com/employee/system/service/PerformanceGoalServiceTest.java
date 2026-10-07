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
import org.mockito.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerformanceGoalServiceTest {

    @Mock
    private PerformanceGoalRepository goalRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PerformanceGoalService performanceGoalService;

    private PerformanceGoalDTO goalDTO;
    private PerformanceGoal goal;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        goalDTO = new PerformanceGoalDTO();
        goalDTO.setEmployeeId(1L);
        goalDTO.setGoalTitle("Goal Title");
        goalDTO.setGoalDescription("Goal Description");
        goalDTO.setCategory("Category");
        goalDTO.setStartDate(LocalDate.now());
        goalDTO.setTargetDate(LocalDate.now().plusMonths(1));
        goalDTO.setStatus("DRAFT");
        goalDTO.setPriority(1);
        goalDTO.setProgressPercentage(0);
        goalDTO.setKeyResults("Key Results");
        goalDTO.setRemarks("Remarks");

        goal = new PerformanceGoal();
        goal.setId(1L);
        goal.setEmployee(new Employee());
        goal.getEmployee().setId(1L);
        goal.setGoalTitle("Goal Title");
        goal.setGoalDescription("Goal Description");
        goal.setCategory("Category");
        goal.setStartDate(LocalDate.now());
        goal.setTargetDate(LocalDate.now().plusMonths(1));
        goal.setStatus("DRAFT");
        goal.setPriority(1);
        goal.setProgressPercentage(0);
        goal.setKeyResults("Key Results");
        goal.setRemarks("Remarks");
        goal.setCreatedAt(LocalDate.now());
        goal.setUpdatedAt(LocalDate.now());

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
    }

    @Test
    @DisplayName("givenValidInput_whenCreateGoal_thenReturnCreatedGoal")
    public void givenValidInput_whenCreateGoal_thenReturnCreatedGoal() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(goal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.createGoal(goalDTO);

        // Assert
        assertEquals(goal.getId(), result.getId());
        assertEquals(goal.getEmployee().getId(), result.getEmployeeId());
        assertEquals(goal.getGoalTitle(), result.getGoalTitle());
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenCreateGoal_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenCreateGoal_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.createGoal(goalDTO));
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenValidId_whenGetGoalById_thenReturnGoal")
    public void givenValidId_whenGetGoalById_thenReturnGoal() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        // Act
        PerformanceGoalDTO result = performanceGoalService.getGoalById(1L);

        // Assert
        assertEquals(goal.getId(), result.getId());
        assertEquals(goal.getEmployee().getId(), result.getEmployeeId());
        assertEquals(goal.getGoalTitle(), result.getGoalTitle());
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenGetGoalById_thenThrowRuntimeException")
    public void givenNonExistingGoalId_whenGetGoalById_thenThrowRuntimeException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.getGoalById(1L));
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetGoalsByEmployee_thenReturnGoals")
    public void givenValidEmployeeId_whenGetGoalsByEmployee_thenReturnGoals() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(goalRepository.findByEmployee(employee)).thenReturn(Arrays.asList(goal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByEmployee(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(goal.getId(), result.get(0).getId());
        assertEquals(goal.getEmployee().getId(), result.get(0).getEmployeeId());
        assertEquals(goal.getGoalTitle(), result.get(0).getGoalTitle());
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetGoalsByEmployee_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenGetGoalsByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.getGoalsByEmployee(1L));
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).findByEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("givenStatusActive_whenGetActiveGoals_thenReturnActiveGoals")
    public void givenStatusActive_whenGetActiveGoals_thenReturnActiveGoals() {
        // Arrange
        when(goalRepository.findByStatus("ACTIVE")).thenReturn(Arrays.asList(goal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getActiveGoals();

        // Assert
        assertEquals(1, result.size());
        assertEquals(goal.getId(), result.get(0).getId());
        assertEquals(goal.getEmployee().getId(), result.get(0).getEmployeeId());
        assertEquals(goal.getGoalTitle(), result.get(0).getGoalTitle());
        verify(goalRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    @DisplayName("givenStatusInactive_whenGetGoalsByStatus_thenReturnGoals")
    public void givenStatusInactive_whenGetGoalsByStatus_thenReturnGoals() {
        // Arrange
        when(goalRepository.findByStatus("INACTIVE")).thenReturn(Arrays.asList(goal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByStatus("INACTIVE");

        // Assert
        assertEquals(1, result.size());
        assertEquals(goal.getId(), result.get(0).getId());
        assertEquals(goal.getEmployee().getId(), result.get(0).getEmployeeId());
        assertEquals(goal.getGoalTitle(), result.get(0).getGoalTitle());
        verify(goalRepository, times(1)).findByStatus("INACTIVE");
    }

    @Test
    @DisplayName("givenValidIdAndDTO_whenUpdateGoal_thenReturnUpdatedGoal")
    public void givenValidIdAndDTO_whenUpdateGoal_thenReturnUpdatedGoal() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(goal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.updateGoal(1L, goalDTO);

        // Assert
        assertEquals(goal.getId(), result.getId());
        assertEquals(goal.getEmployee().getId(), result.getEmployeeId());
        assertEquals(goal.getGoalTitle(), result.getGoalTitle());
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenUpdateGoal_thenThrowRuntimeException")
    public void givenNonExistingGoalId_whenUpdateGoal_thenThrowRuntimeException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.updateGoal(1L, goalDTO));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenValidIdAndProgressPercentage_whenUpdateGoalProgress_thenReturnUpdatedGoal")
    public void givenValidIdAndProgressPercentage_whenUpdateGoalProgress_thenReturnUpdatedGoal() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(goal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.updateGoalProgress(1L, 50);

        // Assert
        assertEquals(goal.getId(), result.getId());
        assertEquals(goal.getEmployee().getId(), result.getEmployeeId());
        assertEquals(50, result.getProgressPercentage());
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenUpdateGoalProgress_thenThrowRuntimeException")
    public void givenNonExistingGoalId_whenUpdateGoalProgress_thenThrowRuntimeException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.updateGoalProgress(1L, 50));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).save(any(PerformanceGoal.class));
    }

    @Test
    @DisplayName("givenValidId_whenDeleteGoal_thenNoException")
    public void givenValidId_whenDeleteGoal_thenNoException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        // Act & Assert
        assertDoesNotThrow(() -> performanceGoalService.deleteGoal(1L));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenDeleteGoal_thenThrowRuntimeException")
    public void givenNonExistingGoalId_whenDeleteGoal_thenThrowRuntimeException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.deleteGoal(1L));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).deleteById(1L);
    }
}