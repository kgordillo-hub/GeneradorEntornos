package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitCommand extends Command {

    Logger logger = LoggerFactory.getLogger(GitCommand.class);

    public GitCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        logger.info("Ejecutando Rollback Git");
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
        logger.info("Ejecutando git clone...");

        String cdCommand = "cd " + invocador.getGitFolder();
        String gitClone = "git clone " + invocador.getTransaccionInfo().getMensaje().getLinkRepo();

        return new String[]{cdCommand, gitClone};
    }

    private String[] generateRollbackCommands() {
        String rmCommand = isWindows ? "rd /s /q" : "rm -r";
        String cdCommand = "cd " + invocador.getGitFolder();
        String linkRepo = invocador.getTransaccionInfo().getMensaje().getLinkRepo();
        String folderName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1);
        rmCommand = rmCommand +" " +folderName;

        return new String[]{cdCommand, rmCommand};
    }
}
