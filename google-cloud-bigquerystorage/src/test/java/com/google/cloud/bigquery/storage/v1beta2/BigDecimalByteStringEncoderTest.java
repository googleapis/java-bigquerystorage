/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1beta2;


import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BigDecimalByteStringEncoderTest {
  @Test
  public void testEncodeBigDecimalandEncodeByteString() {
    BigDecimal testBD = BigDecimal.valueOf(0); // expected result bd
    ByteString testBS =
        BigDecimalByteStringEncoder.encodeToByteString(testBD); // convert expected to bs
    BigDecimal resultBD =
        BigDecimalByteStringEncoder.encodeToBigDecimal(testBS); // convert bs to bd
    Assert.assertEquals(
        0, resultBD.compareTo(testBD)); // ensure converted bd is equal to expected bd

    testBD = BigDecimal.valueOf(1.2);
    testBS = BigDecimalByteStringEncoder.encodeToByteString(testBD);
    resultBD = BigDecimalByteStringEncoder.encodeToBigDecimal(testBS);
    Assert.assertEquals(
        0, resultBD.compareTo(testBD)); // ensure converted bd is equal to expected bd

    testBD = BigDecimal.valueOf(-1.2);
    testBS = BigDecimalByteStringEncoder.encodeToByteString(testBD);
    resultBD = BigDecimalByteStringEncoder.encodeToBigDecimal(testBS);
    Assert.assertEquals(
        0, resultBD.compareTo(testBD)); // ensure converted bd is equal to expected bd

    testBD =
        BigDecimal.valueOf(
            578960446186580977117854925043439539266.34992332820282019728792003956564819967);
    testBS = BigDecimalByteStringEncoder.encodeToByteString(testBD);
    resultBD = BigDecimalByteStringEncoder.encodeToBigDecimal(testBS);
    Assert.assertEquals(
        0, resultBD.compareTo(testBD)); // ensure converted bd is equal to expected bd

    testBD =
        BigDecimal.valueOf(
            -578960446186580977117854925043439539266.34992332820282019728792003956564819967);
    testBS = BigDecimalByteStringEncoder.encodeToByteString(testBD);
    resultBD = BigDecimalByteStringEncoder.encodeToBigDecimal(testBS);
    Assert.assertEquals(
        0, resultBD.compareTo(testBD)); // ensure converted bd is equal to expected bd
  }
}
