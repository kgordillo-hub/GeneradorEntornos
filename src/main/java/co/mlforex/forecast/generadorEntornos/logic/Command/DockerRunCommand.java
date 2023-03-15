package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerRunCommand extends Command{

    Logger logger = LoggerFactory.getLogger(DockerRunCommand.class);

    public DockerRunCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        //llamar rollback general
        logger.info("Ejecutando Rollback docker run");
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
        logger.info("Ejecutando docker run...");
        String imageTag = invocador.getTransaccionInfo().getMensaje().getImageTag();
        String numeroPuerto = invocador.getEntornoVirtualInfo().getNumeroPuerto()+"";
        String dockerRun = "docker run -d -p "+numeroPuerto+":5000 --restart always "+imageTag;

        return new String[]{dockerRun};
    }

    private String[] generateRollbackCommands() {
        String dockerSysPrune = "docker system prune -f";
        return new String[]{dockerSysPrune};
    }
}
