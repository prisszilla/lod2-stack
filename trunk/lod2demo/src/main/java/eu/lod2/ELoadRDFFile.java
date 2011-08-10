
/*
 * Copyright 2011 LOD2 consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.lod2;

import java.net.*;
import java.net.URI;
import java.io.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.model.*;

import org.restlet.resource.ClientResource;
import org.restlet.data.MediaType;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;
import eu.lod2.ExtractionTab;

/**
 * The ELoadRDFFile allows to upload a file into virtuoso via the conductor
 */
//@SuppressWarnings("serial")
public class ELoadRDFFile extends CustomComponent
{

    // reference to the global internal state
    private ExtractionTab extractionTab;

    public ELoadRDFFile(ExtractionTab etab) {

        // The internal state and 
        extractionTab = etab;

	VerticalLayout panel = new VerticalLayout();

	/* Not necessary 
        Link rdfuploadlink = new Link("Upload RDF content to local storage",
                new ExternalResource(state.getHostName() + "/conductor/rdf_import.vspx?username=dba&t_login_pwd=dba&password=dba"));
        rdfuploadlink.setTargetName("_blank");
        rdfuploadlink.setTargetBorder(Link.TARGET_BORDER_NONE);
        panel.addComponent(rdfuploadlink);
	*/

	try { 
	  	URL url = new URL(extractionTab.getState().getHostName() + "/conductor/rdf_import.vspx?username=dba&t_login_pwd=dba&password=dba");
		Embedded browser = new Embedded("", new ExternalResource(url));
		browser.setType(Embedded.TYPE_BROWSER);
		panel.addComponent(browser);
	} catch (MalformedURLException e) {
                e.printStackTrace();
	};

        // The composition root MUST be set
        setCompositionRoot(panel);
    }

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

};
