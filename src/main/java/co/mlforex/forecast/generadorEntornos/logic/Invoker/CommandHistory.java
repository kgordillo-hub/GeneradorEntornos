package co.mlforex.forecast.generadorEntornos.logic.Invoker;

import co.mlforex.forecast.generadorEntornos.logic.Command.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private List<Command> listaComandos;

    public CommandHistory(){
        this.listaComandos = new ArrayList<>();
    }

    public void push(Command command){
        this.listaComandos.add(command);
    }

    public Command pop(){
        if(!listaComandos.isEmpty()){
            return listaComandos.remove(listaComandos.size() -1);
        }else{
            return null;
        }

    }
}
