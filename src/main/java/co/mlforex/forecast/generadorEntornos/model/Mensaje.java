package co.mlforex.forecast.generadorEntornos.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

import java.io.Serializable;
import java.util.List;

@DynamoDBDocument
public class Mensaje implements Serializable {
    @DynamoDBAttribute
    private String linkRepo;
    @DynamoDBAttribute
    private String branchRepoName;
    @DynamoDBAttribute
    private String lastCommitHash;
    @DynamoDBAttribute
    private String idUsuario;
    @DynamoDBAttribute
    private String imageTag;
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    @DynamoDBAttribute
    private Boolean imagenGenerada;

    public Mensaje(){

    }

    public String getLinkRepo() {
        return linkRepo;
    }

    public void setLinkRepo(String linkRepo) {
        this.linkRepo = linkRepo;
    }

    public String getBranchRepoName() {
        return branchRepoName;
    }

    public void setBranchRepoName(String branchRepoName) {
        this.branchRepoName = branchRepoName;
    }

    public String getLastCommitHash() {
        return lastCommitHash;
    }

    public void setLastCommitHash(String lastCommitHash) {
        this.lastCommitHash = lastCommitHash;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    public Boolean getImagenGenerada() {
        return imagenGenerada;
    }

    public void setImagenGenerada(Boolean imagenGenerada) {
        this.imagenGenerada = imagenGenerada;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "linkRepo='" + linkRepo + '\'' +
                ", branchRepoName='" + branchRepoName + '\'' +
                ", lastCommitHash='" + lastCommitHash + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", imageTag='" + imageTag + '\'' +
                ", imagenGenerada=" + imagenGenerada +
                '}';
    }
}
