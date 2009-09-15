/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/jsp/Attic/CmsJspTagEnableAde.java,v $
 * Date   : $Date: 2009/09/15 13:29:07 $
 * Version: $Revision: 1.1.2.5 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2009 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.jsp;

import org.opencms.cache.CmsMemoryObjectCache;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.history.CmsHistoryResourceHandler;
import org.opencms.file.types.CmsResourceTypeContainerPage;
import org.opencms.flex.CmsFlexController;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.staticexport.CmsLinkManager;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.editors.Messages;
import org.opencms.workplace.editors.ade.CmsContainerPageBean;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;

/**
 * Implementation of the <code>&lt;enable-ade/&gt;</code> tag.<p>
 * 
 * @version $Revision: 1.1.2.5 $ 
 * 
 * @since 7.6 
 */
public class CmsJspTagEnableAde extends BodyTagSupport {

    /** Default direct edit include file URI for the jQuery direct edit provider. */
    protected static final String INCLUDE_FILE_JQUERY = "/system/workplace/editors/ade/include.txt";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsJspTagEnableAde.class);

    /** Serial version UID required for safe serialization. */
    private static final long serialVersionUID = 8447599916548975733L;

    /**
     * enable-ade action method.<p>
     * 
     * @param context the current JSP page context
     * 
     * @throws JspException in case something goes wrong
     */
    public static void enableAdeTagAction(PageContext context) throws JspException {

        ServletRequest req = context.getRequest();
        if (CmsHistoryResourceHandler.isHistoryRequest(req)) {
            // don't display advanced direct edit buttons on an historical resource
            return;
        }

        CmsFlexController controller = CmsFlexController.getController(req);
        CmsObject cms = controller.getCmsObject();

        if (cms.getRequestContext().currentProject().isOnlineProject()) {
            // advanced direct edit is never enabled in the online project
            return;
        }

        if (CmsResource.isTemporaryFileName(cms.getRequestContext().getUri())) {
            // don't display advanced direct edit buttons if a temporary file is displayed
            return;
        }

        // insert ade header HTML
        String code = getAdeIncludes(cms, req);
        try {
            context.getOut().print(code);
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    /**
     * Returns the ade include HTML to insert in the page beginning.<p>
     * 
     * @param cms the current cms context
     * @param req the current request
     *  
     * @return the ade include HTML to insert in the page beginning
     */
    protected static String getAdeIncludes(CmsObject cms, ServletRequest req) {

        // check if the selected include file is available in the cache
        CmsMemoryObjectCache cache = CmsMemoryObjectCache.getInstance();
        CmsLinkManager linkMan = OpenCms.getLinkManager();

        String headerInclude = (String)cache.getCachedObject(CmsJspTagEnableAde.class, INCLUDE_FILE_JQUERY);
        if (headerInclude == null) {
            // the file is not available in the cache
            try {
                CmsFile file = cms.readFile(INCLUDE_FILE_JQUERY);
                // get the encoding for the resource
                CmsProperty property = cms.readPropertyObject(
                    file,
                    CmsPropertyDefinition.PROPERTY_CONTENT_ENCODING,
                    true);
                String encoding = property.getValue(OpenCms.getSystemInfo().getDefaultEncoding());
                // create a String with the right encoding
                headerInclude = CmsEncoder.createString(file.getContents(), encoding);

                // resolve macros in include header
                CmsMacroResolver resolver = CmsMacroResolver.newInstance();
                resolver.setKeepEmptyMacros(true); // be sure request macros stay there
                String editorUri = linkMan.substituteLink(cms, "/system/workplace/editors/editor.jsp");
                resolver.addMacro("editorUri", editorUri);
                String serverGetUri = linkMan.substituteLink(cms, "/system/workplace/editors/ade/get.jsp");
                resolver.addMacro("serverGetUri", serverGetUri);
                String serverSetUri = linkMan.substituteLink(cms, "/system/workplace/editors/ade/set.jsp");
                resolver.addMacro("serverSetUri", serverSetUri);
                String skinUri = CmsWorkplace.getSkinUri();
                resolver.addMacro("skinUri", skinUri);

                headerInclude = resolver.resolveMacros(headerInclude);

                // store this in the cache
                cache.putCachedObject(CmsJspTagEnableAde.class, INCLUDE_FILE_JQUERY, headerInclude);

            } catch (CmsException e) {
                // this should better not happen
                headerInclude = "";
                LOG.error(Messages.get().getBundle().key(Messages.LOG_DIRECT_EDIT_NO_HEADER_1, INCLUDE_FILE_JQUERY), e);
            }
        }

        // these macros are request specific
        CmsMacroResolver resolver = CmsMacroResolver.newInstance();
        try {
            CmsResource containerPage = cms.readResource(cms.getRequestContext().getUri());
            if (containerPage.getTypeId() != CmsResourceTypeContainerPage.getStaticTypeId()) {
                resolver.addMacro("currentUri", cms.getRequestContext().getUri());
                // container page is used as template
                String cntPagePath = cms.readPropertyObject(
                    containerPage,
                    CmsPropertyDefinition.PROPERTY_TEMPLATE_ELEMENTS,
                    true).getValue("");
                try {
                    containerPage = cms.readResource(cntPagePath);
                } catch (CmsException e) {
                    LOG.warn(e.getLocalizedMessage(), e);
                }
            } else if (req.getParameter(CmsContainerPageBean.TEMPLATE_ELEMENT_PARAMETER) != null) {
                CmsUUID id = new CmsUUID(req.getParameter(CmsContainerPageBean.TEMPLATE_ELEMENT_PARAMETER));
                resolver.addMacro("currentUri", cms.getSitePath(cms.readResource(id)));
            } else {
                resolver.addMacro("currentUri", cms.getRequestContext().getUri());
            }
            String containerPageUri = cms.getSitePath(containerPage);
            resolver.addMacro("currentContainerPage", containerPageUri);
        } catch (Exception e) {
            LOG.warn(e.getLocalizedMessage(), e);
        }
        resolver.addMacro("currentLocale", cms.getRequestContext().getLocale().toString());

        headerInclude = resolver.resolveMacros(headerInclude);

        return headerInclude;
    }

    /**
     * Close the direct edit tag, also prints the direct edit HTML to the current page.<p>
     * 
     * @return {@link #EVAL_PAGE}
     * 
     * @throws JspException in case something goes wrong
     */
    @Override
    public int doEndTag() throws JspException {

        // only execute action for the first "ade" tag on the page (include file)
        enableAdeTagAction(pageContext);

        if (OpenCms.getSystemInfo().getServletContainerSettings().isReleaseTagsAfterEnd()) {
            // need to release manually, JSP container may not call release as required (happens with Tomcat)
            release();
        }

        return EVAL_PAGE;
    }

    /**
     * Opens the direct edit tag, if manual mode is set then the next 
     * start HTML for the direct edit buttons is printed to the page.<p>
     * 
     * @return {@link #EVAL_BODY_INCLUDE}
     */
    @Override
    public int doStartTag() {

        return EVAL_BODY_INCLUDE;
    }

    /**
     * Releases any resources we may have (or inherit).<p>
     */
    @Override
    public void release() {

        super.release();
    }
}