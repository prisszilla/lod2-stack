/*
 * Copyright 2011 LOD2.eu consortium
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

import com.vaadin.Application;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractOrderedLayout.*;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.data.Property.*;


import eu.lod2.LOD2DemoState;
import eu.lod2.LOD2Exception;
import java.lang.RuntimeException;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class LOD2Demo extends Application 
   implements TabSheet.SelectedTabChangeListener
{

    private LOD2DemoState state;

    private TabSheet tabsheet = new TabSheet();
    private QueryingTab queryingTab;
    private AuthoringTab authoringTab;
    private ExtractionTab extractionTab;
    private EnrichmentTab enrichmentTab;
    private OnlineToolsTab onlineToolsTab;
    private ConfigurationTab configurationTab;
    private TextField currentgraph;

    @Override
        public void init() {

            state = new LOD2DemoState();

            final Window mainWindow = new Window("LOD2 Prototype");
            setTheme("lod2");


            AbsoluteLayout welcome = new AbsoluteLayout();
            welcome.setWidth("99%");
            welcome.setHeight("154px");
            Embedded lod2logo = new Embedded("", new ThemeResource("app_images/LOD2.logo.jpg"));
            lod2logo.setMimeType("image/jpeg");
            lod2logo.addStyleName("lod2logo");
            Label slagzin = new Label("<i>Creating Knowledge out of Interlinked Data</i>");
            slagzin.setContentMode(Label.CONTENT_XHTML);
            welcome.addComponent(lod2logo, "top:10px; left:10px");
            welcome.addComponent(slagzin, "top:20px; left:180px");
            //	welcome.setComponentAlignment(lod2logo, Alignment.TOP_CENTER);
            slagzin.addStyleName("slagzin");

            mainWindow.addComponent(welcome);

            currentgraph = new TextField("Current graph:", state.getCurrentGraph());
	    currentgraph.setNullRepresentation("no graph selected");
            currentgraph.setImmediate(false);
            currentgraph.addListener(new TextChangeListener() {
    		public void textChange(TextChangeEvent event) {
        		currentGraphChange(event);
		}
	    });
            currentgraph.setColumns(50);
	    mainWindow.addComponent(currentgraph);

            //************************************************************************
            // Extraction Tab
            extractionTab = new ExtractionTab(state);	

            tabsheet.addTab(extractionTab);
            tabsheet.getTab(extractionTab).setCaption("Extraction");


            //************************************************************************
            // Querying Tab
            queryingTab = new QueryingTab(state);	

            tabsheet.addTab(queryingTab);
            tabsheet.getTab(queryingTab).setCaption("Querying");

            //************************************************************************
            // Authoring Tab
            authoringTab = new AuthoringTab(state);	

            tabsheet.addTab(authoringTab);
            tabsheet.getTab(authoringTab).setCaption("Authoring");

            //************************************************************************
            // Enrichment Tab
            enrichmentTab = new EnrichmentTab(state);	

            tabsheet.addTab(enrichmentTab);
            tabsheet.getTab(enrichmentTab).setCaption("Enrichment");

            //************************************************************************
            // Online Tools Tab

            onlineToolsTab = new OnlineToolsTab("", state);	

            tabsheet.addTab(onlineToolsTab);
            tabsheet.getTab(onlineToolsTab).setCaption("Online Tools and Services");
            //************************************************************************
            // Configuration Tab
            configurationTab = new ConfigurationTab(state);	

            tabsheet.addTab(configurationTab);
            tabsheet.getTab(configurationTab).setCaption("Configuration");

            tabsheet.addListener(this);
            mainWindow.addComponent(tabsheet);


            setMainWindow(mainWindow);

        }

     public void selectedTabChange(SelectedTabChangeEvent event) {

            final TabSheet source = (TabSheet) event.getSource();

            if (source == tabsheet) {
		    propagateData(tabsheet);
            };
	    currentgraph.setValue(state.getCurrentGraph());
     };

    public void currentGraphChange(TextChangeEvent event) {

	if (event != null && event.getText() != null) {
        	state.setCurrentGraph(event.getText());
	};
	propagateData(tabsheet);

    };

    public void propagateData(TabSheet source) {
	if (source.getSelectedTab() == queryingTab) {
	    queryingTab.setDefaults();
	} else if (source.getSelectedTab() == authoringTab) {
	    authoringTab.setDefaults();
	} else if (source.getSelectedTab() == extractionTab) {
	    extractionTab.setDefaults();
	} else if (source.getSelectedTab() == enrichmentTab) {
	    enrichmentTab.setDefaults();
	} else if (source.getSelectedTab() == onlineToolsTab) {
	    onlineToolsTab.setDefaults();
	} else if (source.getSelectedTab() == configurationTab) {
	    configurationTab.setDefaults();
	} else {
	    
	}
    }
}