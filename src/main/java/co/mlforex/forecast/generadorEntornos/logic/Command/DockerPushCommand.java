package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;

public class DockerPushCommand extends Command{
    public DockerPushCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        //Call general rollback
        System.out.println("Rollback docker image push");
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
        System.out.println("Ejecutando docker push...");
        String linkRepo = invocador.getTransaccionInfo().getMensaje().getLinkRepo();
        String folderName = linkRepo.substring(linkRepo.lastIndexOf('/') + 1);
        String imageName = folderName.toLowerCase();
        String version = invocador.getTransaccionInfo().getVersion();
        String awsAuth = "aws ecr get-login-password --region us-east-1 --profile mlforex| docker login --username AWS " +
                "--password-stdin 703106094997.dkr.ecr.us-east-1.amazonaws.com";
        String dockerPush = "docker push " +invocador.getEcrRepoUrl()+":"+imageName + "-" +version;

        return new String[]{awsAuth, dockerPush};
    }

    private String[] generateRollbackCommands() {
        String dockerRmImg = "for /F %i in ('docker images -a -q') do docker rmi -f %i";
        return new String[]{dockerRmImg};
    }
}
