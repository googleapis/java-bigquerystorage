package com.google.cloud.bigquery.storage.v1beta2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BigDecimalToStringConverterTest {

  @Test
  public void StringToBDTest() {
    BigDecimalToStringConverter.StringToBigDecimal("100000");
  }

  @Test
  public void BDToStringTest() {

  }


}
