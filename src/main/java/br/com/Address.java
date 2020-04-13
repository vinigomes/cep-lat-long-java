package br.com;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Address {

    @CsvBindByName(column = "cep")
    @CsvBindByPosition(position = 0)
    private String cep;

    @CsvBindByName(column = "latitude")
    @CsvBindByPosition(position = 1)
    private String latitude;

    @CsvBindByName(column = "longitude")
    @CsvBindByPosition(position = 2)
    private String longitude;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
