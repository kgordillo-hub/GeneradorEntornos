package co.mlforex.forecast.generadorEntornos.model;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.io.Serializable;

@DynamoDBTable(tableName = "AmbienteDockerInfo")
public class EntornoVirtualInfo implements Serializable {

    private String UID;
    //Llave
    private String nombreApp;
    private String version;
    private Integer numeroPuerto;

    //Atributos
    private String IP_API;
    private String idUsuario;

    public String getNombreApp() {
        return nombreApp;
    }

    public void setNombreApp(String nombreApp) {
        this.nombreApp = nombreApp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getNumeroPuerto() {
        return numeroPuerto;
    }

    public void setNumeroPuerto(Integer numeroPuerto) {
        this.numeroPuerto = numeroPuerto;
    }

    public String getIP_API() {
        return IP_API;
    }

    public void setIP_API(String IP_API) {
        this.IP_API = IP_API;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
