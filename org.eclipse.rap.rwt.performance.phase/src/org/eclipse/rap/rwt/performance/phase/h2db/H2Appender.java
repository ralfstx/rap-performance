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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.rap.rwt.performance.phase.Appender;
import org.eclipse.rap.rwt.performance.phase.PhaseRecord;
import org.eclipse.rap.rwt.performance.phase.h2db.MeasurementIndex.MeasurementRun;


public class H2Appender implements Appender {

  private final Object lock = new Object();
  private final H2Connector connector;
  private PreparedStatement insertStatement;

  public H2Appender( H2Connector connector ) {
    this.connector = connector;
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

  public void append( PhaseRecord record ) {
    try {
      insertRecord( record );
    } catch( SQLException e ) {
      String message = "Failed to write record to database: " + e.getMessage();
      throw new RuntimeException( message, e );
    }
  }

  public MeasurementRun getSummary() {
    String name = connector.getName();
    try {
      int sessionCount = getSessionCount();
      long[] avgs = getAvgs();
      return new MeasurementRun( name, sessionCount, avgs );
    } catch( SQLException e ) {
      String message = "Failed read summary for database: " + e.getMessage();
      throw new RuntimeException( message, e );
    }
  }

  private void createTableIfMissing() throws SQLException {
    String query =   "CREATE TABLE IF NOT EXISTS phases (\n"
                   + "  session VARCHAR(255) NOT NULL,\n"
                   + "  request INT NOT NULL,\n"
                   + "  phase INT NOT NULL ,\n"
                   + "  time LONG,\n"
                   + "  duration LONG,\n"
                   + "  PRIMARY KEY ( session, request, phase ) );\n";
    synchronized( lock ) {
      Statement statement = connector.getConnection().createStatement();
      try {
        statement.execute( query );
      } finally {
        statement.close();
      }
    }
  }

  private void prepareInsertStatement() throws SQLException {
    String query =   "INSERT INTO phases\n"
                   + "( session, request, phase, time, duration )\n"
                   + "VALUES (?, ?, ?, ?, ?);\n";
    synchronized( lock ) {
      insertStatement = connector.getConnection().prepareStatement( query );
    }
  }

  private void insertRecord( PhaseRecord record ) throws SQLException {
    synchronized( lock ) {
      insertStatement.setString( 1, record.getSessionId() );
      insertStatement.setInt( 2, record.getRequestId() );
      insertStatement.setInt( 3, record.getPhaseId() );
      insertStatement.setLong( 4, record.getStartTime() );
      insertStatement.setLong( 5, record.getDuration() );
      insertStatement.execute();
    }
  }

  private int getSessionCount() throws SQLException {
    String query =   "SELECT COUNT( session ) AS sessions\n"
                   + "FROM phases\n"
                   + "WHERE request = -1 AND phase = 0\n"
                   + "GROUP BY request, phase\n";
    int sessions = 0;
    synchronized( lock ) {
      Statement statement = connector.getConnection().createStatement();
      try {
        ResultSet resultSet = statement.executeQuery( query );
        if( resultSet.next() ) {
          sessions = resultSet.getInt( "sessions" );
        }
      } finally {
        statement.close();
      }
    }
    return sessions;
  }

  private long[] getAvgs() throws SQLException {
    Statement statement = connector.getConnection().createStatement();
    long[] avgs = new long[ 4 ];
    try {
      String query =   "SELECT phase, AVG( duration ) AS avg\n"
                     + "FROM phases\n"
                     + "GROUP BY phase\n"
                     + "ORDER BY phase\n";
      ResultSet resultSet = statement.executeQuery( query );
      while( resultSet.next() ) {
        int phase = resultSet.getInt( "phase" );
        avgs[ phase ] = resultSet.getLong( "avg" );
      }
    } finally {
      statement.close();
    }
    return avgs;
  }
}
