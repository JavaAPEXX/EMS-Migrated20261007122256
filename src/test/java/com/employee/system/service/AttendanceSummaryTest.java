```java
package com.employee.system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AttendanceSummary}.
 * Since AttendanceSummary is a Lombok-generated POJO with @Data and @Builder,
 * we test the builder pattern, getters, setters, equals, hashCode, and toString methods.
 */
class AttendanceSummaryTest {

    @Test
    @DisplayName("Given valid attendance data, when building summary, then return summary with all fields set")
    void givenValidAttendanceData_whenBuildingSummary_thenReturnSummaryWithAllFieldsSet() {
        // Arrange
        Long employeeId = 1001L;
        long totalDays = 30L;
        long presentDays = 25L;
        long absentDays = 2L;
        long lateDays = 1L;
        long halfDays = 1L;
        long leaveDays = 1L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .halfDays(halfDays)
                .leaveDays(leaveDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(totalDays, summary.getTotalDays());
        assertEquals(presentDays, summary.getPresentDays());
        assertEquals(absentDays, summary.getAbsentDays());
        assertEquals(lateDays, summary.getLateDays());
        assertEquals(halfDays, summary.getHalfDays());
        assertEquals(leaveDays, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given null employeeId, when building summary, then return summary with null employeeId")
    void givenNullEmployeeId_whenBuildingSummary_thenReturnSummaryWithNullEmployeeId() {
        // Arrange
        long totalDays = 20L;
        long presentDays = 18L;
        long absentDays = 1L;
        long lateDays = 1L;
        long halfDays = 0L;
        long leaveDays = 0L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(null)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .halfDays(halfDays)
                .leaveDays(leaveDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertNull(summary.getEmployeeId());
        assertEquals(totalDays, summary.getTotalDays());
        assertEquals(presentDays, summary.getPresentDays());
        assertEquals(absentDays, summary.getAbsentDays());
        assertEquals(lateDays, summary.getLateDays());
        assertEquals(halfDays, summary.getHalfDays());
        assertEquals(leaveDays, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given zero values for all days, when building summary, then return summary with zero day counts")
    void givenZeroValuesForAllDays_whenBuildingSummary_thenReturnSummaryWithZeroDayCounts() {
        // Arrange
        Long employeeId = 2002L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(0L)
                .presentDays(0L)
                .absentDays(0L)
                .lateDays(0L)
                .halfDays(0L)
                .leaveDays(0L)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(0L, summary.getTotalDays());
        assertEquals(0L, summary.getPresentDays());
        assertEquals(0L, summary.getAbsentDays());
        assertEquals(0L, summary.getLateDays());
        assertEquals(0L, summary.getHalfDays());
        assertEquals(0L, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given large boundary values, when building summary, then return summary with correct large values")
    void givenLargeBoundaryValues_whenBuildingSummary_thenReturnSummaryWithCorrectLargeValues() {
        // Arrange
        Long employeeId = 999999999L;
        long totalDays = Long.MAX_VALUE;
        long presentDays = Long.MAX_VALUE - 1;
        long absentDays = 1L;
        long lateDays = 0L;
        long halfDays = 0L;
        long leaveDays = 0L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .halfDays(halfDays)
                .leaveDays(leaveDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(totalDays, summary.getTotalDays());
        assertEquals(presentDays, summary.getPresentDays());
        assertEquals(absentDays, summary.getAbsentDays());
        assertEquals(lateDays, summary.getLateDays());
        assertEquals(halfDays, summary.getHalfDays());
        assertEquals(leaveDays, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given negative day values, when building summary, then return summary with negative values")
    void givenNegativeDayValues_whenBuildingSummary_thenReturnSummaryWithNegativeValues() {
        // Arrange
        Long employeeId = 3003L;
        long totalDays = -10L;
        long presentDays = -5L;
        long absentDays = -3L;
        long lateDays = -1L;
        long halfDays = -1L;
        long leaveDays = 0L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .halfDays(halfDays)
                .leaveDays(leaveDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(totalDays, summary.getTotalDays());
        assertEquals(presentDays, summary.getPresentDays());
        assertEquals(absentDays, summary.getAbsentDays());
        assertEquals(lateDays, summary.getLateDays());
        assertEquals(halfDays, summary.getHalfDays());
        assertEquals(leaveDays, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given two identical summaries, when comparing with equals, then return true")
    void givenTwoIdenticalSummaries_whenComparingWithEquals_thenReturnTrue() {
        // Arrange
        AttendanceSummary summary1 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        AttendanceSummary summary2 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        boolean result = summary1.equals(summary2);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Given two different summaries, when comparing with equals, then return false")
    void givenTwoDifferentSummaries_whenComparingWithEquals_thenReturnFalse() {
        // Arrange
        AttendanceSummary summary1 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        AttendanceSummary summary2 = AttendanceSummary.builder()
                .employeeId(1002L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        boolean result = summary1.equals(summary2);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Given a summary and null, when comparing with equals, then return false")
    void givenASummaryAndNull_whenComparingWithEquals_thenReturnFalse() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        boolean result = summary.equals(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Given a summary and a different type object, when comparing with equals, then return false")
    void givenASummaryAndDifferentTypeObject_whenComparingWithEquals_thenReturnFalse() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        Object differentObject = new Object();

        // Act
        boolean result = summary.equals(differentObject);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Given two identical summaries, when comparing hashCodes, then return equal hashCodes")
    void givenTwoIdenticalSummaries_whenComparingHashCodes_thenReturnEqualHashCodes() {
        // Arrange
        AttendanceSummary summary1 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        AttendanceSummary summary2 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        int hashCode1 = summary1.hashCode();
        int hashCode2 = summary2.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Given two different summaries, when comparing hashCodes, then return different hashCodes")
    void givenTwoDifferentSummaries_whenComparingHashCodes_thenReturnDifferentHashCodes() {
        // Arrange
        AttendanceSummary summary1 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        AttendanceSummary summary2 = AttendanceSummary.builder()
                .employeeId(1002L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        int hashCode1 = summary1.hashCode();
        int hashCode2 = summary2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Given a summary, when calling toString, then return non-null string containing field values")
    void givenASummary_whenCallingToString_thenReturnNonNullStringContainingFieldValues() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        String result = summary.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("1001"));
        assertTrue(result.contains("30"));
        assertTrue(result.contains("25"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("1"));
    }

    @Test
    @DisplayName("Given a summary with null employeeId, when calling toString, then return non-null string")
    void givenASummaryWithNullEmployeeId_whenCallingToString_thenReturnNonNullString() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(null)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        String result = summary.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("null"));
    }

    @Test
    @DisplayName("Given a summary, when setting new values via setters, then return updated values")
    void givenASummary_whenSettingNewValuesViaSetters_thenReturnUpdatedValues() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        summary.setEmployeeId(2002L);
        summary.setTotalDays(28L);
        summary.setPresentDays(24L);
        summary.setAbsentDays(3L);
        summary.setLateDays(1L);
        summary.setHalfDays(0L);
        summary.setLeaveDays(0L);

        // Assert
        assertEquals(2002L, summary.getEmployeeId());
        assertEquals(28L, summary.getTotalDays());
        assertEquals(24L, summary.getPresentDays());
        assertEquals(3L, summary.getAbsentDays());
        assertEquals(1L, summary.getLateDays());
        assertEquals(0L, summary.getHalfDays());
        assertEquals(0L, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given a summary, when setting null employeeId via setter, then return null employeeId")
    void givenASummary_whenSettingNullEmployeeIdViaSetter_thenReturnNullEmployeeId() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        summary.setEmployeeId(null);

        // Assert
        assertNull(summary.getEmployeeId());
    }

    @Test
    @DisplayName("Given a summary, when calling canEqual with identical summary, then return true")
    void givenASummary_whenCallingCanEqualWithIdenticalSummary_thenReturnTrue() {
        // Arrange
        AttendanceSummary summary1 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        AttendanceSummary summary2 = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        // Act
        boolean result = summary1.canEqual(summary2);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Given a summary, when calling canEqual with different type, then return false")
    void givenASummary_whenCallingCanEqualWithDifferentType_thenReturnFalse() {
        // Arrange
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(1001L)
                .totalDays(30L)
                .presentDays(25L)
                .absentDays(2L)
                .lateDays(1L)
                .halfDays(1L)
                .leaveDays(1L)
                .build();

        Object differentObject = new Object();

        // Act
        boolean result = summary.canEqual(differentObject);

        // Assert
        assertFalse(result