
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
import com.vaadin.terminal.FileResource;


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
 * extract RDF data from an XML file using an XSLT transformation
 */
//@SuppressWarnings("serial")
public class EXML extends CustomComponent
    implements 	TextChangeListener,
    		Upload.SucceededListener,
                Upload.FailedListener,
                Upload.Receiver 
{

    // reference to the global internal state
    private ExtractionTab extractionTab;

    private VerticalLayout panel;
    File  xmlFile;         // The original file
    File  rdfFile;         // The RDF file containing the triples derived via the XSLT
    File  xsltFile;	   // The XSLT transformation file

    // 
    private Button annotateButton;
    private Label annotatedTextField;

    private String textToAnnotate;
    private String annotatedText;

    public EXML(ExtractionTab etab) {

        // The internal state and 
        extractionTab = etab; 

        panel = new VerticalLayout();

	// Create the Upload component for the XML file.
        final Upload uploadXMLFile =
                new Upload("Upload the XML file here", this);

        uploadXMLFile.setButtonCaption("Upload Now");
        uploadXMLFile.addListener(new Upload.SucceededListener() {
	    public void uploadSucceeded(Upload.SucceededEvent event) {
		// Log the upload on screen.
		panel.addComponent(new Label("File " + event.getFilename()
			+ " of type '" + event.getMIMEType()
			+ "' uploaded."));
	    }
			});
        uploadXMLFile.addListener((Upload.FailedListener) this);

        panel.addComponent(uploadXMLFile);

	// Create the Upload component for the XSLT file.
        final Upload uploadXSLTFile =
                new Upload("Upload the XLST file here", this);

        uploadXSLTFile.setButtonCaption("Upload Now");
        uploadXSLTFile.addListener((Upload.SucceededListener) this);
        uploadXSLTFile.addListener((Upload.FailedListener) this);
        panel.addComponent(uploadXSLTFile);

        panel.addComponent(new Label("Click 'Browse' to "+
                "select a file and then click 'Upload'."));



        Form t2f = new Form();
        t2f.setCaption("");

        TextArea textToAnnotateField = new TextArea("XSLT:");
        textToAnnotateField.setImmediate(false);
        textToAnnotateField.addListener(this);
        textToAnnotateField.setColumns(50);
        textToAnnotateField.setRows(10);
        t2f.getLayout().addComponent(textToAnnotateField);

        annotatedTextField = new Label("Extracted RDF", Label.CONTENT_XHTML);
        t2f.getLayout().addComponent(annotatedTextField);

        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        annotateButton = new Button("transfrom XML to RDF", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                applyXSLT(event);
            }
        });
        annotateButton.setDescription("transform the XML to RDF using the XSLT transformation");
        annotateButton.setEnabled(false);

        t2f.getFooter().addComponent(annotateButton);

        panel.addComponent(t2f);



        // The composition root MUST be set
        setCompositionRoot(panel);
    }

    public void textChange(TextChangeEvent event) {

        textToAnnotate = event.getText();
        if (textToAnnotate == null || textToAnnotate.equals("")) {
            annotateButton.setEnabled(false);
        } else {    
            String encoded = "";
            try {
                encoded = URLEncoder.encode(textToAnnotate, "UTF-8");
                annotateButton.setEnabled(true);
            } catch (UnsupportedEncodingException e) { 
                annotateButton.setEnabled(false);
                e.printStackTrace();
            };
        };

    }

    private void applyXSLT(ClickEvent event) {
	    /* should be replaced with an xslt call */
        try {
		// 1. Instantiate a TransformerFactory.
		javax.xml.transform.TransformerFactory tFactory = 
				  javax.xml.transform.TransformerFactory.newInstance();

		// 2. Use the TransformerFactory to process the stylesheet Source and
		//    generate a Transformer.
		javax.xml.transform.Transformer transformer = tFactory.newTransformer
				(new javax.xml.transform.stream.StreamSource(xsltFile));

		// 3. Use the Transformer to transform an XML Source and send the
		//    output to a Result object.
		transformer.transform
		    (new javax.xml.transform.stream.StreamSource(xmlFile), 
		     new javax.xml.transform.stream.StreamResult( new
						  java.io.FileOutputStream(rdfFile)));

            String encoded = "";
            encoded = URLEncoder.encode(textToAnnotate, "UTF-8");
            ClientResource restcall = new ClientResource(
                    "http://spotlight.dbpedia.org/rest/annotate?text=" + encoded + "&confidence=0.4&support=20");

            //            String result = restcall.get().getText();  
	    //            TEXT_XML is usefull to have the resources already extracted, 
	    //            but it does not render directly on a label content.
            // String result = restcall.get(MediaType.TEXT_XML).getText();  
	    //     APPLICATION_XHTML will return an annotated text with rdfa.
            String result = restcall.get(MediaType.APPLICATION_XHTML).getText();  
            annotatedTextField.setValue(result);
        } catch (UnsupportedEncodingException e) { 
            annotateButton.setEnabled(false);
            e.printStackTrace();
        } catch (IOException e) { 
            annotateButton.setEnabled(false);
            e.printStackTrace();
	} catch (javax.xml.transform.TransformerConfigurationException e) {
            annotateButton.setEnabled(false);
            e.printStackTrace();
	} catch (javax.xml.transform.TransformerException e) {
            annotateButton.setEnabled(false);
            e.printStackTrace();
        };

    };

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

	// Callback method to begin receiving the upload.
    public OutputStream receiveUpload(String filename,
                                      String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to
        xmlFile = new File("/tmp/uploads/" + filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(xmlFile);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }

        return fos; // Return the output stream to write to
    }

    // This is called if the upload is finished.
    public void uploadSucceeded(Upload.SucceededEvent event) {
        // Log the upload on screen.
        panel.addComponent(new Label("File " + event.getFilename()
                + " of type '" + event.getMIMEType()
                + "' uploaded."));
        
    }

    // This is called if the upload fails.
    public void uploadFailed(Upload.FailedEvent event) {
        // Log the failure on screen.
        panel.addComponent(new Label("Uploading "
                + event.getFilename() + " of type '"
                + event.getMIMEType() + "' failed."));
    }


};
