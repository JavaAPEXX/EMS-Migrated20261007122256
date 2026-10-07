```java
package com.employee.system.service;

import com.employee.system.dto.LeaveBalanceDTO;
import com.employee.system.dto.LeaveDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.Holiday;
import com.employee.system.entity.Leave;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.HolidayRepository;
import com.employee.system.repository.LeaveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private LeaveService leaveService;

    // -------------------------------------------------------------------------
    // applyLeave tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given valid LeaveDTO when applyLeave then return saved LeaveDTO")
    void givenValidLeaveDTO_whenApplyLeave_thenReturnSavedLeaveDTO() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        LocalDate start = LocalDate.of(2023, 5, 1);
        LocalDate end = LocalDate.of(2023, 5, 3);
        LeaveDTO request = new LeaveDTO();
        request.setEmployeeId(employee.getId());
        request.setStartDate(start);
        request.setEndDate(end);
        request.setLeaveType("SICK");
        request.setReason("Flu");

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveRepository.hasOverlappingLeave(employee, start, end)).thenReturn(false);
        when(holidayRepository.findBetween(start, end)).thenReturn(Collections.emptyList());
        when(leaveRepository.countTakenDaysByTypeAndYear(employee, "SICK", start.getYear())).thenReturn(0L);

        // capture the Leave entity passed to save()
        ArgumentCaptor<Leave> leaveCaptor = ArgumentCaptor.forClass(Leave.class);
        Leave savedLeave = new Leave();
        savedLeave.setId(100L);
        savedLeave.setEmployee(employee);
        savedLeave.setStartDate(start);
        savedLeave.setEndDate(end);
        savedLeave.setLeaveType("SICK");
        savedLeave.setReason("Flu");
        savedLeave.setDays(3); // 3 working days, no holidays
        savedLeave.setStatus("PENDING");
        when(leaveRepository.save(leaveCaptor.capture())).thenReturn(savedLeave);

        // Act
        LeaveDTO result = leaveService.applyLeave(request);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals(start, result.getStartDate());
        assertEquals(end, result.getEndDate());
        assertEquals("SICK", result.getLeaveType());
        assertEquals("PENDING", result.getStatus());
        assertEquals(3, result.getDays());

        // verify that the saved entity contains the calculated days and default status
        Leave captured = leaveCaptor.getValue();
        assertEquals(3, captured.getDays());
        assertEquals("PENDING", captured.getStatus());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(leaveRepository, times(1)).hasOverlappingLeave(employee, start, end);
        verify(holidayRepository, times(1)).findBetween(start, end);
        verify(leaveRepository, times(1)).countTakenDaysByTypeAndYear(employee, "SICK", start.getYear());
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    @Test
    @DisplayName("Given LeaveDTO with null dates when applyLeave then throw RuntimeException")
    void givenLeaveDTOWithNullDates_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(2L);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        LeaveDTO request = new LeaveDTO();
        request.setEmployeeId(employee.getId());
        request.setStartDate(null);
        request.setEndDate(null);
        request.setLeaveType("CASUAL");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(request));
        assertEquals("Invalid leave date range", ex.getMessage());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verifyNoMoreInteractions(leaveRepository, holidayRepository);
    }

    @Test
    @DisplayName("Given LeaveDTO with end date before start date when applyLeave then throw RuntimeException")
    void givenLeaveDTOWithEndBeforeStart_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(3L);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        LeaveDTO request = new LeaveDTO();
        request.setEmployeeId(employee.getId());
        request.setStartDate(LocalDate.of(2023, 6, 10));
        request.setEndDate(LocalDate.of(2023, 6, 5));
        request.setLeaveType("EARNED");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(request));
        assertEquals("Invalid leave date range", ex.getMessage());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verifyNoMoreInteractions(leaveRepository, holidayRepository);
    }

    @Test
    @DisplayName("Given overlapping leave when applyLeave then throw RuntimeException")
    void givenOverlappingLeave_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(4L);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        LocalDate start = LocalDate.of(2023, 7, 1);
        LocalDate end = LocalDate.of(2023, 7, 4);
        LeaveDTO request = new LeaveDTO();
        request.setEmployeeId(employee.getId());
        request.setStartDate(start);
        request.setEndDate(end);
        request.setLeaveType("CASUAL");

        when(leaveRepository.hasOverlappingLeave(employee, start, end)).thenReturn(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(request));
        assertEquals("Leave conflict detected for the requested period", ex.getMessage());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(leaveRepository, times(1)).hasOverlappingLeave(employee, start, end);
        verifyNoMoreInteractions(leaveRepository, holidayRepository);
    }

    @Test
    @DisplayName("Given insufficient leave balance when applyLeave then throw RuntimeException")
    void givenInsufficientBalance_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(5L);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        LocalDate start = LocalDate.of(2023, 8, 1);
        LocalDate end = LocalDate.of(2023, 8, 5);
        LeaveDTO request = new LeaveDTO();
        request.setEmployeeId(employee.getId());
        request.setStartDate(start);
        request.setEndDate(end);
        request.setLeaveType("SICK"); // entitlement = 10

        when(leaveRepository.hasOverlappingLeave(employee, start, end)).thenReturn(false);
        when(holidayRepository.findBetween(start, end)).thenReturn(Collections.emptyList());
        // Already taken 8 days, requesting 5 days => exceeds entitlement
        when(leaveRepository.countTakenDaysByTypeAndYear(employee, "SICK", start.getYear())).thenReturn(8L);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(request));
        assertTrue(ex.getMessage().contains("Insufficient leave balance"));

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(leaveRepository, times(1)).hasOverlappingLeave(employee, start, end);
        verify(holidayRepository, times(1)).findBetween(start, end);
        verify(leaveRepository, times(1)).countTakenDaysByTypeAndYear(employee, "SICK", start.getYear());
    }

    @Test
    @DisplayName("Given non‑existing employee when applyLeave then throw RuntimeException")
    void givenNonExistingEmployee_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        LeaveDTO request = new LeaveDTO();
        request.setEmployeeId(999L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));
        request.setLeaveType("EARNED");

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(request));
        assertEquals("Employee not found with id: 999", ex.getMessage());

        verify(employeeRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(leaveRepository, holidayRepository);
    }

    // -------------------------------------------------------------------------
    // approveLeave tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing leave id and approve true when approveLeave then status becomes APPROVED")
    void givenValidId_whenApproveLeave_thenReturnApprovedDTO() {
        // Arrange
        Long leaveId = 200L;
        Leave existing = new Leave();
        existing.setId(leaveId);
        existing.setStatus("PENDING");

        when(leaveRepository.findById(leaveId)).thenReturn(Optional.of(existing));
        when(leaveRepository.save(existing)).thenReturn(existing); // same instance after status change

        // Act
        LeaveDTO result = leaveService.approveLeave(leaveId, true);

        // Assert
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());

        verify(leaveRepository, times(1)).findById(leaveId);
        verify(leaveRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Given existing leave id and approve false when approveLeave then status becomes REJECTED")
    void givenValidId_whenRejectLeave_thenReturnRejectedDTO() {
        // Arrange
        Long leaveId = 201L;
        Leave existing = new Leave();
        existing.setId(leaveId);
        existing.setStatus("PENDING");

        when(leaveRepository.findById(leaveId)).thenReturn(Optional.of(existing));
        when(leaveRepository.save(existing)).thenReturn(existing);

        // Act
        LeaveDTO result = leaveService.approveLeave(leaveId, false);

        // Assert
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());

        verify(leaveRepository, times(1)).findById(leaveId);
        verify(leaveRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Given non‑existing leave id when approveLeave then throw RuntimeException")
    void givenNonExistingLeave_whenApproveLeave_thenThrowRuntimeException() {
        // Arrange
        Long leaveId = 999L;
        when(leaveRepository.findById(leaveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.approveLeave(leaveId, true));
        assertEquals("Leave not found with id: 999", ex.getMessage());

        verify(leaveRepository, times(1)).findById(leaveId);
        verifyNoMoreInteractions(leaveRepository);
    }

    // -------------------------------------------------------------------------
    // getLeaveById tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing leave id when getLeaveById then return corresponding DTO")
    void givenValidId_whenGetLeaveById_thenReturnDTO() {
        // Arrange
        Long leaveId = 300L;
        Employee emp = new Employee();
        emp.setId(10L);
        emp.setFirstName("Alice");
        emp.setLastName("Smith");

        Leave leave = new Leave();
        leave.setId(leaveId);
        leave.setEmployee(emp);
        leave.setLeaveType("EARNED");
        leave.setStatus("APPROVED");
        leave.setStartDate(LocalDate.of(2023, 9, 1));
        leave.setEndDate(LocalDate.of(2023, 9, 3));
        leave.setDays(3);

        when(leaveRepository.findById(leaveId)).thenReturn(Optional.of(leave));

        // Act
        LeaveDTO result = leaveService.getLeaveById(leaveId);

        // Assert
        assertNotNull(result);
        assertEquals(leaveId, result.getId());
        assertEquals(emp.getId(), result.getEmployeeId());
        assertEquals("Alice Smith", result.getEmployeeName());
        assertEquals("EARNED", result.getLeaveType());
        assertEquals("APPROVED", result.getStatus());

        verify(leaveRepository, times(1)).findById(leaveId);
    }

    @Test
    @DisplayName("Given non‑existing leave id when getLeaveById then throw RuntimeException")
    void givenNonExistingLeave_whenGetLeaveById_thenThrowRuntimeException() {
        // Arrange
        Long leaveId = 999L;
        when(leaveRepository.findById(leaveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaveService.getLeaveById(leaveId));
        assertEquals("Leave not found with id: 999", ex.getMessage());

        verify(leaveRepository, times(1)).findById(leaveId);
    }

    // -------------------------------------------------------------------------
    // getLeavesByEmployee tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given valid employee id when getLeavesByEmployee then return list of DTOs")
    void givenValidEmployeeId_whenGetLeavesByEmployee_thenReturnList() {
        // Arrange
        Long empId = 11L;
        Employee employee = new Employee();
        employee.setId(empId);
        employee.setFirstName("Bob");
        employee.setLastName("Brown");

        Leave leave1 = new Leave();
        leave1.setId(1L);
        leave1.setEmployee(employee);
        leave1.setLeaveType("SICK");
        leave1.setStatus("APPROVED");
        leave1.setStartDate(LocalDate.now());
        leave1.setEndDate(LocalDate.now().plusDays(1));
        leave1.setDays(2);

        Leave leave2 = new Leave();
        leave2.setId(2L);
        leave2.setEmployee(employee);
        leave2.setLeaveType("CASUAL");
        leave