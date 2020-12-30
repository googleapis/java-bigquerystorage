package com.example.bigquerystorage;

import static junit.framework.TestCase.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class WriteAPISampleIT {

  private static final String GOOGLE_CLOUD_PROJECT = System.getenv("GOOGLE_CLOUD_PROJECT");
  private static final String BIGQUERY_DATASET_NAME = System.getenv("BIGQUERY_DATASET_NAME");
  private static final String BIGQUERY_TABLE_NAME = System.getenv("BIGQUERY_TABLE_NAME");

  private final Logger log = Logger.getLogger(this.getClass().getName());
  private ByteArrayOutputStream bout;
  private PrintStream out;
  private PrintStream originalPrintStream;

  private static void requireEnvVar(String varName) {
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT");
    requireEnvVar("BIGQUERY_DATASET_NAME");
    requireEnvVar("BIGQUERY_TABLE_NAME");
  }

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    originalPrintStream = System.out;
    System.setOut(out);
  }

  @After
  public void tearDown() {
    System.out.flush();
    System.setOut(originalPrintStream);
    log.log(Level.INFO, "\n" + bout.toString());
  }

  @Test
  public void testWriteCommittedStream() throws Exception {
    WriteCommittedStream.writeCommittedStream(
        GOOGLE_CLOUD_PROJECT, BIGQUERY_DATASET_NAME, BIGQUERY_TABLE_NAME);
  }

  @Test
  public void testWriteToDefaultStream() throws Exception {
    WriteCommittedStream.writeToDefaultStream(
        GOOGLE_CLOUD_PROJECT, BIGQUERY_DATASET_NAME, BIGQUERY_TABLE_NAME);
  }

  @Test
  public void testWritePendingStream() throws Exception {
    WritePendingStream.writePendingStream(
        GOOGLE_CLOUD_PROJECT, BIGQUERY_DATASET_NAME, BIGQUERY_TABLE_NAME);
  }
}
