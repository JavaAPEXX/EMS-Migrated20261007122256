import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.AttendanceSummary;

public class AttendanceSummaryTest {

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
        assertFalse(result);
    }

    @Test
    @DisplayName("Given a summary, when calling canEqual with null, then return false")
    void givenASummary_whenCallingCanEqualWithNull_thenReturnFalse() {
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
        boolean result = summary.canEqual(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Given a summary, when calling canEqual with different class, then return false")
    void givenASummary_whenCallingCanEqualWithDifferentClass_thenReturnFalse() {
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
        boolean result = summary.canEqual(new Object());

        // Assert
        assertFalse(result);
    }
}
