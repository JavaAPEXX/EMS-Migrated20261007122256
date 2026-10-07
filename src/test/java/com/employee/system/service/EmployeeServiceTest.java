```java
package com.employee.system.service;

import com.employee.system.dto.EmployeeDTO;
import com.employee.system.entity.Employee;
import com.employee.system.repository.EmployeeRepository;
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
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    // -------------------------------------------------------------------------
    // Helper methods to create test objects
    // -------------------------------------------------------------------------
    private EmployeeDTO buildEmployeeDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId("E123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setDepartment("Engineering");
        dto.setDesignation("Developer");
        dto.setDateOfJoining(LocalDate.of(2020, 1, 15));
        dto.setStatus("ACTIVE");
        dto.setAddress("123 Main St");
        dto.setDateOfBirth(LocalDate.of(1990, 5, 20));
        dto.setGender("Male");
        return dto;
    }

    private Employee buildEmployee(Long id) {
        Employee emp = new Employee();
        emp.setId(id);
        emp.setEmployeeId("E123");
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setEmail("john.doe@example.com");
        emp.setPhoneNumber("1234567890");
        emp.setDepartment("Engineering");
        emp.setDesignation("Developer");
        emp.setDateOfJoining(LocalDate.of(2020, 1, 15));
        emp.setStatus("ACTIVE");
        emp.setAddress("123 Main St");
        emp.setDateOfBirth(LocalDate.of(1990, 5, 20));
        emp.setGender("Male");
        return emp;
    }

    // -------------------------------------------------------------------------
    // createEmployee
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given valid EmployeeDTO when createEmployee then return EmployeeDTO with generated id")
    void givenValidEmployeeDTO_whenCreateEmployee_thenReturnEmployeeDTO() {
        // Arrange
        EmployeeDTO inputDto = buildEmployeeDTO();
        Employee savedEntity = buildEmployee(1L);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEntity);

        // Act
        EmployeeDTO result = employeeService.createEmployee(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(inputDto.getEmployeeId(), result.getEmployeeId());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given EmployeeDTO with null status when createEmployee then default status is ACTIVE")
    void givenEmployeeDTOWithNullStatus_whenCreateEmployee_thenDefaultStatusIsActive() {
        // Arrange
        EmployeeDTO inputDto = buildEmployeeDTO();
        inputDto.setStatus(null);
        Employee savedEntity = buildEmployee(2L);
        savedEntity.setStatus("ACTIVE"); // repository would return the entity as saved
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEntity);

        // Act
        EmployeeDTO result = employeeService.createEmployee(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertNull(inputDto.getStatus());
        assertEquals("ACTIVE", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Given null EmployeeDTO when createEmployee then throw NullPointerException")
    void givenNullEmployeeDTO_whenCreateEmployee_thenThrowNullPointerException() {
        // Arrange
        EmployeeDTO inputDto = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> employeeService.createEmployee(inputDto));
        verifyNoInteractions(employeeRepository);
    }

    // -------------------------------------------------------------------------
    // getEmployeeById
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing id when getEmployeeById then return matching EmployeeDTO")
    void givenExistingId_whenGetEmployeeById_thenReturnEmployeeDTO() {
        // Arrange
        Long id = 1L;
        Employee employee = buildEmployee(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO result = employeeService.getEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given non‑existing id when getEmployeeById then throw RuntimeException")
    void givenNonExistingId_whenGetEmployeeById_thenThrowRuntimeException() {
        // Arrange
        Long id = 99L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> employeeService.getEmployeeById(id));
        assertTrue(ex.getMessage().contains("Employee not found with id"));
        verify(employeeRepository, times(1)).findById(id);
    }

    // -------------------------------------------------------------------------
    // getEmployeeByEmployeeId
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing employeeId when getEmployeeByEmployeeId then return matching EmployeeDTO")
    void givenExistingEmployeeId_whenGetEmployeeByEmployeeId_thenReturnEmployeeDTO() {
        // Arrange
        String empId = "E123";
        Employee employee = buildEmployee(1L);
        when(employeeRepository.findByEmployeeId(empId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO result = employeeService.getEmployeeByEmployeeId(empId);

        // Assert
        assertNotNull(result);
        assertEquals(empId, result.getEmployeeId());
        verify(employeeRepository, times(1)).findByEmployeeId(empId);
    }

    @Test
    @DisplayName("Given non‑existing employeeId when getEmployeeByEmployeeId then throw RuntimeException")
    void givenNonExistingEmployeeId_whenGetEmployeeByEmployeeId_thenThrowRuntimeException() {
        // Arrange
        String empId = "UNKNOWN";
        when(employeeRepository.findByEmployeeId(empId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> employeeService.getEmployeeByEmployeeId(empId));
        assertTrue(ex.getMessage().contains("Employee not found with employee id"));
        verify(employeeRepository, times(1)).findByEmployeeId(empId);
    }

    // -------------------------------------------------------------------------
    // getAllEmployees
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given no employees when getAllEmployees then return empty list")
    void givenNoEmployees_whenGetAllEmployees_thenReturnEmptyList() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given multiple employees when getAllEmployees then return list of DTOs")
    void givenMultipleEmployees_whenGetAllEmployees_thenReturnListOfDTOs() {
        // Arrange
        Employee emp1 = buildEmployee(1L);
        Employee emp2 = buildEmployee(2L);
        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

        // Act
        List<EmployeeDTO> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(employeeRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // getEmployeesByDepartment
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given department when getEmployeesByDepartment then return matching DTOs")
    void givenDepartment_whenGetEmployeesByDepartment_thenReturnMatchingDTOs() {
        // Arrange
        String dept = "Engineering";
        Employee emp = buildEmployee(1L);
        when(employeeRepository.findByDepartment(dept)).thenReturn(List.of(emp));

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDepartment(dept);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dept, result.get(0).getDepartment());
        verify(employeeRepository, times(1)).findByDepartment(dept);
    }

    // -------------------------------------------------------------------------
    // getEmployeesByDesignation
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given designation when getEmployeesByDesignation then return matching DTOs")
    void givenDesignation_whenGetEmployeesByDesignation_thenReturnMatchingDTOs() {
        // Arrange
        String desig = "Developer";
        Employee emp = buildEmployee(1L);
        when(employeeRepository.findByDesignation(desig)).thenReturn(List.of(emp));

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDesignation(desig);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(desig, result.get(0).getDesignation());
        verify(employeeRepository, times(1)).findByDesignation(desig);
    }

    // -------------------------------------------------------------------------
    // searchEmployeesByName
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given name fragment when searchEmployeesByName then return matching DTOs")
    void givenName_whenSearchEmployeesByName_thenReturnMatchingDTOs() {
        // Arrange
        String name = "John";
        Employee emp = buildEmployee(1L);
        when(employeeRepository.searchByName(name)).thenReturn(List.of(emp));

        // Act
        List<EmployeeDTO> result = employeeService.searchEmployeesByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getFirstName().contains(name));
        verify(employeeRepository, times(1)).searchByName(name);
    }

    // -------------------------------------------------------------------------
    // updateEmployee
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing id and update DTO when updateEmployee then return updated DTO")
    void givenExistingIdAndUpdateDTO_whenUpdateEmployee_thenReturnUpdatedDTO() {
        // Arrange
        Long id = 1L;
        Employee existing = buildEmployee(id);
        EmployeeDTO updateDto = buildEmployeeDTO();
        updateDto.setFirstName("Jane");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmployeeDTO result = employeeService.updateEmployee(id, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Given non‑existing id when updateEmployee then throw RuntimeException")
    void givenNonExistingId_whenUpdateEmployee_thenThrowRuntimeException() {
        // Arrange
        Long id = 99L;
        EmployeeDTO updateDto = buildEmployeeDTO();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> employeeService.updateEmployee(id, updateDto));
        assertTrue(ex.getMessage().contains("Employee not found with id"));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteEmployee
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing id when deleteEmployee then repository delete is invoked")
    void givenExistingId_whenDeleteEmployee_thenRepositoryDeleteInvoked() {
        // Arrange
        Long id = 1L;
        Employee employee = buildEmployee(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        // Act
        employeeService.deleteEmployee(id);

        // Assert
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Given non‑existing id when deleteEmployee then throw RuntimeException")
    void givenNonExistingId_whenDeleteEmployee_thenThrowRuntimeException() {
        // Arrange
        Long id = 99L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> employeeService.deleteEmployee(id));
        assertTrue(ex.getMessage().contains("Employee not found with id"));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // getActiveEmployeesCount
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given repository count when getActiveEmployeesCount then return same count")
    void givenRepositoryCount_whenGetActiveEmployeesCount_thenReturnCount() {
        // Arrange
        long expectedCount = 5L;
        when(employeeRepository.countActiveEmployees()).thenReturn(expectedCount);

        // Act
        long result = employeeService.getActiveEmployeesCount();

        // Assert
        assertEquals(expectedCount, result);
        verify(employeeRepository, times(1)).countActiveEmployees();
    }

    // -------------------------------------------------------------------------
    // changeEmployeeStatus
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing id and new status when changeEmployeeStatus then return DTO with updated status")
    void givenExistingIdAndNewStatus_whenChangeEmployeeStatus_thenReturnDTOWithNewStatus() {
        // Arrange
        Long id = 1L;
        String newStatus = "INACTIVE";
        Employee employee = buildEmployee(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmployeeDTO result = employeeService.changeEmployeeStatus(id, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    @DisplayName("Given non‑existing id when changeEmployeeStatus then throw RuntimeException")
    void givenNonExistingId_whenChangeEmployeeStatus_thenThrowRuntimeException() {
        // Arrange
        Long