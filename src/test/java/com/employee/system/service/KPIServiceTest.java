```java
package com.employee.system.service;

import com.employee.system.dto.KPIDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.KPI;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.KPIRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KPIServiceTest {

    @Mock
    private KPIRepository kpiRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private KPIService kpiService;

    // -------------------------------------------------------------------------
    // createKPI
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given valid KPIDTO with null optional fields when createKPI then return saved KPIDTO with defaults")
    void givenValidKPIDTOWithNullOptionals_whenCreateKPI_thenReturnSavedKPIDTOWithDefaults() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        KPIDTO inputDto = new KPIDTO();
        inputDto.setEmployeeId(employee.getId());
        inputDto.setKpiName("Sales Growth");
        inputDto.setKpiDescription("Increase sales by 10%");
        inputDto.setMeasurementUnit("%");
        inputDto.setTargetValue(BigDecimal.valueOf(10));
        inputDto.setCurrentValue(null); // should default to ZERO
        inputDto.setWeight(5);
        inputDto.setFrequency("Monthly");
        inputDto.setStartDate(LocalDate.now().minusMonths(1));
        inputDto.setEndDate(LocalDate.now().plusMonths(11));
        inputDto.setStatus(null); // should default to ACTIVE
        inputDto.setRemarks("Initial KPI");

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        KPI savedKPI = new KPI();
        savedKPI.setId(100L);
        savedKPI.setEmployee(employee);
        savedKPI.setKpiName(inputDto.getKpiName());
        savedKPI.setKpiDescription(inputDto.getKpiDescription());
        savedKPI.setMeasurementUnit(inputDto.getMeasurementUnit());
        savedKPI.setTargetValue(inputDto.getTargetValue());
        savedKPI.setCurrentValue(BigDecimal.ZERO);
        savedKPI.setWeight(inputDto.getWeight());
        savedKPI.setFrequency(inputDto.getFrequency());
        savedKPI.setStartDate(inputDto.getStartDate());
        savedKPI.setEndDate(inputDto.getEndDate());
        savedKPI.setStatus("ACTIVE");
        savedKPI.setRemarks(inputDto.getRemarks());

        when(kpiRepository.save(any(KPI.class))).thenReturn(savedKPI);

        // Act
        KPIDTO resultDto = kpiService.createKPI(inputDto);

        // Assert
        ArgumentCaptor<KPI> kpiCaptor = ArgumentCaptor.forClass(KPI.class);
        verify(kpiRepository, times(1)).save(kpiCaptor.capture());
        KPI capturedKPI = kpiCaptor.getValue();

        assertEquals(employee, capturedKPI.getEmployee());
        assertEquals(BigDecimal.ZERO, capturedKPI.getCurrentValue());
        assertEquals("ACTIVE", capturedKPI.getStatus());

        assertNotNull(resultDto);
        assertEquals(savedKPI.getId(), resultDto.getId());
        assertEquals(employee.getId(), resultDto.getEmployeeId());
        assertEquals("John Doe", resultDto.getEmployeeName());
        assertEquals(BigDecimal.ZERO, resultDto.getCurrentValue());
        assertEquals("ACTIVE", resultDto.getStatus());
    }

    @Test
    @DisplayName("Given non‑existing employee id when createKPI then throw RuntimeException")
    void givenNonExistingEmployeeId_whenCreateKPI_thenThrowRuntimeException() {
        // Arrange
        KPIDTO inputDto = new KPIDTO();
        inputDto.setEmployeeId(999L);
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> kpiService.createKPI(inputDto));
        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    @Test
    @DisplayName("Given null KPIDTO when createKPI then throw NullPointerException")
    void givenNullKPIDTO_whenCreateKPI_thenThrowNullPointerException() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> kpiService.createKPI(null));
    }

    // -------------------------------------------------------------------------
    // getKPIById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing KPI id when getKPIById then return corresponding KPIDTO")
    void givenExistingKPIId_whenGetKPIById_thenReturnCorrespondingKPIDTO() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(2L);
        employee.setFirstName("Alice");
        employee.setLastName("Smith");

        KPI kpi = new KPI();
        kpi.setId(200L);
        kpi.setEmployee(employee);
        kpi.setKpiName("Customer Satisfaction");
        kpi.setKpiDescription("Maintain CSAT > 85%");
        kpi.setMeasurementUnit("%");
        kpi.setTargetValue(BigDecimal.valueOf(85));
        kpi.setCurrentValue(BigDecimal.valueOf(80));
        kpi.setWeight(3);
        kpi.setFrequency("Quarterly");
        kpi.setStatus("ACTIVE");

        when(kpiRepository.findById(200L)).thenReturn(Optional.of(kpi));

        // Act
        KPIDTO resultDto = kpiService.getKPIById(200L);

        // Assert
        verify(kpiRepository, times(1)).findById(200L);
        assertNotNull(resultDto);
        assertEquals(kpi.getId(), resultDto.getId());
        assertEquals(employee.getId(), resultDto.getEmployeeId());
        assertEquals("Alice Smith", resultDto.getEmployeeName());
        assertEquals(kpi.getKpiName(), resultDto.getKpiName());
    }

    @Test
    @DisplayName("Given non‑existing KPI id when getKPIById then throw RuntimeException")
    void givenNonExistingKPIId_whenGetKPIById_thenThrowRuntimeException() {
        // Arrange
        when(kpiRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> kpiService.getKPIById(999L));
        assertTrue(ex.getMessage().contains("KPI not found"));
    }

    // -------------------------------------------------------------------------
    // getKPIsByEmployee
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing employee with KPIs when getKPIsByEmployee then return populated list")
    void givenExistingEmployeeWithKPIs_whenGetKPIsByEmployee_thenReturnPopulatedList() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(3L);
        employee.setFirstName("Bob");
        employee.setLastName("Brown");

        KPI kpi1 = new KPI();
        kpi1.setId(301L);
        kpi1.setEmployee(employee);
        kpi1.setKpiName("Efficiency");
        kpi1.setStatus("ACTIVE");

        KPI kpi2 = new KPI();
        kpi2.setId(302L);
        kpi2.setEmployee(employee);
        kpi2.setKpiName("Quality");
        kpi2.setStatus("ACTIVE");

        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
        when(kpiRepository.findByEmployee(employee)).thenReturn(List.of(kpi1, kpi2));

        // Act
        List<KPIDTO> result = kpiService.getKPIsByEmployee(3L);

        // Assert
        verify(employeeRepository, times(1)).findById(3L);
        verify(kpiRepository, times(1)).findByEmployee(employee);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getKpiName().equals("Efficiency")));
        assertTrue(result.stream().anyMatch(dto -> dto.getKpiName().equals("Quality")));
    }

    @Test
    @DisplayName("Given existing employee without KPIs when getKPIsByEmployee then return empty list")
    void givenExistingEmployeeWithoutKPIs_whenGetKPIsByEmployee_thenReturnEmptyList() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(4L);
        employee.setFirstName("Carol");
        employee.setLastName("White");

        when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
        when(kpiRepository.findByEmployee(employee)).thenReturn(Collections.emptyList());

        // Act
        List<KPIDTO> result = kpiService.getKPIsByEmployee(4L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given non‑existing employee when getKPIsByEmployee then throw RuntimeException")
    void givenNonExistingEmployee_whenGetKPIsByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> kpiService.getKPIsByEmployee(999L));
        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    // -------------------------------------------------------------------------
    // getActiveKPIs
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given active KPIs exist when getActiveKPIs then return list of active KPIDTOs")
    void givenActiveKPIsExist_whenGetActiveKPIs_thenReturnListOfActiveKPIDTOs() {
        // Arrange
        Employee emp = new Employee();
        emp.setId(5L);
        emp.setFirstName("Dave");
        emp.setLastName("Green");

        KPI activeKPI = new KPI();
        activeKPI.setId(501L);
        activeKPI.setEmployee(emp);
        activeKPI.setStatus("ACTIVE");
        activeKPI.setKpiName("Revenue");

        when(kpiRepository.findByStatus("ACTIVE")).thenReturn(List.of(activeKPI));

        // Act
        List<KPIDTO> result = kpiService.getActiveKPIs();

        // Assert
        verify(kpiRepository, times(1)).findByStatus("ACTIVE");
        assertEquals(1, result.size());
        assertEquals("Revenue", result.get(0).getKpiName());
        assertEquals("ACTIVE", result.get(0).getStatus());
    }

    @Test
    @DisplayName("Given no active KPIs when getActiveKPIs then return empty list")
    void givenNoActiveKPIs_whenGetActiveKPIs_thenReturnEmptyList() {
        // Arrange
        when(kpiRepository.findByStatus("ACTIVE")).thenReturn(Collections.emptyList());

        // Act
        List<KPIDTO> result = kpiService.getActiveKPIs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getKPIsByFrequency
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given KPIs for given frequency when getKPIsByFrequency then return matching list")
    void givenKPIsForGivenFrequency_whenGetKPIsByFrequency_thenReturnMatchingList() {
        // Arrange
        Employee emp = new Employee();
        emp.setId(6L);
        emp.setFirstName("Eve");
        emp.setLastName("Black");

        KPI weeklyKPI = new KPI();
        weeklyKPI.setId(601L);
        weeklyKPI.setEmployee(emp);
        weeklyKPI.setFrequency("Weekly");
        weeklyKPI.setKpiName("Bug Fix Rate");

        when(kpiRepository.findByFrequency("Weekly")).thenReturn(List.of(weeklyKPI));

        // Act
        List<KPIDTO> result = kpiService.getKPIsByFrequency("Weekly");

        // Assert
        verify(kpiRepository, times(1)).findByFrequency("Weekly");
        assertEquals(1, result.size());
        assertEquals("Bug Fix Rate", result.get(0).getKpiName());
        assertEquals("Weekly", result.get(0).getFrequency());
    }

    @Test
    @DisplayName("Given no KPIs for given frequency when getKPIsByFrequency then return empty list")
    void givenNoKPIsForGivenFrequency_whenGetKPIsByFrequency_thenReturnEmptyList() {
        // Arrange
        when(kpiRepository.findByFrequency("Yearly")).thenReturn(Collections.emptyList());

        // Act
        List<KPIDTO> result = kpiService.getKPIsByFrequency("Yearly");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // updateKPI
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Given existing KPI and valid DTO when updateKPI then return updated KPIDTO")
    void givenExistingKPIAndValidDTO_whenUpdateKPI_thenReturnUpdatedKPIDTO() {
        // Arrange
        KPI existingKPI = new KPI();
        existingKPI.setId(701L);
        existingKPI.setKpiName("Old Name");
        existingKPI.setStatus("ACTIVE