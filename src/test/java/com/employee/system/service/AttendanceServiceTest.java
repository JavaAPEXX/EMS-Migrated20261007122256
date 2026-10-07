```java
package com.employee.system.service;

import com.employee.system.dto.AttendanceDTO;
import com.employee.system.entity.Attendance;
import com.employee.system.entity.Employee;
import com.employee.system.repository.AttendanceRepository;
import com.employee.system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Employee employee;
    private Attendance attendance;
    private AttendanceDTO attendanceDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        attendance = new Attendance();
        attendance.setId(1L);
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(LocalDate.of(2023, 10, 1));
        attendance.setStatus("PRESENT");
        attendance.setCheckInTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        attendance.setCheckOutTime(LocalDateTime.of(2023, 10, 1, 17, 0));
        attendance.setRemarks("On time");
        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());

        attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(1L);
        attendanceDTO.setEmployeeId(1L);
        attendanceDTO.setEmployeeName("John Doe");
        attendanceDTO.setAttendanceDate(LocalDate.of(2023, 10, 1));
        attendanceDTO.setStatus("PRESENT");
        attendanceDTO.setCheckInTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        attendanceDTO.setCheckOutTime(LocalDateTime.of(2023, 10, 1, 17, 0));
        attendanceDTO.setRemarks("On time");
        attendanceDTO.setCreatedAt(LocalDateTime.now());
        attendanceDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Given valid attendance DTO when marking attendance then return saved attendance DTO")
    void givenValidAttendanceDTO_whenMarkAttendance_thenReturnSavedAttendanceDTO() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        // Act
        AttendanceDTO result = attendanceService.markAttendance(attendanceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("PRESENT", result.getStatus());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Given non-existent employee ID when marking attendance then throw RuntimeException")
    void givenNonExistentEmployeeId_whenMarkAttendance_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(attendanceDTO);
        });
        assertTrue(exception.getMessage().contains("Employee not found with id: 999"));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Given null attendance DTO when marking attendance then throw NullPointerException")
    void givenNullAttendanceDTO_whenMarkAttendance_thenThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            attendanceService.markAttendance(null);
        });
    }

    @Test
    @DisplayName("Given existing attendance ID when getting attendance by ID then return attendance DTO")
    void givenExistingAttendanceId_whenGetAttendanceById_thenReturnAttendanceDTO() {
        // Arrange
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        // Act
        AttendanceDTO result = attendanceService.getAttendanceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getEmployeeName());
        verify(attendanceRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Given non-existent attendance ID when getting attendance by ID then throw RuntimeException")
    void givenNonExistentAttendanceId_whenGetAttendanceById_thenThrowRuntimeException() {
        // Arrange
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceById(999L);
        });
        assertTrue(exception.getMessage().contains("Attendance not found with id: 999"));
    }

    @Test
    @DisplayName("Given existing employee and date when getting attendance by employee and date then return attendance DTO")
    void givenExistingEmployeeAndDate_whenGetAttendanceByEmployeeAndDate_thenReturnAttendanceDTO() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 1);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployeeAndAttendanceDate(employee, date)).thenReturn(Optional.of(attendance));

        // Act
        AttendanceDTO result = attendanceService.getAttendanceByEmployeeAndDate(1L, date);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getEmployeeName());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).findByEmployeeAndAttendanceDate(employee, date);
    }

    @Test
    @DisplayName("Given non-existent employee when getting attendance by employee and date then throw RuntimeException")
    void givenNonExistentEmployee_whenGetAttendanceByEmployeeAndDate_thenThrowRuntimeException() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 1);
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceByEmployeeAndDate(999L, date);
        });
        assertTrue(exception.getMessage().contains("Employee not found with id: 999"));
    }

    @Test
    @DisplayName("Given existing employee but no attendance on date when getting attendance by employee and date then throw RuntimeException")
    void givenExistingEmployeeButNoAttendanceOnDate_whenGetAttendanceByEmployeeAndDate_thenThrowRuntimeException() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 1);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployeeAndAttendanceDate(employee, date)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceByEmployeeAndDate(1L, date);
        });
        assertTrue(exception.getMessage().contains("Attendance not found for employee on date: " + date));
    }

    @Test
    @DisplayName("Given existing employee with attendance records when getting attendance by employee then return list of DTOs")
    void givenExistingEmployeeWithAttendanceRecords_whenGetAttendanceByEmployee_thenReturnListOfDTOs() {
        // Arrange
        List<Attendance> attendanceList = Arrays.asList(attendance);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployee(employee)).thenReturn(attendanceList);

        // Act
        List<AttendanceDTO> result = attendanceService.getAttendanceByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("John Doe", result.get(0).getEmployeeName());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("Given existing employee with no attendance records when getting attendance by employee then return empty list")
    void givenExistingEmployeeWithNoAttendanceRecords_whenGetAttendanceByEmployee_thenReturnEmptyList() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployee(employee)).thenReturn(Collections.emptyList());

        // Act
        List<AttendanceDTO> result = attendanceService.getAttendanceByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("Given non-existent employee when getting attendance by employee then throw RuntimeException")
    void givenNonExistentEmployee_whenGetAttendanceByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceByEmployee(999L);
        });
        assertTrue(exception.getMessage().contains("Employee not found with id: 999"));
    }

    @Test
    @DisplayName("Given existing employee and valid date range when getting attendance by date range then return list of DTOs")
    void givenExistingEmployeeAndValidDateRange_whenGetAttendanceByDateRange_thenReturnListOfDTOs() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);
        List<Attendance> attendanceList = Arrays.asList(attendance);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate)).thenReturn(attendanceList);

        // Act
        List<AttendanceDTO> result = attendanceService.getAttendanceByDateRange(1L, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate);
    }

    @Test
    @DisplayName("Given existing employee and date range with no records when getting attendance by date range then return empty list")
    void givenExistingEmployeeAndDateRangeWithNoRecords_whenGetAttendanceByDateRange_thenReturnEmptyList() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate)).thenReturn(Collections.emptyList());

        // Act
        List<AttendanceDTO> result = attendanceService.getAttendanceByDateRange(1L, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate);
    }

    @Test
    @DisplayName("Given non-existent employee when getting attendance by date range then throw RuntimeException")
    void givenNonExistentEmployee_whenGetAttendanceByDateRange_thenThrowRuntimeException() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceByDateRange(999L, startDate, endDate);
        });
        assertTrue(exception.getMessage().contains("Employee not found with id: 999"));
    }

    @Test
    @DisplayName("Given valid date range when getting all attendance by date range then return list of DTOs")
    void givenValidDateRange_whenGetAllAttendanceByDateRange_thenReturnListOfDTOs() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);
        List<Attendance> attendanceList = Arrays.asList(attendance);
        when(attendanceRepository.findByAttendanceDateBetween(startDate, endDate)).thenReturn(attendanceList);

        // Act
        List<AttendanceDTO> result = attendanceService.getAllAttendanceByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(attendanceRepository, times(1)).findByAttendanceDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Given date range with no records when getting all attendance by date range then return empty list")
    void givenDateRangeWithNoRecords_whenGetAllAttendanceByDateRange_thenReturnEmptyList() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);
        when(attendanceRepository.findByAttendanceDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());

        // Act
        List<AttendanceDTO> result = attendanceService.getAllAttendanceByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attendanceRepository, times(1)).findByAttendanceDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Given existing attendance ID and valid DTO when updating attendance then return updated DTO")
    void givenExistingAttendanceIdAndValidDTO_whenUpdateAttendance_thenReturnUpdatedDTO() {
        // Arrange
        AttendanceDTO updateDTO = new AttendanceDTO();
        updateDTO.setStatus("LATE");
        updateDTO.setCheckInTime(LocalDateTime.of(2023, 10, 1, 9, 30));
        updateDTO.setCheckOutTime(LocalDateTime.of(2023, 10, 1, 17, 0));
        updateDTO.setRemarks("Late arrival");

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        // Act
        AttendanceDTO result = attendanceService.updateAttendance(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(attendanceRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Given non-existent attendance ID when updating attendance then throw RuntimeException")
    void givenNonExistentAttendanceId_whenUpdateAttendance_thenThrowRuntimeException() {
        // Arrange
        AttendanceDTO updateDTO = new AttendanceDTO();
        updateDTO.setStatus("LATE");
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.updateAttendance(999L, updateDTO);
        });
        assertTrue(exception.getMessage().contains("Attendance not found with id: 999"));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Given existing attendance ID when deleting attendance then delete successfully")
    void givenExistingAttendanceId_whenDeleteAttendance_thenReturnVoid() {
        // Arrange
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        // Act
        attendanceService.deleteAttendance(1L);

        // Assert
        verify(attendanceRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).delete(attendance);
    }

    @Test
    @DisplayName("Given non-existent attendance ID when deleting attendance then throw RuntimeException")
    void givenNonExistentAttendanceId_whenDeleteAttendance_thenThrowRuntimeException() {
        // Arrange
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.deleteAttendance(999L);
        });
        assertTrue(exception.getMessage().contains("Attendance not found with id: 999"));
        verify(attendanceRepository, never()).delete(any(Attendance.class));
    }

    @Test
    @DisplayName("Given existing employee and valid date range when getting attendance summary then return summary with correct counts")
    void givenExistingEmployeeAndValidDateRange_whenGetAttendanceSummary_thenReturnSummaryWithCorrectCounts() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);

        Attendance presentAttendance = new Attendance();
        presentAttendance.setStatus("PRESENT");
        presentAttendance.setEmployee(employee);

        Attendance lateAttendance = new Attendance();
        lateAttendance.setStatus("LATE");
        lateAttendance.setEmployee(employee);

        Attendance halfDayAttendance = new Attendance();
        halfDayAttendance.setStatus("HALF_DAY");
        halfDayAttendance.setEmployee(employee);

        Attendance onLeaveAttendance = new Attendance();
        onLeaveAttendance.setStatus("ON_LEAVE");
        onLeaveAttendance.setEmployee(employee);

        List<Attendance> attendanceList = Arrays.asList(presentAttendance, lateAttendance, halfDayAttendance, onLeaveAttendance);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of