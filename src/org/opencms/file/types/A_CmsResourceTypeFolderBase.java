/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/file/types/A_CmsResourceTypeFolderBase.java,v $
 * Date   : $Date: 2006/08/25 12:22:55 $
 * Version: $Revision: 1.16.4.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package org.opencms.file.types;

import org.opencms.db.CmsSecurityManager;
import org.opencms.file.CmsDataNotImplementedException;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsFolder;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsVfsException;
import org.opencms.main.CmsException;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsMultiException;
import org.opencms.main.OpenCms;

import java.util.List;

/**
 * Resource type descriptor for the type "folder".<p>
 *
 * @author Alexander Kandzior 
 * 
 * @version $Revision: 1.16.4.3 $ 
 * 
 * @since 6.0.0 
 */
public abstract class A_CmsResourceTypeFolderBase extends A_CmsResourceType {

    /**
     * Default constructor, used to initialize member variables.<p>
     */
    public A_CmsResourceTypeFolderBase() {

        super();
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#changeLastModifiedProjectId(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource)
     */
    public void changeLastModifiedProjectId(CmsObject cms, CmsSecurityManager securityManager, CmsResource resource)
    throws CmsException {

        // collect all resources in the folder (include deleted ones)
        List resources = securityManager.readChildResources(
            cms.getRequestContext(),
            resource,
            CmsResourceFilter.ALL,
            true,
            true);

        // handle the folder itself
        super.changeLastModifiedProjectId(cms, securityManager, resource);

        // now walk through all sub-resources in the folder
        for (int i = 0; i < resources.size(); i++) {
            CmsResource childResource = (CmsResource)resources.get(i);
            // handle child resources
            getResourceType(childResource.getTypeId()).changeLastModifiedProjectId(cms, securityManager, childResource);
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#chtype(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, int)
     */
    public void chtype(CmsObject cms, CmsSecurityManager securityManager, CmsResource filename, int newType)
    throws CmsException, CmsDataNotImplementedException {

        if (!OpenCms.getResourceManager().getResourceType(newType).isFolder()) {
            // it is not possible to change the type of a folder to a file type
            throw new CmsDataNotImplementedException(Messages.get().container(
                Messages.ERR_CHTYPE_FOLDER_1,
                cms.getSitePath(filename)));
        }
        super.chtype(cms, securityManager, filename, newType);
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#copyResource(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, java.lang.String, int)
     */
    public void copyResource(
        CmsObject cms,
        CmsSecurityManager securityManager,
        CmsResource source,
        String destination,
        int siblingMode) throws CmsIllegalArgumentException, CmsException {

        // first validate the destination name
        destination = CmsResource.validateFoldername(destination);

        // collect all resources in the folder (but exclude deleted ones)
        List resources = securityManager.readChildResources(
            cms.getRequestContext(),
            source,
            CmsResourceFilter.IGNORE_EXPIRATION,
            true,
            true);

        // handle the folder itself
        super.copyResource(cms, securityManager, source, destination, siblingMode);

        // now walk through all sub-resources in the folder
        for (int i = 0; i < resources.size(); i++) {
            CmsResource childResource = (CmsResource)resources.get(i);
            String childDestination = destination.concat(childResource.getName());
            // handle child resources
            getResourceType(childResource.getTypeId()).copyResource(
                cms,
                securityManager,
                childResource,
                childDestination,
                siblingMode);
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#createResource(org.opencms.file.CmsObject, CmsSecurityManager, java.lang.String, byte[], List)
     */
    public CmsResource createResource(
        CmsObject cms,
        CmsSecurityManager securityManager,
        String resourcename,
        byte[] content,
        List properties) throws CmsException {

        resourcename = CmsResource.validateFoldername(resourcename);
        return super.createResource(cms, securityManager, resourcename, content, properties);
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#getLoaderId()
     */
    public int getLoaderId() {

        // folders have no loader
        return -1;
    }

    /**
     * @see org.opencms.file.types.A_CmsResourceType#isFolder()
     */
    public boolean isFolder() {

        return true;
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#moveResource(org.opencms.file.CmsObject, org.opencms.db.CmsSecurityManager, org.opencms.file.CmsResource, java.lang.String)
     */
    public void moveResource(CmsObject cms, CmsSecurityManager securityManager, CmsResource resource, String destination)
    throws CmsException, CmsIllegalArgumentException {

        String dest = cms.getRequestContext().addSiteRoot(destination);
        if (!CmsResource.isFolder(dest)) {
            // ensure folder name end's with a / (required for the following comparison)
            dest = dest.concat("/");
        }
        if (resource.getRootPath().equals(dest)) {
            // move to target with same name is not allowed
            throw new CmsVfsException(org.opencms.file.Messages.get().container(
                org.opencms.file.Messages.ERR_MOVE_SAME_NAME_1,
                destination));
        }
        if (dest.startsWith(resource.getRootPath())) {
            // move of folder inside itself is not allowed
            throw new CmsVfsException(org.opencms.file.Messages.get().container(
                org.opencms.file.Messages.ERR_MOVE_SAME_FOLDER_2,
                cms.getSitePath(resource),
                destination));
        }

        // first validate the destination name
        dest = CmsResource.validateFoldername(dest);

        securityManager.moveResource(cms.getRequestContext(), resource, dest);

        // touch all resources, so each resource has the time to adapt it self to the new environment
        CmsFolder folder = securityManager.readFolder(cms.getRequestContext(), dest, CmsResourceFilter.ALL);
        List resources = securityManager.readResources(cms.getRequestContext(), folder, CmsResourceFilter.ALL, true);
        if (resources != null) {
            CmsMultiException me = new CmsMultiException();
            // now walk through all sub-resources in the folder
            for (int i = 0; i < resources.size(); i++) {
                CmsResource childResource = (CmsResource)resources.get(i);
                if (childResource.isFolder()) {
                    continue;
                }
                try {
                    // touch, collecting the errors
                    getResourceType(childResource.getTypeId()).writeFile(
                        cms,
                        securityManager,
                        CmsFile.upgrade(childResource, cms));
                } catch (CmsException e) {
                    me.addException(e);
                }
            }
            if (!me.getExceptions().isEmpty()) {
                throw me;
            }
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#replaceResource(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, int, byte[], List)
     */
    public void replaceResource(
        CmsObject cms,
        CmsSecurityManager securityManager,
        CmsResource resource,
        int type,
        byte[] content,
        List properties) throws CmsException, CmsDataNotImplementedException {

        if (type != getTypeId()) {
            // it is not possible to replace a folder with a different type
            throw new CmsDataNotImplementedException(Messages.get().container(
                Messages.ERR_REPLACE_RESOURCE_FOLDER_1,
                cms.getSitePath(resource)));
        }
        // properties of a folder can be replaced, content is ignored
        super.replaceResource(cms, securityManager, resource, getTypeId(), null, properties);
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#restoreResourceBackup(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, int)
     */
    public void restoreResourceBackup(
        CmsObject cms,
        CmsSecurityManager securityManager,
        CmsResource resourename,
        int tag) throws CmsException {

        // it is not possible to restore a folder from the backup
        throw new CmsDataNotImplementedException(Messages.get().container(Messages.ERR_RESTORE_FOLDERS_0));
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#setDateExpired(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, long, boolean)
     */
    public void setDateExpired(
        CmsObject cms,
        CmsSecurityManager securityManager,
        CmsResource resource,
        long dateLastModified,
        boolean recursive) throws CmsException {

        // handle the folder itself
        super.setDateExpired(cms, securityManager, resource, dateLastModified, recursive);

        if (recursive) {
            // collect all resources in the folder (but exclude deleted ones)
            List resources = securityManager.readChildResources(
                cms.getRequestContext(),
                resource,
                CmsResourceFilter.IGNORE_EXPIRATION,
                true,
                true);

            // now walk through all sub-resources in the folder
            for (int i = 0; i < resources.size(); i++) {
                CmsResource childResource = (CmsResource)resources.get(i);
                // handle child resources
                getResourceType(childResource.getTypeId()).setDateExpired(
                    cms,
                    securityManager,
                    childResource,
                    dateLastModified,
                    recursive);
            }
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#setDateLastModified(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, long, boolean)
     */
    public void setDateLastModified(
        CmsObject cms,
        CmsSecurityManager securityManager,
        CmsResource resource,
        long dateLastModified,
        boolean recursive) throws CmsException {

        // handle the folder itself
        super.setDateLastModified(cms, securityManager, resource, dateLastModified, recursive);

        if (recursive) {
            // collect all resources in the folder (but exclude deleted ones)
            List resources = securityManager.readChildResources(
                cms.getRequestContext(),
                resource,
                CmsResourceFilter.IGNORE_EXPIRATION,
                true,
                true);

            // now walk through all sub-resources in the folder
            for (int i = 0; i < resources.size(); i++) {
                CmsResource childResource = (CmsResource)resources.get(i);
                // handle child resources
                getResourceType(childResource.getTypeId()).setDateLastModified(
                    cms,
                    securityManager,
                    childResource,
                    dateLastModified,
                    recursive);
            }
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#setDateReleased(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, long, boolean)
     */
    public void setDateReleased(
        CmsObject cms,
        CmsSecurityManager securityManager,
        CmsResource resource,
        long dateLastModified,
        boolean recursive) throws CmsException {

        // handle the folder itself
        super.setDateReleased(cms, securityManager, resource, dateLastModified, recursive);

        if (recursive) {
            // collect all resources in the folder (but exclude deleted ones)
            List resources = securityManager.readChildResources(
                cms.getRequestContext(),
                resource,
                CmsResourceFilter.IGNORE_EXPIRATION,
                true,
                true);

            // now walk through all sub-resources in the folder
            for (int i = 0; i < resources.size(); i++) {
                CmsResource childResource = (CmsResource)resources.get(i);
                // handle child resources
                getResourceType(childResource.getTypeId()).setDateReleased(
                    cms,
                    securityManager,
                    childResource,
                    dateLastModified,
                    recursive);
            }
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#undoChanges(org.opencms.file.CmsObject, CmsSecurityManager, CmsResource, int)
     */
    public void undoChanges(CmsObject cms, CmsSecurityManager securityManager, CmsResource resource, int mode)
    throws CmsException {

        List resources = null;
        if (mode > CmsResource.UNDO_CONTENT) { // recursive?
            // collect all resources in the folder (including deleted ones)
            resources = securityManager.readChildResources(
                cms.getRequestContext(),
                resource,
                CmsResourceFilter.ALL,
                true,
                true);
        }

        // handle the folder itself, undo move op
        super.undoChanges(cms, securityManager, resource, mode);

        if ((mode > CmsResource.UNDO_CONTENT) && (resources != null)) { // recursive?
            // now walk through all sub-resources in the folder
            for (int i = 0; i < resources.size(); i++) {
                CmsResource childResource = (CmsResource)resources.get(i);
                I_CmsResourceType type = getResourceType(childResource.getTypeId());
                if (childResource.isFolder() || childResource.getState() != CmsResource.STATE_NEW) {
                    // recurse into this method for subfolders
                    type.undoChanges(cms, securityManager, childResource, mode);
                } else {
                    if (mode > CmsResource.UNDO_CONTENT_RECURSIVE) { // undo move?
                        String newPath = cms.getRequestContext().removeSiteRoot(
                            securityManager.readResource(
                                cms.getRequestContext(),
                                resource.getStructureId(),
                                CmsResourceFilter.ALL).getRootPath()
                                + childResource.getName());
                        type.moveResource(cms, securityManager, childResource, newPath);
                    }
                }
            }
        }
    }
}