package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import junit.framework.TestCase;


public class H2ConnectorFactory_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    System.setProperty( H2ConnectorFactory.DB_PATH_PROPERTY, "/tmp/property" );
  }

  @Override
  protected void tearDown() throws Exception {
    System.getProperties().remove( H2ConnectorFactory.DB_PATH_PROPERTY );
  }

  public void testGetConnector() throws IOException {
    File dbFile = H2TestUtil.fakeExampleDb();
    String dbPath = H2TestUtil.getDbName( dbFile );
    H2Connector connector = H2ConnectorFactory.getConnector( dbPath );
    assertNotNull( connector );
    Connection connection = connector.getConnection();
    assertNotNull( connection );
    connector.close();
  }
}
