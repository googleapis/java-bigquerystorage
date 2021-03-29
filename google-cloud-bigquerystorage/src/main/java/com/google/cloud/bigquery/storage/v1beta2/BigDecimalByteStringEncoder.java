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

/*
 * This code was ported from ZetaSQL and can be found here:
 * https://github.com/google/zetasql/blob/c55f967a5ae35b476437210c529691d8a73f5507/java/com/google/zetasql/Value.java
 */

package com.google.cloud.bigquery.storage.v1beta2;

import com.google.common.primitives.Bytes;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalByteStringEncoder {
  private static int scale = 38;
  private static final BigDecimal MAX_BIGNUMERIC_VALUE =
      new BigDecimal(
          "578960446186580977117854925043439539266.34992332820282019728792003956564819967");
  private static final BigDecimal MIN_BIGNUMERIC_VALUE =
      new BigDecimal(
          "-578960446186580977117854925043439539266.34992332820282019728792003956564819968");

  public static BigDecimal encodeToBigDecimal(ByteString byteString) {
    BigDecimal bigDecimal =
        deserializeBigDecimal(
            byteString, scale, MAX_BIGNUMERIC_VALUE, MIN_BIGNUMERIC_VALUE, "BigDecimal");
    return bigDecimal;
  }

  public static ByteString encodeToByteString(BigDecimal bigDecimal) {
    ByteString byteString =
        serializeBigDecimal(
            bigDecimal, scale, MAX_BIGNUMERIC_VALUE, MIN_BIGNUMERIC_VALUE, "ByteString");
    return byteString;
  }
  // Make these private and make public wrapper that internalizes these min/max/scale/type
  private static BigDecimal deserializeBigDecimal(
      ByteString serializedValue,
      int scale,
      BigDecimal maxValue,
      BigDecimal minValue,
      String typeName) {
    byte[] bytes = serializedValue.toByteArray();
    // NUMERIC/BIGNUMERIC values are serialized as scaled integers in two's complement form in
    // little endian order. BigInteger requires the same encoding but in big endian order,
    // therefore we must reverse the bytes that come from the proto.
    Bytes.reverse(bytes);
    BigInteger scaledValue = new BigInteger(bytes);
    BigDecimal decimalValue = new BigDecimal(scaledValue, scale);
    if (decimalValue.compareTo(maxValue) > 0 || decimalValue.compareTo(minValue) < 0) {
      throw new IllegalArgumentException(typeName + " overflow: " + decimalValue.toPlainString());
    }
    return decimalValue;
  }
  /** Returns a numeric Value that equals to {@code v}. */
  private static ByteString serializeBigDecimal(
      BigDecimal v, int scale, BigDecimal maxValue, BigDecimal minValue, String typeName) {
    if (v.scale() > scale) {
      throw new IllegalArgumentException(
          typeName + " scale cannot exceed " + scale + ": " + v.toPlainString());
    }
    if (v.compareTo(maxValue) > 0 || v.compareTo(minValue) < 0) {
      throw new IllegalArgumentException(typeName + " overflow: " + v.toPlainString());
    }
    byte[] bytes = v.setScale(scale).unscaledValue().toByteArray();
    // NUMERIC/BIGNUMERIC values are serialized as scaled integers in two's complement form in
    // little endian
    // order. BigInteger requires the same encoding but in big endian order, therefore we must
    // reverse the bytes that come from the proto.
    Bytes.reverse(bytes);
    return ByteString.copyFrom(bytes);
  }
}
