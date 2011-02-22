package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.performance.phase.h2db.H2Connector;
import org.eclipse.rap.rwt.performance.phase.h2db.H2ResultsReader;

public class H2ResultsReader_Test extends TestCase {

  public void testGetConnection() throws IOException {
    File dbFile = H2TestUtil.fakeExampleDb();
    String dbName = H2TestUtil.getDbName( dbFile );
    H2Connector connector = new H2Connector( dbFile.getParentFile(), dbName );
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter( writer );
    H2ResultsReader resultsReader = new H2ResultsReader( connector );
    String sql = "SELECT * FROM test;";
    String expected =   "{\n"
                      + "\"headers\": [ \"ID\", \"NAME\" ],\n"
                      + "\"values\": [\n"
                      + "[ 1, \"Hello\" ],\n"
                      + "[ 2, \"World\" ] ]\n"
                      + "}\n";
    resultsReader.readResults( sql, printWriter );
    assertEquals( expected, writer.getBuffer().toString() );
    connector.close();
  }
}
