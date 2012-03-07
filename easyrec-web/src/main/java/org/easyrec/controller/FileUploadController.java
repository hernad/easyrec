/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.controller;

import org.easyrec.model.web.FileUploadBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;

/**
 * This controller provides file upload for the webapp.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2010</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 10:46:56 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17653 $</p>
 *
 * @author Roman Cerny
 */
public class FileUploadController extends SimpleFormController {


    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
                                    BindException errors) throws Exception {
        logger.debug("FileUploadController: onSubmit called");

        // cast the bean
        FileUploadBean bean = (FileUploadBean) command;
        if (bean == null || bean.getFile() == null || bean.getFile().isEmpty()) {
            logger.info("no file or empty file was uploaded, aborting");
            // well, let's do nothing with the bean for now and return
            return super.onSubmit(request, response, command, errors);
        }
        // check if there's content there
        MultipartFile file = bean.getFile();
        if (file != null) {
            FileOutputStream fos = null;
            try {

                ConfigurableApplicationContext appContext = (ConfigurableApplicationContext) getApplicationContext();
                String pathToWebapp = ((WebApplicationContext) appContext).getServletContext().getRealPath("/");
                File backUpFile = new File(
                        pathToWebapp + File.separator + "upload" + System.currentTimeMillis() + ".csv");
                fos = new FileOutputStream(backUpFile);
                fos.write(file.getBytes());

                logger.info("file has been uploaded successfully to folder: "); // + ontologyFileFolder);

            } catch (Exception e) {
                logger.error(e);
                throw e;
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }

        // well, let's do nothing with the bean for now and return
        return super.onSubmit(request, response, command, errors);
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them
    }
}
