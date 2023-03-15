package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerBuildCommand extends Command{

    Logger logger = LoggerFactory.getLogger(DockerBuildCommand.class);

    public DockerBuildCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        logger.info("Ejecutando rollback de docker build");
        //Remove created folder if any
        ejecutarComando(generateRollbackCommands());
    }

    @Override
    public boolean ejecutar() {
        //Execute Docker build
        Boolean estadoEjecucion = ejecutarComando(generateExecCommands());
        if (!estadoEjecucion) {
            invocador.totalRollback();
        }
        return estadoEjecucion;
    }

    private String[] generateExecCommands() {
        logger.info("Ejecutando docker build...");
        String linkRepo = invocador.getTransaccionInfo().getMensaje().getLinkRepo();
        String folderName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1);

        String cdCommand = "cd " + invocador.getGitFolder() + folderName;
        String imageName = folderName.toLowerCase();
        String version = invocador.getTransaccionInfo().getVersion();
        String dockerBuild = "docker build . -t " + imageName + ":" +version;

        return new String[]{cdCommand, dockerBuild};
    }

    private String[] generateRollbackCommands() {
        String dockerPrune = "docker system prune -f";
        return new String[]{dockerPrune};
    }
}
