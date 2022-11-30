package de.uhd.ifi.se.accompleteness;

import de.uhd.ifi.se.accompleteness.rest.RunRest;
import de.uhd.ifi.se.accompleteness.rest.StatusRest;

import static spark.Spark.*;

/**
 * The main class of the application which starts a server and creates the
 * API endpoint listeners.
 * 
 * @see RunRest
 * @see StatusRest
 */
public class App {

    /**
     * The constructor of the {@link App} class containing the API endpoint
     * definitions.
     * 
     * @param port The port the server is listening to
     */
    public App(int port) {
        port(port);
        staticFiles.externalLocation("/uvl-acceptance-criteria/target/site/apidocs");

        StatusRest statusRest = new StatusRest();
        RunRest runRest = new RunRest();
        get("/hitec/classify/concepts/acceptance-criteria-completeness/status", statusRest::createResponse);
        post("/hitec/classify/concepts/acceptance-criteria-completeness/run", runRest::createResponse);
    }

    
    /** 
     * The main method of the {@link App} which starts a server listening to
     * port 9640.
     * 
     * @param args The command line arguments passed to the application
     */
    public static void main( String[] args ) {
        new App(9640);
    }
    
}
