package charliek.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

/** Tests the supported user-input cases handled by {@link DateTimeParser}. */
class DateTimeParserTest {
    /** Verifies that a date-only input produces a date without a time. */
    @Test
    void parseUserInput_dateOnly_returnsDateWithoutTime() {
        DateTimeParser.ParsedDateTime result =
                DateTimeParser.parseUserInput("2/12/2019");

        assertAll(
                () -> assertEquals(LocalDate.of(2019, 12, 2), result.date()),
                () -> assertNull(result.dateTime()),
                () -> assertFalse(result.hasTime()));
    }

    /** Verifies that a date-time input produces the expected date and time. */
    @Test
    void parseUserInput_dateAndTime_returnsDateTime() {
        DateTimeParser.ParsedDateTime result =
                DateTimeParser.parseUserInput("2/12/2019 6pm");

        assertAll(
                () -> assertNull(result.date()),
                () -> assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result.dateTime()),
                () -> assertTrue(result.hasTime()));
    }

    /** Verifies that surrounding and repeated whitespace is normalized. */
    @Test
    void parseUserInput_extraWhitespace_returnsNormalizedValue() {
        DateTimeParser.ParsedDateTime result =
                DateTimeParser.parseUserInput("  2/12/2019   6 PM  ");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result.dateTime());
    }

    /** Verifies that null, blank, and unsupported inputs are rejected. */
    @Test
    void parseUserInput_invalidInput_throwsDateTimeParseException() {
        assertAll(
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeParser.parseUserInput(null)),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeParser.parseUserInput("   ")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeParser.parseUserInput("31/02/2019")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeParser.parseUserInput("not a date")));
    }

    /** Verifies that stored ISO date and date-time values are restored correctly. */
    @Test
    void parseStored_isoValues_returnsCorrespondingDateOrDateTime() {
        DateTimeParser.ParsedDateTime date = DateTimeParser.parseStored("2019-12-02");
        DateTimeParser.ParsedDateTime dateTime =
                DateTimeParser.parseStored("2019-12-02T18:00:00");

        assertAll(
                () -> assertEquals(LocalDate.of(2019, 12, 2), date.date()),
                () -> assertNull(date.dateTime()),
                () -> assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dateTime.dateTime()),
                () -> assertNull(dateTime.date()));
    }

    /** Verifies that malformed stored values are rejected. */
    @Test
    void parseStored_invalidValue_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parseStored("2019-02-30"));
    }

    /** Verifies the user-facing formatting for both supported value shapes. */
    @Test
    void formatForDisplay_dateAndDateTime_returnsReadableValues() {
        assertAll(
                () -> assertEquals("2 Dec 2019",
                        DateTimeParser.formatForDisplay(
                                DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2)))),
                () -> assertEquals("2 Dec 2019, 18:05",
                        DateTimeParser.formatForDisplay(
                                DateTimeParser.ParsedDateTime.ofDateTime(
                                        LocalDateTime.of(2019, 12, 2, 18, 5)))));
    }

    /** Verifies the stable ISO formatting used for CSV persistence. */
    @Test
    void formatForStorage_dateAndDateTime_returnsIsoValues() {
        assertAll(
                () -> assertEquals("2019-12-02",
                        DateTimeParser.formatForStorage(
                                DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2)))),
                () -> assertEquals("2019-12-02T18:05:00",
                        DateTimeParser.formatForStorage(
                                DateTimeParser.ParsedDateTime.ofDateTime(
                                        LocalDateTime.of(2019, 12, 2, 18, 5)))));
    }

    /** Verifies that formatting methods reject absent values. */
    @Test
    void formatForDisplayOrStorage_nullValue_throwsIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.formatForDisplay(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.formatForStorage(null)));
    }

    /** Verifies the invariant that a parsed value contains exactly one date shape. */
    @Test
    void parsedDateTime_bothOrNeitherValues_throwsIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new DateTimeParser.ParsedDateTime(null, null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new DateTimeParser.ParsedDateTime(
                                LocalDate.of(2019, 12, 2),
                                LocalDateTime.of(2019, 12, 2, 18, 0))));
    }
}
