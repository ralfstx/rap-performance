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
package org.eclipse.rap.rwt.performance.phase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.service.IServiceStore;


public class PerformancePhaseListener implements PhaseListener {

  private static final long serialVersionUID = 1L;
  private static final String RECORD_ATTR
    = PerformancePhaseListener.class.getName() + ".requestRecord";

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  public void beforePhase( PhaseEvent event ) {
    RequestTracker tracker = getTracker();
    tracker.startPhase( event.getPhaseId() );
  }

  public void afterPhase( PhaseEvent event ) {
    RequestTracker tracker = getTracker();
    tracker.endPhase( event.getPhaseId() );
  }

  private RequestTracker getTracker() {
    IServiceStore serviceStore = RWT.getServiceStore();
    RequestTracker tracker
    = ( RequestTracker )serviceStore.getAttribute( RECORD_ATTR );
    if( tracker == null ) {
      tracker = createTracker();
      serviceStore.setAttribute( RECORD_ATTR, tracker );
    }
    return tracker;
  }

  private RequestTracker createTracker() {
    HttpServletRequest request = RWT.getRequest();
    String sessionId = getSessionId( request );
    int requestId = getRequestId( request );
    return new RequestTracker( sessionId, requestId );
  }

  private String getSessionId( HttpServletRequest request ) {
    HttpSession session = request.getSession( false );
    return session != null ? session.getId() : "";
  }

  private int getRequestId( HttpServletRequest request ) {
    String parameter = request.getParameter( "requestCounter" );
    return parameter != null ? Integer.parseInt( parameter ) : -1;
  }
}
