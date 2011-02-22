package org.eclipse.rap.rwt.performance.phase;

import org.eclipse.rap.rwt.performance.phase.http.HttpServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

  private static MeasurementManager manager;
  private HttpServiceTracker httpTracker;

  public void start( BundleContext context ) throws Exception {
    httpTracker = new HttpServiceTracker( context );
    httpTracker.open();
    manager = new MeasurementManager();
  }

  public void stop( BundleContext context ) throws Exception {
    manager = null;
    httpTracker.close();
  }

  public static MeasurementManager getManager() {
    return manager;
  }
}
