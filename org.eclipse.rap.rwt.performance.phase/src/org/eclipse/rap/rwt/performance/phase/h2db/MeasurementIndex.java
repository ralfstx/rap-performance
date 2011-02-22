/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public final class MeasurementIndex {

  public static final class MeasurementRun {
    private final String name;
    private final int sessionCount;
    private final long[] avgs;

    public MeasurementRun( String name, int sessionCount, long[] avgs ) {
      if( avgs.length != 4 ) {
        throw new IllegalArgumentException( "Illegals average count" );
      }
      this.name = name;
      this.sessionCount = sessionCount;
      this.avgs = avgs;
    }

    public String getName() {
      return name;
    }

    public int getSessionCount() {
      return sessionCount;
    }

    public long getAvg( int phaseId ) {
      return avgs[ phaseId ];
    }
  }

  private final H2Connector connector;
  private PreparedStatement insertStatement;

  public MeasurementIndex() {
    connector = H2ConnectorFactory.getConnector( "index" );
  }

  public void initialize() {
    try {
      createTableIfMissing();
      prepareInsertStatement();
    } catch( SQLException e ) {
      String message = "Failed to setup database: " + e.getMessage();
      throw new RuntimeException( message, e );
    }
  }

  public void addRun( MeasurementRun run ) {
    try {
      insertRecord( run );
    } catch( SQLException e ) {
      String message = "Failed to write record to database: " + e.getMessage();
      throw new RuntimeException( message, e );
    }
  }

  public void writeAllRuns( PrintWriter writer ) {
    H2ResultsReader resultsReader = new H2ResultsReader( connector );
    String query = "SELECT * FROM runs;";
    resultsReader.readResults( query, writer );
  }

  public void close() {
    connector.close();
  }

  private void createTableIfMissing() throws SQLException {
    Statement statement = connector.getConnection().createStatement();
    String query =   "CREATE TABLE IF NOT EXISTS runs (\n"
                   + "  name VARCHAR(255) NOT NULL,\n"
                   + "  sessions INT,\n"
                   + "  avg_p1 LONG,\n"
                   + "  avg_p2 LONG,\n"
                   + "  avg_p3 LONG,\n"
                   + "  avg_p4 LONG,\n"
                   + "  PRIMARY KEY ( name ) );\n";
    try {
      statement.execute( query );
    } finally {
      statement.close();
    }
  }

  private void prepareInsertStatement() throws SQLException {
    String query =   "INSERT INTO runs\n"
                   + "( name, sessions, avg_p1, avg_p2, avg_p3, avg_p4 )"
                   + "VALUES ( ?, ?, ?, ?, ?, ? );\n";
    insertStatement = connector.getConnection().prepareStatement( query );
  }

  private void insertRecord( MeasurementRun run ) throws SQLException {
    insertStatement.setString( 1, run.getName() );
    insertStatement.setLong( 2, run.getSessionCount() );
    insertStatement.setLong( 3, run.getAvg( 0 ) );
    insertStatement.setLong( 4, run.getAvg( 1 ) );
    insertStatement.setLong( 5, run.getAvg( 2 ) );
    insertStatement.setLong( 6, run.getAvg( 3 ) );
    insertStatement.execute();
  }
}
