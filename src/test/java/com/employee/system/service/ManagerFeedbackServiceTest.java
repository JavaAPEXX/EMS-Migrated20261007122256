package com.employee.system.service;

import com.employee.system.dto.ManagerFeedbackDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.ManagerFeedback;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.ManagerFeedbackRepository;
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
class ManagerFeedbackServiceTest {

    @Mock
    private ManagerFeedbackRepository feedbackRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ManagerFeedbackService managerFeedbackService;

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

    private ManagerFeedbackDTO buildFeedbackDTO(Long employeeId, Long providedById) {
        ManagerFeedbackDTO dto = new ManagerFeedbackDTO();
        dto.setEmployeeId(employeeId);
        dto.setProvidedById(providedById);
        dto.setFeedbackType("POSITIVE");
        dto.setFeedbackCategory("TEAMWORK");
        dto.setFeedbackText("Great collaboration");
        dto.setRating(5);
        dto.setActionItems("Continue mentoring");
        // leave feedbackDate and status null to test defaults
        return dto;
    }

    private ManagerFeedback buildFeedback(Long id, Employee employee, Employee manager) {
        ManagerFeedback f = new ManagerFeedback();
        f.setId(id);
        f.setEmployee(employee);
        f.setProvidedBy(manager);
        f.setFeedbackType("POSITIVE");
        f.setFeedbackCategory("TEAMWORK");
        f.setFeedbackText("Great collaboration");
        f.setRating(5);
        f.setActionItems("Continue mentoring");
        f.setFeedbackDate(LocalDate.of(2023, 1, 1));
        f.setStatus("COMPLETED");
        f.setCreatedAt(LocalDate.now().atStartOfDay());
        f.setUpdatedAt(LocalDate.now().atStartOfDay());
        return f;
    }

    // -------------------------------------------------------------------------
    // provideFeedback
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given valid DTO when provideFeedback then returns saved DTO with defaults")
    void givenValidDto_whenProvideFeedback_thenReturnSavedDto() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedbackDTO inputDto = buildFeedbackDTO(employee.getId(), manager.getId());

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(manager.getId())).thenReturn(Optional.of(manager));

        ArgumentCaptor<ManagerFeedback> captor = ArgumentCaptor.forClass(ManagerFeedback.class);
        ManagerFeedback savedEntity = buildFeedback(10L, employee, manager);
        when(feedbackRepository.save(captor.capture())).thenReturn(savedEntity);

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.provideFeedback(inputDto);

        // Assert
        ManagerFeedback captured = captor.getValue();
        assertEquals(employee, captured.getEmployee());
        assertEquals(manager, captured.getProvidedBy());
        assertEquals("POSITIVE", captured.getFeedbackType());
        assertEquals("DRAFT", captured.getStatus()); // default applied
        assertEquals(LocalDate.now(), captured.getFeedbackDate()); // default applied

        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(manager.getId(), result.getProvidedById());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("Jane Smith", result.getProvidedByName());
    }

    @Test
    @DisplayName("Given non‑existing employee when provideFeedback then throws RuntimeException")
    void givenNonExistingEmployee_whenProvideFeedback_thenThrowException() {
        // Arrange
        ManagerFeedbackDTO dto = buildFeedbackDTO(99L, 2L);
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.provideFeedback(dto));
        assertTrue(ex.getMessage().contains("Employee not found with id: 99"));
        verify(employeeRepository, times(1)).findById(99L);
        verifyNoMoreInteractions(employeeRepository, feedbackRepository);
    }

    @Test
    @DisplayName("Given non‑existing manager when provideFeedback then throws RuntimeException")
    void givenNonExistingManager_whenProvideFeedback_thenThrowException() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        ManagerFeedbackDTO dto = buildFeedbackDTO(employee.getId(), 99L);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.provideFeedback(dto));
        assertTrue(ex.getMessage().contains("Manager not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // getFeedbackById
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing feedback id when getFeedbackById then returns DTO")
    void givenExistingId_whenGetFeedbackById_thenReturnDto() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedback feedback = buildFeedback(10L, employee, manager);
        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.getFeedbackById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(manager.getId(), result.getProvidedById());
        assertEquals("POSITIVE", result.getFeedbackType());
    }

    @Test
    @DisplayName("Given non‑existing feedback id when getFeedbackById then throws RuntimeException")
    void givenNonExistingId_whenGetFeedbackById_thenThrowException() {
        // Arrange
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.getFeedbackById(99L));
        assertTrue(ex.getMessage().contains("Manager feedback not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // getFeedbackByEmployee
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing employee when getFeedbackByEmployee then returns list of DTOs")
    void givenExistingEmployee_whenGetFeedbackByEmployee_thenReturnDtoList() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedback feedback = buildFeedback(10L, employee, manager);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployee(employee)).thenReturn(List.of(feedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByEmployee(employee.getId());

        // Assert
        assertEquals(1, result.size());
        ManagerFeedbackDTO dto = result.get(0);
        assertEquals(10L, dto.getId());
        assertEquals(employee.getId(), dto.getEmployeeId());
    }

    @Test
    @DisplayName("Given non‑existing employee when getFeedbackByEmployee then throws RuntimeException")
    void givenNonExistingEmployee_whenGetFeedbackByEmployee_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.getFeedbackByEmployee(99L));
        assertTrue(ex.getMessage().contains("Employee not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // getFeedbackByManager
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing manager when getFeedbackByManager then returns list of DTOs")
    void givenExistingManager_whenGetFeedbackByManager_thenReturnDtoList() {
        // Arrange
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        Employee employee = buildEmployee(1L, "John", "Doe");
        ManagerFeedback feedback = buildFeedback(10L, employee, manager);
        when(employeeRepository.findById(manager.getId())).thenReturn(Optional.of(manager));
        when(feedbackRepository.findByProvidedBy(manager)).thenReturn(List.of(feedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByManager(manager.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(manager.getId(), result.get(0).getProvidedById());
    }

    @Test
    @DisplayName("Given non‑existing manager when getFeedbackByManager then throws RuntimeException")
    void givenNonExistingManager_whenGetFeedbackByManager_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.getFeedbackByManager(99L));
        assertTrue(ex.getMessage().contains("Manager not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // getFeedbackByType
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing type when getFeedbackByType then returns matching DTOs")
    void givenExistingType_whenGetFeedbackByType_thenReturnDtoList() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedback feedback = buildFeedback(10L, employee, manager);
        when(feedbackRepository.findByFeedbackType("POSITIVE")).thenReturn(List.of(feedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByType("POSITIVE");

        // Assert
        assertEquals(1, result.size());
        assertEquals("POSITIVE", result.get(0).getFeedbackType());
    }

    @Test
    @DisplayName("Given type with no feedback when getFeedbackByType then returns empty list")
    void givenNoFeedbackForType_whenGetFeedbackByType_thenReturnEmptyList() {
        // Arrange
        when(feedbackRepository.findByFeedbackType("NEGATIVE")).thenReturn(Collections.emptyList());

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByType("NEGATIVE");

        // Assert
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getFeedbackByCategory
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing employee and category when getFeedbackByCategory then returns DTO list")
    void givenExistingEmployeeAndCategory_whenGetFeedbackByCategory_thenReturnDtoList() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedback feedback = buildFeedback(10L, employee, manager);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployeeAndCategory(employee, "TEAMWORK"))
                .thenReturn(List.of(feedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByCategory(employee.getId(), "TEAMWORK");

        // Assert
        assertEquals(1, result.size());
        assertEquals("TEAMWORK", result.get(0).getFeedbackCategory());
    }

    @Test
    @DisplayName("Given non‑existing employee when getFeedbackByCategory then throws RuntimeException")
    void givenNonExistingEmployee_whenGetFeedbackByCategory_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.getFeedbackByCategory(99L, "ANY"));
        assertTrue(ex.getMessage().contains("Employee not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // updateFeedback
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing feedback id and DTO when updateFeedback then returns updated DTO")
    void givenExistingIdAndDto_whenUpdateFeedback_thenReturnUpdatedDto() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedback existing = buildFeedback(10L, employee, manager);
        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(existing));

        ManagerFeedbackDTO updateDto = new ManagerFeedbackDTO();
        updateDto.setFeedbackType("NEGATIVE");
        updateDto.setFeedbackCategory("COMMUNICATION");
        updateDto.setFeedbackText("Needs improvement");
        updateDto.setRating(2);
        updateDto.setActionItems("Attend workshop");
        updateDto.setStatus("REVIEWED");

        ArgumentCaptor<ManagerFeedback> captor = ArgumentCaptor.forClass(ManagerFeedback.class);
        when(feedbackRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.updateFeedback(10L, updateDto);

        // Assert
        ManagerFeedback saved = captor.getValue();
        assertEquals("NEGATIVE", saved.getFeedbackType());
        assertEquals("COMMUNICATION", saved.getFeedbackCategory());
        assertEquals("Needs improvement", saved.getFeedbackText());
        assertEquals(2, saved.getRating());
        assertEquals("Attend workshop", saved.getActionItems());
        assertEquals("REVIEWED", saved.getStatus());

        assertEquals("NEGATIVE", result.getFeedbackType());
        assertEquals("COMMUNICATION", result.getFeedbackCategory());
    }

    @Test
    @DisplayName("Given non‑existing feedback id when updateFeedback then throws RuntimeException")
    void givenNonExistingId_whenUpdateFeedback_thenThrowException() {
        // Arrange
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        ManagerFeedbackDTO dto = new ManagerFeedbackDTO();

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.updateFeedback(99L, dto));
        assertTrue(ex.getMessage().contains("Manager feedback not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // deleteFeedback
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given existing feedback id when deleteFeedback then repository delete is invoked")
    void givenExistingId_whenDeleteFeedback_thenRepositoryDeleteInvoked() {
        // Arrange
        Employee employee = buildEmployee(1L, "John", "Doe");
        Employee manager = buildEmployee(2L, "Jane", "Smith");
        ManagerFeedback feedback = buildFeedback(10L, employee, manager);
        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));

        // Act
        managerFeedbackService.deleteFeedback(10L);

        // Assert
        verify(feedbackRepository, times(1)).delete(feedback);
    }

    @Test
    @DisplayName("Given non‑existing feedback id when deleteFeedback then throws RuntimeException")
    void givenNonExistingId_whenDeleteFeedback_thenThrowException() {
        // Arrange
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> managerFeedbackService.deleteFeedback(99L));
        assertTrue(ex.getMessage().contains("Manager feedback not found with id: 99"));
    }

    // -------------------------------------------------------------------------
    // Null input edge case for provideFeedback
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Given null DTO when provideFeedback then throws NullPointerException")
    void givenNullDto_whenProvideFeedback_thenThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> managerFeedbackService.provideFeedback(null));
    }
}