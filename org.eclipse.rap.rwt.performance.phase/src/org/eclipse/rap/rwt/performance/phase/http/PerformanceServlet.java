package org.eclipse.rap.rwt.performance.phase.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.performance.phase.Activator;
import org.eclipse.rap.rwt.performance.phase.h2db.H2Connector;
import org.eclipse.rap.rwt.performance.phase.h2db.H2ConnectorFactory;
import org.eclipse.rap.rwt.performance.phase.h2db.H2ResultsReader;
import org.eclipse.rap.rwt.performance.phase.h2db.MeasurementIndex;
import org.eclipse.rwt.internal.theme.JsonValue;


public class PerformanceServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    String path = request.getPathInfo();
    if( path == null ) {
      response.sendRedirect( request.getServletPath() + "/" );
    } else {
      String[] splitPath = ServletUtil.splitFirst( path );
      if( "".equals( splitPath[ 0 ] ) ) {
        writeFile( response, "index.html" );
      } else if( "status".equals( splitPath[ 0 ] ) ) {
        writeStatus( response, splitPath[ 1 ] );
      } else if( "start".equals( splitPath[ 0 ] ) ) {
        Activator.getManager().start();
        writeStatus( response, splitPath[ 1 ] );
      } else if( "stop".equals( splitPath[ 0 ] ) ) {
        Activator.getManager().stop();
        writeStatus( response, splitPath[ 1 ] );
      } else if( "results".equals( splitPath[ 0 ] ) ) {
        writeResults( response, splitPath[ 1 ] );
      } else if( "files".equals( splitPath[ 0 ] ) ) {
        writeFile( response, splitPath[ 1 ] );
      } else {
        writeNotFound( response );
      }
    }
  }

  private void writeStatus( HttpServletResponse response, String path )
    throws IOException
  {
    response.setContentType( "application/json" );
    response.setCharacterEncoding( "UTF-8" );
    PrintWriter writer = response.getWriter();
    writer.write( "{" );
    writer.write( "\"active\": " );
    boolean active = Activator.getManager().isActive();
    writer.write( JsonValue.valueOf( active ).toString() );
    writer.write( "\n" );
    writer.write( "}\n" );
  }

  private void writeResults( HttpServletResponse response, String path )
    throws IOException
  {
    response.setContentType( "application/json" );
    response.setCharacterEncoding( "UTF-8" );
    if( path == null ) {
      writeAllResults( response.getWriter() );
    } else {
      H2Connector connector = H2ConnectorFactory.getConnector( path );
      try {
        writeResults( response.getWriter(), connector );
      } finally {
        connector.close();
      }
    }
  }

  private void writeAllResults( PrintWriter writer ) {
    MeasurementIndex indexTable = new MeasurementIndex();
    try {
      indexTable.initialize();
      indexTable.writeAllRuns( writer );
    } finally {
      indexTable.close();
    }
  }

  private static void writeResults( PrintWriter writer, H2Connector connector )
  {
    H2ResultsReader resultsReader = new H2ResultsReader( connector );
    String query =   "SELECT request, phase,"
                   + " MIN( duration) AS min,"
                   + " AVG( duration ) AS avg,"
                   + " MAX( duration ) AS max\n"
                   + "FROM phases\n"
                   + "GROUP BY request, phase\n"
                   + "ORDER BY request, phase\n";
    resultsReader.readResults( query, writer );
  }

  private void writeFile( HttpServletResponse response, String path )
    throws IOException
  {
    if( path.endsWith( ".html" ) ) {
      response.setContentType( "text/html" );
      response.setCharacterEncoding( "UTF-8" );
    }
    ServletOutputStream outputStream = response.getOutputStream();
    ClassLoader classLoader = PerformanceServlet.class.getClassLoader();
    String resourceName = "resources/" + path;
    InputStream inputStream = classLoader.getResourceAsStream( resourceName );
    if( inputStream != null ) {
      try {
        copy( inputStream, outputStream );
      } finally {
        inputStream.close();
      }
    } else {
      writeNotFound( response );
    }
  }

  private static void copy( InputStream inputStream, OutputStream outputStream )
    throws IOException
  {
    byte[] buffer = new byte[ 4096 ];
    int read;
    while( ( read = inputStream.read( buffer ) ) != -1 ) {
      outputStream.write( buffer, 0, read );
    }
  }

  private void writeNotFound( HttpServletResponse response ) {
    response.setStatus( HttpServletResponse.SC_NOT_FOUND );
  }
}
