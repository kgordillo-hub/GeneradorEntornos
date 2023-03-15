package co.mlforex.forecast.generadorEntornos.logic.Invoker;

import co.mlforex.forecast.generadorEntornos.logic.Command.*;
import co.mlforex.forecast.generadorEntornos.logic.notification.NotificadorSns;
import co.mlforex.forecast.generadorEntornos.model.Mensaje;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import co.mlforex.forecast.generadorEntornos.repository.ImagenDockerInfoRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class Invocador extends Thread {

    private TransaccionInfo transaccionInfo;
    private CommandHistory commandHistory;

    private String topicArn;

    protected String gitFolder;
    protected String ecrRepoUrl;

    ImagenDockerInfoRepo imagenDockerInfoRepo;

    private Boolean genImagen = Boolean.FALSE;

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
                String imageName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1);
                String imageTag = ecrRepoUrl + ":" + imageName + "-" + version;

                transaccionInfo.getMensaje().setImagenGenerada(Boolean.TRUE);
                transaccionInfo.getMensaje().setImageTag(imageTag);
                final String message = new GsonBuilder().disableHtmlEscaping().create().toJson(transaccionInfo);
                System.out.println("Notificando evento...");
                notificarEvento(message);
                System.out.println("Actualizando transacción");
                imagenDockerInfoRepo.save(transaccionInfo);
            }

        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Boolean crearEntornoDocker() {
        Command dockerPull = new DockerPullCommand(this);
        dockerPull.ejecutar();
        commandHistory.push(dockerPull);

        Command dockerRun = new DockerRunCommand(this);
        dockerRun.ejecutar();
        commandHistory.push(dockerRun);

        return Boolean.TRUE;
    }

    public void totalRollback() {
        //Include try catch
        Command command;
        System.out.println("Iniciando rollback");
        while ((command = commandHistory.pop()) != null) {
            System.out.println("Ejecutando RB...");
            command.rollback();
        }
        transaccionInfo.getMensaje().setImagenGenerada(Boolean.FALSE);
        final String messsage = new GsonBuilder().disableHtmlEscaping().create().toJson(transaccionInfo);

        notificarEvento(messsage);
        System.out.println("Actualizando transacción");
        imagenDockerInfoRepo.save(transaccionInfo);
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


    @Override
    public void run() {
        if (genImagen) {
            crearImagenDocker();
        } else {

        }
    }
}
