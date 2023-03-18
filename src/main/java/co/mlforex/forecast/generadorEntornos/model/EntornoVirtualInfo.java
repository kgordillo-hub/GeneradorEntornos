package co.mlforex.forecast.generadorEntornos.model;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;

@DynamoDBTable(tableName = "AmbienteDockerInfo")
public class EntornoVirtualInfo implements Serializable {

    private String UID;
    //Llave
    @DynamoDBAttribute
    private String nombreApp;
    @DynamoDBAttribute
    private String version;
    @DynamoDBAttribute
    private Integer numeroPuerto;

    //Atributos
    @DynamoDBAttribute
    private String ipAPI;
    @DynamoDBAttribute
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

    public String getIpAPI() {
        return ipAPI;
    }

    public void setIpAPI(String ipAPI) {
        this.ipAPI = ipAPI;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @DynamoDBHashKey(attributeName = "UID")
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String generateUID(){
        return DigestUtils.md5Hex(nombreApp.toLowerCase()+":"+version+":"+numeroPuerto);
    }
}
