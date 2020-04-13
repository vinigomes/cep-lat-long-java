package br.com;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.util.List;

public class Services {

    private final String GOOGLE_MAPS_API_KEY = System.getenv("GOOGLE_MAPS_API_KEY");

    public List<Address> readCepFromCSV(InputStream inputStream) throws BusinessException {
        List<Address> addresses = null;
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withSkipLines(1).build()) {
            ColumnPositionMappingStrategy positionStrategyMapping = new ColumnPositionMappingStrategy();
            positionStrategyMapping.setType(Address.class);
            CsvToBean cb = new CsvToBeanBuilder(reader)
                    .withType(Address.class)
                    .withMappingStrategy(positionStrategyMapping)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            addresses = cb.parse();
        } catch (IOException e) {
            throw new BusinessException("Ocurred an error to read cep from CSV. See the log files.", e);
        }
        return addresses;
    }

    public List<Address> convertCsvWithCepToLatitudeLongitude(List<Address> addresses) throws BusinessException {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(GOOGLE_MAPS_API_KEY).build();
        try {
            GeocodingResult[] results;
            for (Address address : addresses) {
                results =  GeocodingApi.geocode(context, address.getCep()).region("BR").await();
                LatLng location = results[0].geometry.location;
                Double latitude = location.lat;
                Double longitude = location.lng;
                address.setLatitude(latitude.toString());
                address.setLongitude(longitude.toString());
            }
        } catch (InterruptedException | IOException | ApiException e) {
            throw new BusinessException("Ocurred an error to get latitude and longitude from Google Maps API. See the log files.", e);
        }
        return addresses;
    }

    public void writeCSV(String fullPath, List<Address> addresses) throws BusinessException {
        Writer writer  = null;
        try {
            writer = new FileWriter(fullPath);
            HeaderColumnNameMappingStrategy headerStrategyMapping = new HeaderColumnNameMappingStrategy();
            headerStrategyMapping.setType(Address.class);
            StatefulBeanToCsvBuilder<Address> builder = new StatefulBeanToCsvBuilder<>(writer).withMappingStrategy(headerStrategyMapping);
            StatefulBeanToCsv<Address> beanWriter = builder.build();
            beanWriter.write(addresses);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            throw new BusinessException("Ocurred an error to write the CSV file. See the log files.", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new BusinessException("Ocurred an error to close writer object. See the log files.", e);
            }
        }
    }


}
