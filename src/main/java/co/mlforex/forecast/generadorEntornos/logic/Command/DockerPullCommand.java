package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;

public class DockerPullCommand extends Command{
    public DockerPullCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        //eliminar imagen local
    }

    @Override
    public boolean ejecutar() {
        //obtener docker del repo
        return Boolean.FALSE;
    }
}
