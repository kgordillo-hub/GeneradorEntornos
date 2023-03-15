package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;

public class DockerTagCommand extends Command{
    public DockerTagCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        //Call general rollback
        System.out.println("Rollback docker image tag");
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
        System.out.println("Ejecutando docker tag...");
        String linkRepo = invocador.getTransaccionInfo().getMensaje().getLinkRepo();
        String folderName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1);
        String imageName = folderName.toLowerCase();
        String version = invocador.getTransaccionInfo().getVersion();
        String dockerTag = "docker tag " + imageName + ":" +version +" "+invocador.getEcrRepoUrl()+":"
                +imageName + "-" +version;

        return new String[]{dockerTag};
    }

    private String[] generateRollbackCommands() {
        String dockerPrune = "docker image prune -f";
        return new String[]{dockerPrune};
    }
}
