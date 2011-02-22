package org.eclipse.rap.rwt.performance.phase.http;


public class ServletUtil {

  public static String[] splitFirst( String path ) {
    String[] result = new String[2];
    String splitPath = path;
    while( splitPath.length() > 0 && splitPath.charAt( 0 ) ==  '/' ) {
      splitPath = splitPath.substring( 1 );
    }
    int index = splitPath.indexOf( '/' );
    if( index != -1 ) {
      result[ 0 ] = splitPath.substring( 0, index );
      if( splitPath.length() > index + 1 ) {
        result[ 1 ] = splitPath.substring( index + 1 );
      }
    } else {
      result[ 0 ] = splitPath;
    }
    return result;
  }
}
