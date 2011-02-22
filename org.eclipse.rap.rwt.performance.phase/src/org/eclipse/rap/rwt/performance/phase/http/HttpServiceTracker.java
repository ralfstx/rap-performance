package org.eclipse.rap.rwt.performance.phase.http;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServiceTracker
  extends ServiceTracker<HttpService, HttpService>
{

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
  }

  @Override
  public HttpService addingService( ServiceReference<HttpService> reference ) {
    HttpService httpService;
    httpService = super.addingService( reference );
    try {
      register( httpService );
    } catch( Exception e ) {
      throw new RuntimeException( "Failed to add http service: " + e );
    }
    return httpService;
  }

  @Override
  public void removedService( ServiceReference<HttpService> reference,
                              HttpService service )
  {
    super.removedService( reference, service );
  }
  
  private void register( HttpService httpService )
    throws ServletException, NamespaceException
  {
    PerformanceServlet servlet = new PerformanceServlet();
    httpService.registerServlet( "/performance", servlet, null, null );
  }
}