package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;


public class H2Connector_Test extends TestCase {

  public void testLegalDbName() {
    assertTrue( H2Connector.isLegalDbName( "foo" ) );
    assertTrue( H2Connector.isLegalDbName( "foo23" ) );
    assertTrue( H2Connector.isLegalDbName( "foo-23" ) );
    assertTrue( H2Connector.isLegalDbName( "-foo.23_" ) );
    assertFalse( H2Connector.isLegalDbName( "" ) );
    assertFalse( H2Connector.isLegalDbName( "/" ) );
    assertFalse( H2Connector.isLegalDbName( "foo/bar" ) );
    assertFalse( H2Connector.isLegalDbName( "foo bar" ) );
    assertFalse( H2Connector.isLegalDbName( "bär" ) );
  }

  public void testCreateWithNullParent() {
    try {
      new H2Connector( null, "foo" );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testCreateWithNullName() {
    try {
      new H2Connector( new File( "/tmp" ), null );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testCreateWithIllegalPath() {
    try {
      new H2Connector( new File( "/tmp" ), "foo/bar" );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetConnection() throws IOException, SQLException {
    File dbFile = H2TestUtil.fakeExampleDb();
    String dbName = H2TestUtil.getDbName( dbFile );
    assertTrue( dbFile.exists() );
    assertTrue( dbFile.isFile() );
    assertTrue( dbFile.length() > 0 );
    H2Connector connector = new H2Connector( dbFile.getParentFile(), dbName );
    Connection connection = connector.getConnection();
    assertNotNull( connection );
    assertFalse( connection.isClosed() );
    connector.close();
    assertTrue( connection.isClosed() );
  }

  public void testExecuteQueryOnConnection() throws IOException, SQLException {
    File dbFile = H2TestUtil.fakeExampleDb();
    String dbName = H2TestUtil.getDbName( dbFile );
    H2Connector connector = new H2Connector( dbFile.getParentFile(), dbName );
    Connection connection = connector.getConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery( "SELECT * FROM TEST;" );
    assertNotNull( resultSet );
    assertTrue( resultSet.first() );
    resultSet.close();
    connector.close();
  }
}
