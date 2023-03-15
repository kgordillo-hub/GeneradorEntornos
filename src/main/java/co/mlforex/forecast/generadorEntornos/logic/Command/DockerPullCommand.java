package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerPullCommand extends Command{

    Logger logger = LoggerFactory.getLogger(DockerPullCommand.class);

    public DockerPullCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        logger.info("Ejecutando rollback docker pull");
        //Remove created folder if any
        ejecutarComando(generateRollbackCommands());
    }

    @Override
    public boolean ejecutar() {
        Boolean estadoEjecucion = ejecutarComando(generateExecCommands());
        if (!estadoEjecucion) {
            invocador.totalRollback();
        }
        return estadoEjecucion;
    }


    private String[] generateExecCommands() {
        logger.info("Ejecutando docker pull...");
        String imageTag = invocador.getTransaccionInfo().getMensaje().getImageTag();
        String awsAuth = "aws ecr get-login-password --region us-east-1 --profile mlforex| docker login --username AWS " +
                "--password-stdin 703106094997.dkr.ecr.us-east-1.amazonaws.com";
        String dockerPull = "docker pull " +imageTag;

        return new String[]{awsAuth, dockerPull};
    }

    private String[] generateRollbackCommands() {
        String dockerImgPrune = "docker image prune -f";
        String dockerSysPrune = "docker system prune -f";
        return new String[]{dockerImgPrune, dockerSysPrune};
    }
}
