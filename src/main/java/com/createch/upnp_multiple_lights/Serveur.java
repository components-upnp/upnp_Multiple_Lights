/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.createch.upnp_multiple_lights;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;

/**
 *
 * @author IDA
 */
public class Serveur implements Runnable {
    
    /**
     * Main
     * Copy code if you need to add a Upnp service on your device
     * @param args
     * @throws Exception
     */

    private Fenetre frameC;

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new Serveur());
        serverThread.setDaemon(false);
        serverThread.start();
    }

    /**
     * Run the UPnP service
     */
    public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Permet de crï¿½er un device
     * Il est possible de crï¿½er plusieurs service pour un mï¿½me device, dans ce cas confer commentaires en fin de methode.
     * @return LocalDevice
     * @throws ValidationException
     * @throws LocalServiceBindingException
     * @throws IOException
     */
    public LocalDevice createDevice()
            throws ValidationException, LocalServiceBindingException, IOException {

        /**
         * Description du Device
         */
        DeviceIdentity identity =
                new DeviceIdentity(
                        UDN.uniqueSystemIdentifier("Multiple Lights")
                );

        DeviceType type =
                new UDADeviceType("TypeSelection", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "Selection Interface Light",					// Friendly Name
                        new ManufacturerDetails(
                                "CreaTech",								// Manufacturer
                                ""),								// Manufacturer URL
                        new ModelDetails(
                                "Selection Light",						// Model Name
                                "un composant qui permet de selectionner une lampe et de l'allumer ou l'éteindre.",	// Model Description
                                "v1" 								// Model Number
                        )
                );


        // Ajout du service


        LocalService<Selection> selectionService =
                new AnnotationLocalServiceBinder().read(Selection.class);

        selectionService.setManager(
                new DefaultServiceManager(selectionService, Selection.class)
        );


        new Fenetre(selectionService).setVisible(true);


        // retour en cas de 1 service
        return new LocalDevice(identity, type, details, selectionService);


		/* Si jamais plusieurs services pour un device (adapter code)
	    return new LocalDevice(
	            identity, type, details,
	            new LocalService[] {switchPowerService, myOtherService}
	    );
	    */
    }

    
}
