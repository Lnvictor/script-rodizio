package com.br.gerador;

import com.br.gerador.config.Constants;
import com.br.gerador.domain.Rodizio;
import com.br.gerador.integration.GoogleSheetsClient;
import com.br.gerador.service.RodizioService;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        GoogleSheetsClient client = new GoogleSheetsClient(Constants.CREDENTIALS_PATH,
                Constants.TOKENS_DIRECTORY_PATH, Constants.APP_NAME, Constants.EMAIL);

        Sheets service = client.newSheetsService();


        RodizioService s =  new RodizioService(client);
        Rodizio rodizio = s.generateNextRodizios("Aparecida", "Esther", 8 ,8);
        s.generateSheetsFormat(rodizio);
    }
}
