package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanCommand extends Command{

    Logger logger = LoggerFactory.getLogger(CleanCommand.class);

    public CleanCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {

    }

    @Override
    public boolean ejecutar() {
        ejecutarComando(generateExecCommands());
        return Boolean.TRUE;
    }

    private String[] generateExecCommands() {
        logger.info("Ejecutando limpieza...");
        String linkRepo = invocador.getTransaccionInfo().getMensaje().getLinkRepo();
        String folderName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1);
        String rmCommand = isWindows ? "rd /s /q" : "rm -r";
        rmCommand = rmCommand +" " +folderName;
        String cdCommand = "cd " + invocador.getGitFolder();

        String imgCleanCommand = "for /F %i in ('docker images -a -q') do docker rmi -f %i";
        String systemPruneCommand = "docker system prune -f";
        String imgPruneCommand = "docker image prune -f";

        return new String[]{cdCommand,rmCommand, imgCleanCommand, imgPruneCommand, systemPruneCommand};
    }

}
