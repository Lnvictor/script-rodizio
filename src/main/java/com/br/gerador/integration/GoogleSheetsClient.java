package com.br.gerador.integration;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsClient {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.DRIVE);
    private final NetHttpTransport HTTP_TRANSPORT;

    private final String credentialsPath;
    private final String tokenPath;
    private final String appName;
    private final String email;

    public GoogleSheetsClient(String credentialsPath, String tokenPath, String appName, String email) throws GeneralSecurityException, IOException {
        this.credentialsPath = credentialsPath;
        this.tokenPath = tokenPath;
        this.appName = appName;
        this.email = email;

        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    }

    private AuthorizationCodeInstalledApp configureApp() throws  IOException {
        File file = new File(this.credentialsPath);
        InputStream in = Files.newInputStream(file.toPath());
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(this.tokenPath)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver);
    }

    public Sheets newSheetsService() {
        try {
            var app = this.configureApp();
            return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, app.authorize(this.email))
                    .setApplicationName(this.appName)
                    .build();
        }
        catch (Exception ex){
            System.out.println("Deu ruim, irmão!!");
            return null;
        }
    }

    //public void writeNewTable(String spreedsheetId, )
}
