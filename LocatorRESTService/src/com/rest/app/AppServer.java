package com.rest.app;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

public class AppServer {
	public static void main(String[] args) throws IOException {
        System.out.println("Starting Embedded Jersey HTTPServer...\n");
        HttpServer httpServer = createHttpServer();
        httpServer.start();
        System.out.println(String.format("\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n", getURI()));
        System.out.println("Started Embedded Jersey HTTPServer Successfully !!!");
    }
 
        private static HttpServer createHttpServer() throws IOException {
        ResourceConfig resourceConfig = new PackagesResourceConfig("com.locator.service");
        return HttpServerFactory.create(getURI(), resourceConfig);
    }
 
    private static URI getURI() {
        return UriBuilder.fromUri("http://" + getHostName() + "/").port(8080).build();
    }
 
    private static String getHostName() {
        return "localhost";
    }
}
