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
 */

package com.google.cloud.bigquery.storage.v1beta2;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

@RunWith(JUnit4.class)
public class CivilTimeEncoderTest {
  private static final Logger LOG = Logger.getLogger(CivilTimeEncoderTest.class.getName());

  @Test
  public void Packed64TimeMicrosTest() {
    // 00:00:00.000000
    // 0b000000000000000000000000000|00000|000000|000000|00000000000000000000
    // 0x0
    assertEquals(0x0L, CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(0, 0, 0, 0)));
    // 00:01:02.003000
    // 0b000000000000000000000000000|00000|000001|000010|00000000101110111000
    // 0x4200BB8
    assertEquals(
        0x4200BB8L, CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(0, 1, 2, 3_000_000)));
    // 12:00:00.000000
    // 0b000000000000000000000000000|01100|000000|000000|00000000000000000000
    // 0xC00000000
    assertEquals(
        0xC00000000L, CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(12, 0, 0, 0)));
    // 13:14:15.016000
    // 0b000000000000000000000000000|01101|001110|001111|00000011111010000000
    // 0xD38F03E80
    assertEquals(
        0xD38F03E80L,
        CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(13, 14, 15, 16_000_000)));
    // 23:59:59.999000
    // 0b000000000000000000000000000|10111|111011|111011|11110011111001011000
    // 0x17EFBF3E58
    assertEquals(
        0x17EFBF3E58L,
        CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(23, 59, 59, 999_000_000)));

    // 00:00:00.000000
    // 0b000000000000000000000000000|00000|000000|000000|00000000000000000000
    // 0x0
    assertEquals(LocalTime.of(0, 0, 0, 0), CivilTimeEncoder.decodePacked64TimeMicros(0x0L));
    // 00:01:02.000003
    // 0b000000000000000000000000000|00000|000001|000010|00000000000000000011
    // 0x4200003
    assertEquals(
        LocalTime.of(0, 1, 2, 3_000), CivilTimeEncoder.decodePacked64TimeMicros(0x4200003L));
    // 12:00:00.000000
    // 0b000000000000000000000000000|01100|000000|000000|00000000000000000000
    // 0xC00000000
    assertEquals(
        LocalTime.of(12, 0, 0, 0), CivilTimeEncoder.decodePacked64TimeMicros(0xC00000000L));
    // 13:14:15.000016
    // 0b000000000000000000000000000|01101|001110|001111|00000000000000010000
    // 0xD38F00010
    assertEquals(
        LocalTime.of(13, 14, 15, 16_000), CivilTimeEncoder.decodePacked64TimeMicros(0xD38F00010L));
    // 23:59:59.999999
    // 0b000000000000000000000000000|10111|111011|111011|11110100001000111111
    // 0x17EFBF423F
    assertEquals(
        LocalTime.of(23, 59, 59, 999_999_000),
        CivilTimeEncoder.decodePacked64TimeMicros(0x17EFBF423FL));

    try {
      // 00:00:00.000000
      // 0b000000000000000000000000001|00000|000000|000000|00000000000000000000
      // 0x2000000000
      CivilTimeEncoder.decodePacked64TimeMicros(0x2000000000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      assertEquals(null, e.getMessage());
    }
    try {
      // 00:00:00.1000000
      // 0b000000000000000000000000000|00000|000000|000000|11110100001001000000
      // 0xF4240
      CivilTimeEncoder.decodePacked64TimeMicros(0xF4240L);
    } catch (IllegalArgumentException e) {
      assertEquals(null, e.getMessage());
    }
    try {
      // 00:00:60.000000
      // 0b000000000000000000000000000|00000|000000|111100|00000000000000000000
      // 0x3C00000
      CivilTimeEncoder.decodePacked64TimeMicros(0x3C00000L);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid value for SecondOfMinute (valid values 0 - 59): 60", e.getMessage());
    }
    try {
      // 24:00:00.000000
      // 0b000000000000000000000000000|11000|000000|000000|00000000000000000000
      // 0x1800000000
      CivilTimeEncoder.decodePacked64TimeMicros(0x1800000000L);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid value for HourOfDay (valid values 0 - 23): 24", e.getMessage());
    }
    try {
      // 00:60:00.000000
      // 0b000000000000000000000000000|00000|111100|000000|00000000000000000000
      // 0xF0000000
      CivilTimeEncoder.decodePacked64TimeMicros(0xF0000000L);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid value for MinuteOfHour (valid values 0 - 59): 60", e.getMessage());
    }

    LocalTime time = LocalTime.of(1, 1, 1, 1_000_000);
    long conversion = CivilTimeEncoder.encodePacked64TimeMicros(time);
    assertEquals(time, CivilTimeEncoder.decodePacked64TimeMicros(conversion));
  }

  @Test
  public void Packed64DateTimeSecondsTest() {
    // 0001/01/01 00:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|00000|000000|000000
    // 0x4420000
    assertEquals(
        0x4420000L,
        CivilTimeEncoder.encodePacked64DatetimeSeconds(LocalDateTime.of(1, 1, 1, 0, 0, 0)));
    // 0001/02/03 00:01:02
    // 0b0000000000000000000000|00000000000001|0010|00011|00000|000001|000010
    // 0x4860042
    assertEquals(
        0x4860042L,
        CivilTimeEncoder.encodePacked64DatetimeSeconds(LocalDateTime.of(1, 2, 3, 0, 1, 2)));
    // 0001/01/01 12:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|01100|000000|000000
    // 0x442C000
    assertEquals(
        0x442C000L,
        CivilTimeEncoder.encodePacked64DatetimeSeconds(LocalDateTime.of(1, 1, 1, 12, 0, 0)));
    // 0001/01/01 13:14:15
    // 0b0000000000000000000000|00000000000001|0001|00001|01101|001110|001111
    // 0x442D38F
    assertEquals(
        0x442D38FL,
        CivilTimeEncoder.encodePacked64DatetimeSeconds(LocalDateTime.of(1, 1, 1, 13, 14, 15)));
    // 9999/12/31 23:59:59
    // 0b0000000000000000000000|10011100001111|1100|11111|10111|111011|111011
    // 0x9C3F3F7EFB
    assertEquals(
        0x9C3F3F7EFBL,
        CivilTimeEncoder.encodePacked64DatetimeSeconds(LocalDateTime.of(9999, 12, 31, 23, 59, 59)));

    // 0001/01/01 00:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|00000|000000|000000
    // 0x4420000
    assertEquals(
        LocalDateTime.of(1, 1, 1, 0, 0, 0),
        CivilTimeEncoder.decodePacked64DatetimeSeconds(0x4420000L));
    // 0001/02/03 00:01:02
    // 0b0000000000000000000000|00000000000001|0010|00011|00000|000001|000010
    // 0x4860042
    assertEquals(
        LocalDateTime.of(1, 2, 3, 0, 1, 2),
        CivilTimeEncoder.decodePacked64DatetimeSeconds(0x4860042L));
    // 0001/01/01 12:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|01100|000000|000000
    // 0x442C000
    assertEquals(
        LocalDateTime.of(1, 1, 1, 12, 0, 0),
        CivilTimeEncoder.decodePacked64DatetimeSeconds(0x442C000L));
    // 0001/01/01 13:14:15
    // 0b0000000000000000000000|00000000000001|0001|00001|01101|001110|001111
    // 0x442D38F
    assertEquals(
        LocalDateTime.of(1, 1, 1, 13, 14, 15),
        CivilTimeEncoder.decodePacked64DatetimeSeconds(0x442D38FL));
    // 9999/12/31 23:59:59
    // 0b0000000000000000000000|10011100001111|1100|11111|10111|111011|111011
    // 0x9C3F3F7EFB
    assertEquals(
        LocalDateTime.of(9999, 12, 31, 23, 59, 59),
        CivilTimeEncoder.decodePacked64DatetimeSeconds(0x9C3F3F7EFBL));

    // encode and decode failures
    // 10000/01/01 00:00:00
    // 0b0000000000000000000000|10011100010000|0001|00001|00000|000000|000000
    // 0x9C40420000
    LocalDateTime dateTime = LocalDateTime.of(10000, 1, 1, 0, 0, 0);
    try {
      CivilTimeEncoder.encodePacked64DatetimeSeconds(dateTime);
      Assert.fail();
    } catch (IllegalArgumentException expected) {
    }
    try {
      // 0001/01/01 00:00:00
      // 0b0000000000000000000001|00000000000001|0001|00001|00000|000000|000000
      // 0x10004420000
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x10004420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 00:00:00
      // 0b0000000000000000000001|00000000000001|0001|00001|00000|000000|000000
      // 0x10004420000
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x10004420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 00:00:60
      // 0b0000000000000000000000|00000000000001|0001|00001|00000|000000|111100
      // 0x442003C
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x442003CL);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 00:60:00
      // 0b0000000000000000000000|00000000000001|0001|00001|00000|111100|000000
      // 0x4420F00
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x4420F00L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 24:00:00
      // 0b0000000000000000000000|00000000000001|0001|00001|11000|000000|000000
      // 0x4438000
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x4438000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/00 00:00:00
      // 0b0000000000000000000000|00000000000001|0001|00000|00000|000000|000000
      // 0x4400000
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x4400000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/13/01 00:00:00
      // 0b0000000000000000000000|00000000000001|1101|00001|00000|000000|000000
      // 0x7420000
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x7420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 10000/01/01 00:00:00
      // 0b0000000000000000000000|10011100010000|0001|00001|00000|000000|000000
      // 0x9C40420000
      CivilTimeEncoder.decodePacked64DatetimeSeconds(0x9C40420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    // Round Trip Test
    dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
    long conversion = CivilTimeEncoder.encodePacked64DatetimeSeconds(dateTime);
    assertEquals(dateTime, CivilTimeEncoder.decodePacked64DatetimeSeconds(conversion));
  }

  @Test
  public void Packed64DateTimeMicrosTest() {
    // 0001/01/01 00:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|00000|000000|000000
    // 0x4420000
    assertEquals(
        0x442000000000L,
        CivilTimeEncoder.encodePacked64DatetimeMicros(LocalDateTime.of(1, 1, 1, 0, 0, 0, 0)));
    // 0001/02/03 00:01:02
    // 0b0000000000000000000000|00000000000001|0010|00011|00000|000001|000010
    // 0x4860042
    assertEquals(
        0x486004200BB8L,
        CivilTimeEncoder.encodePacked64DatetimeMicros(
            LocalDateTime.of(1, 2, 3, 0, 1, 2, 3_000_000)));
    // 0001/01/01 12:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|01100|000000|000000
    // 0x442C000
    assertEquals(
        0x442C00000000L,
        CivilTimeEncoder.encodePacked64DatetimeMicros(LocalDateTime.of(1, 1, 1, 12, 0, 0, 0)));
    // 0001/01/01 13:14:15
    // 0b0000000000000000000000|00000000000001|0001|00001|01101|001110|001111
    // 0x442D38F
    assertEquals(
        0x442D38F03E80L,
        CivilTimeEncoder.encodePacked64DatetimeMicros(
            LocalDateTime.of(1, 1, 1, 13, 14, 15, 16_000_000)));
    // 9999/12/31 23:59:59
    // 0b0000000000000000000000|10011100001111|1100|11111|10111|111011|111011
    // 0x9C3F3F7EFB
    assertEquals(
        0x9C3F3F7EFBF3E58L,
        CivilTimeEncoder.encodePacked64DatetimeMicros(
            LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000)));

    // 0001/01/01 00:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|00000|000000|000000
    // 0x4420000
    assertEquals(
        LocalDateTime.of(1, 1, 1, 0, 0, 0, 0),
        CivilTimeEncoder.decodePacked64DatetimeMicros(0x442000000000L));
    // 0001/02/03 00:01:02
    // 0b0000000000000000000000|00000000000001|0010|00011|00000|000001|000010
    // 0x4860042
    assertEquals(
        LocalDateTime.of(1, 2, 3, 0, 1, 2, 3_000_000),
        CivilTimeEncoder.decodePacked64DatetimeMicros(0x486004200BB8L));
    // 0001/01/01 12:00:00
    // 0b0000000000000000000000|00000000000001|0001|00001|01100|000000|000000
    // 0x442C000
    assertEquals(
        LocalDateTime.of(1, 1, 1, 12, 0, 0, 0),
        CivilTimeEncoder.decodePacked64DatetimeMicros(0x442C00000000L));
    // 0001/01/01 13:14:15
    // 0b0000000000000000000000|00000000000001|0001|00001|01101|001110|001111
    // 0x442D38F
    assertEquals(
        LocalDateTime.of(1, 1, 1, 13, 14, 15, 16_000_000),
        CivilTimeEncoder.decodePacked64DatetimeMicros(0x442D38F03E80L));
    // 9999/12/31 23:59:59
    // 0b0000000000000000000000|10011100001111|1100|11111|10111|111011|111011
    // 0x9C3F3F7EFB
    assertEquals(
        LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000),
        CivilTimeEncoder.decodePacked64DatetimeMicros(0x9C3F3F7EFBF3E58L));

    // 10000/01/01 00:00:00
    // 0b0000000000000000000000|10011100010000|0001|00001|00000|000000|000000
    // 0x9C40420000
    LocalDateTime dateTime = LocalDateTime.of(10000, 1, 1, 0, 0, 0);
    try {
      CivilTimeEncoder.encodePacked64DatetimeMicros(dateTime);
      Assert.fail();
    } catch (IllegalArgumentException expected) {
    }
    try {
      // 0001/01/01 00:00:00
      // 0b0000000000000000000001|00000000000001|0001|00001|00000|000000|000000
      // 0x10004420000
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x10004420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 00:00:00
      // 0b0000000000000000000001|00000000000001|0001|00001|00000|000000|000000
      // 0x10004420000
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x10004420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 00:00:60
      // 0b0000000000000000000000|00000000000001|0001|00001|00000|000000|111100
      // 0x442003C
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x442003CL);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 00:60:00
      // 0b0000000000000000000000|00000000000001|0001|00001|00000|111100|000000
      // 0x4420F00
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x4420F00L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/01 24:00:00
      // 0b0000000000000000000000|00000000000001|0001|00001|11000|000000|000000
      // 0x4438000
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x4438000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/01/00 00:00:00
      // 0b0000000000000000000000|00000000000001|0001|00000|00000|000000|000000
      // 0x4400000
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x4400000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 0001/13/01 00:00:00
      // 0b0000000000000000000000|00000000000001|1101|00001|00000|000000|000000
      // 0x7420000
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x7420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      // 10000/01/01 00:00:00
      // 0b0000000000000000000000|10011100010000|0001|00001|00000|000000|000000
      // 0x9C40420000
      CivilTimeEncoder.decodePacked64DatetimeMicros(0x9C40420000L);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Round Trip Test
    dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1, 1_000_000);
    long conversion = CivilTimeEncoder.encodePacked64DatetimeMicros(dateTime);
    assertEquals(dateTime, CivilTimeEncoder.decodePacked64DatetimeMicros(conversion));
  }
}
