/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.google.cloud.bigquery.storage.v1beta2;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Encoder for TIME and DATETIME values, according to civil_time encoding.
 *
 * <p>The valid range and number of bits required by each date/time field is as the following:
 *
 * <table>
 *   <tr> <th> Field  </th> <th> Range          </th> <th> #Bits </th> </tr>
 *   <tr> <td> Year   </td> <td> [1, 9999]      </td> <td> 14    </td> </tr>
 *   <tr> <td> Month  </td> <td> [1, 12]        </td> <td> 4     </td> </tr>
 *   <tr> <td> Day    </td> <td> [1, 31]        </td> <td> 5     </td> </tr>
 *   <tr> <td> Hour   </td> <td> [0, 23]        </td> <td> 5     </td> </tr>
 *   <tr> <td> Minute </td> <td> [0, 59]        </td> <td> 6     </td> </tr>
 *   <tr> <td> Second </td> <td> [0, 59]*       </td> <td> 6     </td> </tr>
 *   <tr> <td> Micros </td> <td> [0, 999999]    </td> <td> 20    </td> </tr>
 *   <tr> <td> Nanos  </td> <td> [0, 999999999] </td> <td> 30    </td> </tr>
 * </table>
 *
 * <p>* Leap second is not supported.
 *
 * <p>When encoding the TIME or DATETIME into a bit field, larger date/time field is on the more
 * significant side.
 */
public final class CivilTimeEncoder {
  private static final int NANO_LENGTH = 30;
  private static final int MICRO_LENGTH = 20;
  // private static final int SECOND_LENGTH = 10;

  private static final int NANO_SHIFT = 0;
  private static final int MICRO_SHIFT = 0;
  private static final int SECOND_SHIFT = 0;
  private static final int MINUTE_SHIFT = 6;
  private static final int HOUR_SHIFT = 12;
  private static final int DAY_SHIFT = 17;
  private static final int MONTH_SHIFT = 22;
  private static final int YEAR_SHIFT = 26;

  private static final long NANO_MASK = 0x3FFFFFFFL;
  private static final long MICRO_MASK = 0xFFFFFL;
  private static final long SECOND_MASK = 0x3FL;
  private static final long MINUTE_MASK = 0xFC0L;
  private static final long HOUR_MASK = 0x1F000L;
  private static final long DAY_MASK = 0x3E0000L;
  private static final long MONTH_MASK = 0x3C00000L;
  private static final long YEAR_MASK = 0xFFFC000000L;

  private static final long TIME_SECONDS_MASK = 0x1FFFFL;
  private static final long TIME_MICROS_MASK = 0x1FFFFFFFFFL;
  private static final long TIME_NANOS_MASK = 0x7FFFFFFFFFFFL;
  private static final long DATETIME_SECONDS_MASK = 0xFFFFFFFFFFL;
  private static final long DATETIME_MICROS_MASK = 0xFFFFFFFFFFFFFFFL;

  /**
   * Encodes {@code time} as a 4-byte integer with seconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *      3         2         1
   * MSB 10987654321098765432109876543210 LSB
   *                    | H ||  M ||  S |
   * </pre>
   *
   * @see #decodePacked32TimeSecondsAsJavaTime(int)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static int encodePacked32TimeSeconds(java.time.LocalTime time) {
    checkValidTimeSeconds(time);
    int bitFieldTimeSeconds = 0x0;
    bitFieldTimeSeconds |= time.getHour() << HOUR_SHIFT;
    bitFieldTimeSeconds |= time.getMinute() << MINUTE_SHIFT;
    bitFieldTimeSeconds |= time.getSecond() << SECOND_SHIFT;
    return bitFieldTimeSeconds;
  }

  /**
   * Decodes {@code bitFieldTimeSeconds} as a {@link LocalTime} with seconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *      3         2         1
   * MSB 10987654321098765432109876543210 LSB
   *                    | H ||  M ||  S |
   * </pre>
   *
   * @see #encodePacked32TimeSeconds(LocalTime)
   * @see #encodePacked32TimeSecondsAsJavaTime(int)
   */
  @SuppressWarnings("GoodTime") // should return a java.time.LocalTime
  public static LocalTime decodePacked32TimeSeconds(int bitFieldTimeSeconds) {
    checkValidBitField(bitFieldTimeSeconds, TIME_SECONDS_MASK);
    int hourOfDay = getFieldFromBitField(bitFieldTimeSeconds, HOUR_MASK, HOUR_SHIFT);
    int minuteOfHour = getFieldFromBitField(bitFieldTimeSeconds, MINUTE_MASK, MINUTE_SHIFT);
    int secondOfMinute = getFieldFromBitField(bitFieldTimeSeconds, SECOND_MASK, SECOND_SHIFT);
    LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute);
    checkValidTimeSeconds(time);
    return time;
  }

  /**
   * Decodes {@code bitFieldTimeSeconds} as a {@link java.time.LocalTime} with seconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *      3         2         1
   * MSB 10987654321098765432109876543210 LSB
   *                    | H ||  M ||  S |
   * </pre>
   *
   * @see #encodePacked32TimeSeconds(java.time.LocalTime)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static java.time.LocalTime decodePacked32TimeSecondsAsJavaTime(int bitFieldTimeSeconds) {
    checkValidBitField(bitFieldTimeSeconds, TIME_SECONDS_MASK);
    int hourOfDay = getFieldFromBitField(bitFieldTimeSeconds, HOUR_MASK, HOUR_SHIFT);
    int minuteOfHour = getFieldFromBitField(bitFieldTimeSeconds, MINUTE_MASK, MINUTE_SHIFT);
    int secondOfMinute = getFieldFromBitField(bitFieldTimeSeconds, SECOND_MASK, SECOND_SHIFT);
    // java.time.LocalTime validates the input parameters.
    try {
      return java.time.LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute);
    } catch (java.time.DateTimeException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Encodes {@code time} as a 8-byte integer with microseconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *                                | H ||  M ||  S ||-------micros-----|
   * </pre>
   *
   * @see #decodePacked64TimeMicros(long)
   * @see #encodePacked64TimeMicros(java.time.LocalTime)
   */
  @SuppressWarnings("GoodTime") // should accept a java.time.LocalTime
  public static long encodePacked64TimeMicros(java.time.LocalTime time) {
    checkValidTimeMillis(time);
    return (((long) encodePacked32TimeSeconds(time)) << MICRO_LENGTH) | (time.getNano() / 1_000L);
  }

  /**
   * Decodes {@code bitFieldTimeMicros} as a {@link java.time.LocalTime} with microseconds
   * precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *                                | H ||  M ||  S ||-------micros-----|
   * </pre>
   *
   * @see #encodePacked64TimeMicros(java.time.LocalTime)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static java.time.LocalTime decodePacked64TimeMicrosAsJavaTime(long bitFieldTimeMicros) {
    checkValidBitField(bitFieldTimeMicros, TIME_MICROS_MASK);
    int bitFieldTimeSeconds = (int) (bitFieldTimeMicros >> MICRO_LENGTH);
    java.time.LocalTime timeSeconds = decodePacked32TimeSecondsAsJavaTime(bitFieldTimeSeconds);
    int microOfSecond = getFieldFromBitField(bitFieldTimeMicros, MICRO_MASK, MICRO_SHIFT);
    checkValidMicroOfSecond(microOfSecond);
    return timeSeconds.withNano(microOfSecond * 1_000);
  }

  /**
   * Encodes {@code time} as a 8-byte integer with nanoseconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *                      | H ||  M ||  S ||---------- nanos -----------|
   * </pre>
   *
   * @see #decodePacked64TimeNanosAsJavaTime(long)
   */
  @SuppressWarnings({"GoodTime-ApiWithNumericTimeUnit", "JavaLocalTimeGetNano"})
  public static long encodePacked64TimeNanos(java.time.LocalTime time) {
    checkValidTimeNanos(time);
    return (((long) encodePacked32TimeSeconds(time)) << NANO_LENGTH) | time.getNano();
  }

  /**
   * Decodes {@code bitFieldTimeNanos} as a {@link java.time.LocalTime} with nanoseconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *                      | H ||  M ||  S ||---------- nanos -----------|
   * </pre>
   *
   * @see #encodePacked64TimeNanos(java.time.LocalTime)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static java.time.LocalTime decodePacked64TimeNanosAsJavaTime(long bitFieldTimeNanos) {
    checkValidBitField(bitFieldTimeNanos, TIME_NANOS_MASK);
    int bitFieldTimeSeconds = (int) (bitFieldTimeNanos >> NANO_LENGTH);
    java.time.LocalTime timeSeconds = decodePacked32TimeSecondsAsJavaTime(bitFieldTimeSeconds);
    int nanoOfSecond = getFieldFromBitField(bitFieldTimeNanos, NANO_MASK, NANO_SHIFT);
    checkValidNanoOfSecond(nanoOfSecond);
    return timeSeconds.withNano(nanoOfSecond);
  }

  // tested to here???

  /**
   * Encodes {@code dateTime} as a 8-byte integer with seconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *                             |--- year ---||m || D || H ||  M ||  S |
   * </pre>
   *
   * @see #decodePacked64DatetimeSecondsAsJavaTime(long)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static long encodePacked64DatetimeSeconds(java.time.LocalDateTime dateTime) {
    checkValidDateTimeSeconds(dateTime);
    long bitFieldDatetimeSeconds = 0x0L;
    bitFieldDatetimeSeconds |= (long) dateTime.getYear() << YEAR_SHIFT;
    bitFieldDatetimeSeconds |= (long) dateTime.getMonthValue() << MONTH_SHIFT;
    bitFieldDatetimeSeconds |= (long) dateTime.getDayOfMonth() << DAY_SHIFT;
    bitFieldDatetimeSeconds |= (long) encodePacked32TimeSeconds(dateTime.toLocalTime());
    return bitFieldDatetimeSeconds;
  }

  /**
   * Decodes {@code bitFieldDatetimeSeconds} as a {@link LocalDateTime} with seconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSBa
   *                             |--- year ---||m || D || H ||  M ||  S |
   * </pre>
   *
   * @see #encodePacked64DatetimeSeconds(java.time.LocalDateTime)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static java.time.LocalDateTime decodePacked64DatetimeSecondsAsJavaTime(
      long bitFieldDatetimeSeconds) {
    checkValidBitField(bitFieldDatetimeSeconds, DATETIME_SECONDS_MASK);
    int bitFieldTimeSeconds = (int) (bitFieldDatetimeSeconds & TIME_SECONDS_MASK);
    java.time.LocalTime timeSeconds = decodePacked32TimeSecondsAsJavaTime(bitFieldTimeSeconds);
    int year = getFieldFromBitField(bitFieldDatetimeSeconds, YEAR_MASK, YEAR_SHIFT);
    int monthOfYear = getFieldFromBitField(bitFieldDatetimeSeconds, MONTH_MASK, MONTH_SHIFT);
    int dayOfMonth = getFieldFromBitField(bitFieldDatetimeSeconds, DAY_MASK, DAY_SHIFT);
    try {
      java.time.LocalDateTime dateTime =
          java.time.LocalDateTime.of(
              year,
              monthOfYear,
              dayOfMonth,
              timeSeconds.getHour(),
              timeSeconds.getMinute(),
              timeSeconds.getSecond());
      checkValidDateTimeSeconds(dateTime);
      return dateTime;
    } catch (java.time.DateTimeException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Encodes {@code dateTime} as a 8-byte integer with microseconds precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *         |--- year ---||m || D || H ||  M ||  S ||-------micros-----|
   * </pre>
   *
   * @see #decodePacked64DatetimeMicrosAsJavaTime(long)
   */
  @SuppressWarnings({"GoodTime-ApiWithNumericTimeUnit", "JavaLocalDateTimeGetNano"})
  public static long encodePacked64DatetimeMicros(java.time.LocalDateTime dateTime) {
    checkValidDateTimeMicros(dateTime);
    return (encodePacked64DatetimeSeconds(dateTime) << MICRO_LENGTH)
        | (dateTime.getNano() / 1_000L);
  }

  /**
   * Decodes {@code bitFieldDatetimeMicros} as a {@link java.time.LocalDateTime} with microseconds
   * precision.
   *
   * <p>Encoding is as the following:
   *
   * <pre>
   *        6         5         4         3         2         1
   * MSB 3210987654321098765432109876543210987654321098765432109876543210 LSB
   *         |--- year ---||m || D || H ||  M ||  S ||-------micros-----|
   * </pre>
   *
   * @see #encodePacked64DatetimeMicros(java.time.LocalDateTime)
   */
  @SuppressWarnings("GoodTime-ApiWithNumericTimeUnit")
  public static java.time.LocalDateTime decodePacked64DatetimeMicrosAsJavaTime(
      long bitFieldDatetimeMicros) {
    checkValidBitField(bitFieldDatetimeMicros, DATETIME_MICROS_MASK);
    long bitFieldDatetimeSeconds = bitFieldDatetimeMicros >> MICRO_LENGTH;
    java.time.LocalDateTime dateTimeSeconds =
        decodePacked64DatetimeSecondsAsJavaTime(bitFieldDatetimeSeconds);
    int microOfSecond = getFieldFromBitField(bitFieldDatetimeMicros, MICRO_MASK, MICRO_SHIFT);
    checkValidMicroOfSecond(microOfSecond);
    java.time.LocalDateTime dateTime = dateTimeSeconds.withNano(microOfSecond * 1_000);
    checkValidDateTimeMicros(dateTime);
    return dateTime;
  }

  private static int getFieldFromBitField(long bitField, long mask, int shift) {
    return (int) ((bitField & mask) >> shift);
  }

  private static void checkValidTimeSeconds(LocalDateTime time) {
    checkArgument(time.getHour() >= 0 && time.getHour() <= 23);
    checkArgument(time.getMinute() >= 0 && time.getMinute() <= 59);
    checkArgument(time.getSecond() >= 0 && time.getSecond() <= 59);
  }

  private static void checkValidTimeSeconds(LocalTime time) {
    checkArgument(time.getHour() >= 0 && time.getHour() <= 23);
    checkArgument(time.getMinute() >= 0 && time.getMinute() <= 59);
    checkArgument(time.getSecond() >= 0 && time.getSecond() <= 59);
  }

  private static void checkValidTimeMillis(LocalDateTime time) {
    checkValidTimeSeconds(time);
    checkArgument(time.getNano() >= 0 && time.getNano() < 999999999);
  }

  private static void checkValidTimeMillis(LocalTime time) {
    checkValidTimeSeconds(time);
    checkArgument(time.getNano() >= 0 && time.getNano() <= 999999999);
  }

  private static void checkValidTimeMicros(LocalTime time) {
    checkValidTimeSeconds(time);
    checkArgument(time.equals(time.truncatedTo(ChronoUnit.MICROS)));
  }

  private static void checkValidTimeNanos(LocalTime time) {
    checkValidTimeSeconds(time);
  }

  private static void checkValidDateTimeSeconds(LocalDateTime dateTime) {
    checkArgument(dateTime.getYear() >= 1 && dateTime.getYear() <= 9999);
    checkArgument(dateTime.getMonthValue() >= 1 && dateTime.getMonthValue() <= 12);
    checkArgument(dateTime.getDayOfMonth() >= 1 && dateTime.getDayOfMonth() <= 31);
    checkValidTimeSeconds(dateTime.toLocalTime());
  }

  private static void checkValidDateTimeMillis(LocalDateTime dateTime) {
    checkValidDateTimeSeconds(dateTime);
    checkArgument(dateTime.getNano() >= 0 && dateTime.getNano() <= 999999999);
  }

  private static void checkValidDateTimeMicros(java.time.LocalTime time) {
    checkValidTimeSeconds(time);
    checkArgument(time.equals(time.truncatedTo(ChronoUnit.MICROS)));
  }

  private static void checkValidDateTimeMicros(java.time.LocalDateTime dateTime) {
    checkValidDateTimeSeconds(dateTime);
    checkArgument(dateTime.equals(dateTime.truncatedTo(ChronoUnit.MICROS)));
  }

  private static void checkValidDateTimeNanos(java.time.LocalDateTime dateTime) {
    checkValidDateTimeSeconds(dateTime);
    checkArgument(dateTime.equals(dateTime.truncatedTo(ChronoUnit.NANOS)));
  }

  private static void checkValidMicroOfSecond(int microOfSecond) {
    checkArgument(microOfSecond >= 0 && microOfSecond <= 999999);
  }

  private static void checkValidNanoOfSecond(int nanoOfSecond) {
    checkArgument(nanoOfSecond >= 0 && nanoOfSecond <= 999999999);
  }

  private static void checkValidBitField(long bitField, long mask) {
    checkArgument((bitField & ~mask) == 0x0L);
  }

  private CivilTimeEncoder() {}
}
