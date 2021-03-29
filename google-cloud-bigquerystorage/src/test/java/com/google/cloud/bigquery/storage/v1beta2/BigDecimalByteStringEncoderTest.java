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

import static com.google.common.truth.Truth.assertThat;

import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BigDecimalByteStringEncoderTest {
  private static String MAX_BIGNUMERIC_VALUE_STRING =
      "578960446186580977117854925043439539266.34992332820282019728792003956564819967";
  private static String MIN_BIGNUMERIC_VALUE_STRING =
      "-578960446186580977117854925043439539266.34992332820282019728792003956564819968";
  @Test
  public void testEncodeToBigDecimal(){
    List<BigDecimal> bigDecimals = Arrays.asList(
        BigDecimal.valueOf(0),
        BigDecimal.valueOf(1.2),
        BigDecimal.valueOf(-1.2),
        BigDecimal.valueOf(578960446186580977117854925043439539266.34992332820282019728792003956564819967),
        BigDecimal.valueOf(-578960446186580977117854925043439539266.34992332820282019728792003956564819967));
    ByteString zeroBS = BigDecimalByteStringEncoder.encodeToByteString(BigDecimal.valueOf(0));
    BigDecimal zeroBD = BigDecimal.valueOf(0);
    Assert.assertEquals(zeroBD, (BigDecimalByteStringEncoder.encodeToBigDecimal(zeroBS)));
    // TODO(jstocklass): How do i make this fail.... I don't trust this test at all

  }
  public void testEncodeToByteString(){
    // start with some bigdecimals
    // convert to bytestrings
    // make sure it's correct
  }
}