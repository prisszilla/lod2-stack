/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.testng.AssertJUnit.*;
/**
 *
 * @author Stefan Schurischuster
 */
public class OntoWiki {
    private static Logger logger = Logger.getLogger(OntoWiki.class);
    
    private static int createCount = 0;
    private WebDriver driver;
    private By frameIdentifier;
    private BasicFunctions bf;
    
    public OntoWiki(By frameIdentifier)  {
        //this.driver = driver;
        this.frameIdentifier = frameIdentifier;
        bf = TestCase.bf;
    }
    
    /**
     * login is required.
     * 
     *@param title 
     *          The title of the knowledge base to create.
     * @param uri
     *          A uri for the knowledge base.
     * @param optionID 
     *          This String contains the element id of the radio button that is
     *          used to determine data import during knowledge base creation.
     *          If this field is left empty the default configuration is used.
     * 
     */
    public void createNewKnowledgeBase(String title, String uri, String optionID)  {
        createCount++;
        assertTrue("Could not create Knowledge Base. Delete and Create did not work.",
                createCount < 3);
        
        bf.waitUntilElementIsVisible("Could not find Edit Menu item.", By.xpath(
                "//div[@id='modellist']//ul[@class='menu clickMenu']/li[contains(.,'Edit')]")).click();
        bf.waitUntilElementIsVisibleFast("Could not find menu Create Knowledge Base menu entry.", 
                By.xpath("//ul[@class='innerBox']//a[.='Create Knowledge Base']")).click();
        
        // Type title and uri
        bf.waitUntilElementIsVisible(By.xpath("//input[@name='title']")).sendKeys(title);
        bf.getVisibleElement(By.xpath("//input[@name='modeluri']")).sendKeys(uri);
        
        // Leave radio buttons untouched.
        if(!optionID.isEmpty())  {
            bf.getVisibleElement("Could not find radio button with id:" +optionID, 
                    By.id(optionID)).click();
        }
        
        // Submit
        bf.getVisibleElement("Could not find 'Create Knowledge Base' submit button.", 
                By.id("createmodel")).click();
        
        if(bf.isElementVisibleAfterWait(By.xpath("//p[contains(@class,'error')]"),4)) {
            // Perform delete
            navigateToContextMenuEntry(uri,"Delete Knowledge");
        
            By element = By.xpath("//div[@class='section-sidewindows']//a[" 
                    +bf.xpathEndsWith("@about", uri) +"]");
            bf.waitUntilElementDisappears("Knowledgebase was not correctly deleted. It is still"
                + "visible after delete.", element);        
            // Restart test case:
            createNewKnowledgeBase(title,uri,optionID);
        }
        bf.checkIFrame(frameIdentifier, By.cssSelector("div.innercontent"));
        bf.waitUntilElementIsVisible("Knwoledge Base "+ title+" was not correctly created. "
                + "Success message was not displayed." , By.xpath("//p[contains(@class,'success')]"));
        assertTrue("New Knowledge Base with title: "+title +" was not added to existing Knowledge Bases.",
                bf.isElementVisible(By.xpath("//a[@about='" +uri+ "']")));
        logger.info("Successfully created a new knowledge base.");
    }
    
    /**
     * Adds data to a existing knowledge base
     * @param knowledgeBaseUri 
     *          identifies the knowledge base to use.
     */
    public void addDataToKnowledgeBaseViaRDFFromWeb(String knowledgeBaseUri, String importUri)  {
        bf.bePatient();
        navigateToContextMenuEntry(knowledgeBaseUri, "Add");
        bf.waitUntilElementIsVisible("Could not find import form"
                + " when trying to add data to a knowledge base.",
                By.id("addmodel"));
        // Choose correct import type
        bf.waitUntilElementIsVisible("Could not find import rdf from web option", 
                By.id("import-basicimporter-rdfweb")).click();
        
        // Submit
        bf.getVisibleElement("Could not find data import submit button.", 
                By.id("addmodel")).click();
        
        bf.waitUntilElementIsVisible("Could not find location input for import from web.", 
                By.id("location-input")).sendKeys(importUri);
        
        // Submit again
        bf.getVisibleElement("Can not find submit button.", 
                By.id("importdata")).click();
        bf.checkIFrame(frameIdentifier, By.cssSelector("div.content"));
        bf.waitUntilElementIsVisible("Data form "+ importUri+" was not correctly added."
                + "Success message was not displayed." , By.xpath("//p[contains(@class,'success')]"));
        logger.info("Successfully added data to knowledge base.");
    }
    
    /**
     * Adds a resource to an existing knowledge base.
     * 
     * @param resourceLabel 
     *              The label of the resource to add.
     */
    public void addResource(String resourceLabel)  {
        bf.waitUntilElementIsVisible("Could not find Edit Menu item.", By.xpath(
                "//div[@id='navigation']//ul[@class='menu clickMenu']/li[contains(.,'Edit')]")).click();
        bf.waitUntilElementIsVisibleFast("Could not find menu Create Knowledge Base menu entry.", 
                By.xpath("//ul[@class='innerBox']//a[contains(.,'Add')]")).click();
        bf.waitUntilElementIsVisibleFast("Add-dialog did not pop up.", 
                By.id("rdfauthor-view"));
        bf.getVisibleElement(By.xpath("//*[starts-with(@id,'literal-value-')]")).sendKeys(resourceLabel);
        // Click submit
        bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        bf.waitUntilElementIsVisible("Data"+ resourceLabel+" was not correctly added.", 
                By.xpath("//div[@id='navigation-content']//a[" +bf.xpathEndsWith("@about", resourceLabel) +"]"));
        logger.info("Successfully added resource with label:" +resourceLabel);
    }
    
    /**
     * Adds a instance to a existing resource.
     * 
     * @param resource
     * @param label 
     */
    public void addInstanceToResource(String resource, String label)  {
        By rscsLocation = By.xpath("//div[@id='navigation-content']//a[" +bf.xpathEndsWith("@about", resource) +"]");
        bf.waitUntilElementIsVisible("Could not find resource with title: "+resource, 
                rscsLocation);
        navigateToContextMenuEntry(resource, "Create");
        
        bf.waitUntilElementIsVisibleFast("Add-dialog did not pop up.", 
                By.id("rdfauthor-view"));
        bf.getVisibleElement(By.xpath("//*[starts-with(@id,'literal-value-')]")).sendKeys(label);
        // Click submit
        bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        bf.checkIFrame(frameIdentifier, By.id("navigation-input"));
        bf.waitUntilElementIsVisible("Could not find resource with title: "+resource, 
                rscsLocation).click();
        
        bf.waitUntilElementIsVisibleFast("Adding of instance: "+label+" was not successful."
                + "It is not listed when clicking the resource:" +resource, 
                By.xpath("//div[@class='innercontent']//a[" +bf.xpathEndsWith("@about", label) +"]"));       
    }
    
    /**
     * Navigates to a knowledge base context menu entry.
     * 
     * @param ident
     *          Identifying text from the @about field of the html element.
     * @param entry 
     *          The entry to click from the context menu.
     */
    public void navigateToContextMenuEntry(String ident, String entry)  {
        By element = By.xpath("//div[@class='section-sidewindows']//a[" +bf.xpathEndsWith("@about", ident) +"]");
        bf.checkIFrame(
                frameIdentifier, 
                element);
        bf.waitUntilElementIsVisible("Could not find knowledge base with uri: "+ ident, 
                element).click();
        bf.bePatient(2000);
        TestCase.driverActions.moveToElement(bf.waitUntilElementIsVisibleFast(
                "Could not find knowledge base with uri: "+ ident, 
                element)).build().perform();
        
        bf.waitUntilElementIsVisibleFast("Context menu button did not pop up.", 
                By.xpath("//a[" +bf.xpathEndsWith("@about", ident) +"]//span[@class='button']")).click();
        
        bf.waitUntilElementIsVisibleFast("Context menu did not pop up.", 
                By.className("contextmenu-enhanced"));
        // Click entry
        bf.getVisibleElement("Could not find context entry: "+entry, 
                By.xpath("//div[@class='contextmenu-enhanced']//a[contains(.,'"+entry+"')]")).click();
        bf.bePatient(1000);
     }
    
    /**
     * Logs into ontoWiki. 
     * pre: ontoWiki frontend must be visible.
     * post: logged in with username.
     * 
     * @param username
     * @param password 
     */
    public void logIntoOntoWiki(String username, String password)  {
        // Check if already logged in
        if(!bf.isElementVisibleAfterWait(By.id("login"),2))  {
            logger.info("Already logged into ontoWiki. Skipping login.");
            return;
        }
        
        bf.waitUntilElementIsVisible("Could not find username input field for logging into onto wiki.",
                By.id("username")).sendKeys(username);
        
         // Skipping password input
        
        
        bf.getVisibleElement("Could not find Login button.", By.id("locallogin")).click();
        logger.info("Successfuly logged in.");
    }
}