package org.eclipse.rap.rwt.performance.phase.http;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.performance.phase.http.ServletUtil;


public class ServletUtil_Test extends TestCase {

  public void testNull() {
    try {
      ServletUtil.splitFirst( null );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testEmpty() {
    String[] result = ServletUtil.splitFirst( "" );
    assertEquals( 2, result.length );
    assertEquals( "", result[ 0 ] );
    assertEquals( null, result[ 1 ] );
  }

  public void testRoot() {
    String[] result = ServletUtil.splitFirst( "/" );
    assertEquals( 2, result.length );
    assertEquals( "", result[ 0 ] );
    assertEquals( null, result[ 1 ] );
  }

  public void testTrailingSlash() {
    String[] result = ServletUtil.splitFirst( "/foo/" );
    assertEquals( 2, result.length );
    assertEquals( "foo", result[ 0 ] );
    assertEquals( null, result[ 1 ] );
  }

  public void testTwoPathSegments() {
    String[] result = ServletUtil.splitFirst( "/foo/bar" );
    assertEquals( 2, result.length );
    assertEquals( "foo", result[ 0 ] );
    assertEquals( "bar", result[ 1 ] );
  }

  public void testTwoPathSegmentsWithTrailingSlash() {
    String[] result = ServletUtil.splitFirst( "/foo/bar/" );
    assertEquals( 2, result.length );
    assertEquals( "foo", result[ 0 ] );
    assertEquals( "bar/", result[ 1 ] );
  }

  public void testThreePathSegments() {
    String[] result = ServletUtil.splitFirst( "/foo/bar/baz" );
    assertEquals( 2, result.length );
    assertEquals( "foo", result[ 0 ] );
    assertEquals( "bar/baz", result[ 1 ] );
  }
}
