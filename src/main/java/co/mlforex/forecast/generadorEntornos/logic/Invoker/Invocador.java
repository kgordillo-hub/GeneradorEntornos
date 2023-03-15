package co.mlforex.forecast.generadorEntornos.logic.Invoker;

import co.mlforex.forecast.generadorEntornos.logic.Command.*;
import co.mlforex.forecast.generadorEntornos.logic.notification.NotificadorSns;
import co.mlforex.forecast.generadorEntornos.model.EntornoVirtualInfo;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import co.mlforex.forecast.generadorEntornos.repository.AmbienteDockerInfoRepo;
import co.mlforex.forecast.generadorEntornos.repository.ImagenDockerInfoRepo;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Invocador extends Thread {

    Logger logger = LoggerFactory.getLogger(Invocador.class);

    private TransaccionInfo transaccionInfo;
    private EntornoVirtualInfo entornoVirtualInfo;
    private CommandHistory commandHistory;

    private String topicArn;

    protected String gitFolder;
    protected String ecrRepoUrl;

    ImagenDockerInfoRepo imagenDockerInfoRepo;

    AmbienteDockerInfoRepo ambienteDockerInfoRepo;

    private Boolean genImagen = Boolean.FALSE;

    public Invocador(EntornoVirtualInfo entornoVirtualInfo, TransaccionInfo transaccionInfo, String topicArn, Boolean genImagen,
                     AmbienteDockerInfoRepo ambienteDockerInfoRepo) {
        this.entornoVirtualInfo = entornoVirtualInfo;
        this.transaccionInfo = transaccionInfo;
        this.topicArn = topicArn;
        this.genImagen = genImagen;
        this.ambienteDockerInfoRepo = ambienteDockerInfoRepo;
        commandHistory = new CommandHistory();
    }

    public Invocador(TransaccionInfo transaccionInfo, String gitFolder, String ecrRepoUrl, String topicArn,
                     Boolean genImagen, ImagenDockerInfoRepo imagenDockerInfoRepo) {
        this.transaccionInfo = transaccionInfo;
        commandHistory = new CommandHistory();
        this.gitFolder = gitFolder;
        this.ecrRepoUrl = ecrRepoUrl;
        this.topicArn = topicArn;
        this.genImagen = genImagen;
        this.imagenDockerInfoRepo = imagenDockerInfoRepo;
    }

    public void crearImagenDocker() {
        try {
            Boolean state = Boolean.TRUE;
            final List<Command> commandsToExecute = new ArrayList<>();
            Command gitCommand = new GitCommand(this);
            Command dockerBuild = new DockerBuildCommand(this);
            Command dockerTag = new DockerTagCommand(this);
            Command dockerPush = new DockerPushCommand(this);
            Command cleanCommand = new CleanCommand(this);

            commandsToExecute.add(gitCommand);
            commandsToExecute.add(dockerBuild);
            commandsToExecute.add(dockerTag);
            commandsToExecute.add(dockerPush);
            commandsToExecute.add(cleanCommand);

            for (final Command c : commandsToExecute) {
                if (state) {
                    commandHistory.push(c);
                    state = c.ejecutar();
                }
            }

            if (state) {
                String linkRepo = transaccionInfo.getMensaje().getLinkRepo();
                String version = transaccionInfo.getVersion();
                String imageName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1).toLowerCase();
                String imageTag = ecrRepoUrl + ":" + imageName + "-" + version;

                transaccionInfo.getMensaje().setImagenGenerada(Boolean.TRUE);
                transaccionInfo.getMensaje().setImageTag(imageTag);
                final String message = new GsonBuilder().disableHtmlEscaping().create().toJson(transaccionInfo);
                logger.info("Notificando evento de generación de imagen...");
                notificarEvento(message);
                logger.info("Actualizando generacion info");
                imagenDockerInfoRepo.save(transaccionInfo);
            }

        } catch (final Exception e) {
            logger.debug("Error en Invocador:crearImagenDocker", e.getMessage());
            throw e;
        }
    }

    public void crearEntornoDocker() {
        try {
            Boolean state = Boolean.TRUE;
            final List<Command> commandsToExecute = new ArrayList<>();
            Command dockerPull = new DockerPullCommand(this);
            Command dockerRun = new DockerRunCommand(this);

            commandsToExecute.add(dockerPull);
            commandsToExecute.add(dockerRun);

            for (final Command c : commandsToExecute) {
                if (state) {
                    commandHistory.push(c);
                    state = c.ejecutar();
                }
            }
            if (state) {
                String currentIP = currentIpAddress();
                entornoVirtualInfo.setIP_API(currentIP != null ? currentIP : "");
                final String message = new GsonBuilder().disableHtmlEscaping().create().toJson(entornoVirtualInfo);
                logger.info("Notificando evento de generación ambiente...");
                notificarEvento(message);
                logger.info("Actualizando ambiente info");
                ambienteDockerInfoRepo.save(entornoVirtualInfo);
            }
        } catch (final Exception e) {
            logger.debug("Error en Invocador:crearEntornoDocker", e.getMessage());
            throw e;
        }
    }

    public void totalRollback() {
        //Include try catch
        Command command;
        logger.info("Iniciando total rollback");
        while ((command = commandHistory.pop()) != null) {
            logger.info("Ejecutando RB...");
            command.rollback();
        }
        if (genImagen) {
            String messsage = "";
            transaccionInfo.getMensaje().setImagenGenerada(Boolean.FALSE);
            //messsage = new GsonBuilder().disableHtmlEscaping().create().toJson(transaccionInfo);
            logger.info("Actualizando transacción");
            imagenDockerInfoRepo.save(transaccionInfo);
            //notificarEvento(messsage);
        }

    }

    @Override
    public void run() {
        if (genImagen) {
            crearImagenDocker();
        } else {
            crearEntornoDocker();
        }
    }

    private String currentIpAddress() {
        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        } catch (UnknownHostException e) {
            logger.debug("Error en Invocador:currentIpAddress",e.getMessage());
            return null;
        }
    }

    public void notificarEvento(String mensaje) {
        new NotificadorSns().publishMessageSns(mensaje, topicArn);
    }

    public TransaccionInfo getTransaccionInfo() {
        return transaccionInfo;
    }

    public String getGitFolder() {
        return gitFolder;
    }

    public String getEcrRepoUrl() {
        return ecrRepoUrl;
    }

    public EntornoVirtualInfo getEntornoVirtualInfo() {
        return entornoVirtualInfo;
    }
}
