/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/Attic/A_CmsEntryPoint.java,v $
 * Date   : $Date: 2010/03/04 14:00:18 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.gwt.client;

import org.opencms.gwt.client.rpc.CmsLog;
import org.opencms.gwt.client.util.CmsStringUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Handles exception handling and more for entry points.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 8.0.0
 * 
 * @see org.opencms.gwt.CmsLogService
 * @see org.opencms.gwt.shared.I_CmsLogService
 * @see org.opencms.gwt.shared.I_CmsLogServiceAsync
 */
public abstract class A_CmsEntryPoint implements EntryPoint {

    /**
     * Default constructor.<p> 
     */
    protected A_CmsEntryPoint() {

        // just for subclassing
    }

    /**
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    public void onModuleLoad() {

        enableRemoteExceptionHandler();
    }

    /**
     * Enables client exception logging on the server.<p>
     */
    protected void enableRemoteExceptionHandler() {

        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

            /**
             * @see com.google.gwt.core.client.GWT.UncaughtExceptionHandler#onUncaughtException(java.lang.Throwable)
             */
            public void onUncaughtException(Throwable t) {

                CmsLog.log(CmsStringUtil.getMessage(t));
            }
        });
    }
}