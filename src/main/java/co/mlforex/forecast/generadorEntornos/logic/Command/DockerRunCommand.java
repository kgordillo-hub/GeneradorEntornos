package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;

public class DockerRunCommand extends Command{
    public DockerRunCommand(Invocador invocador) {
        super(invocador);
    }

    @Override
    public void rollback() {
        //llamar rollback general
    }

    @Override
    public boolean ejecutar() {
        //Agregar docker run
        return false;
    }
}
