package co.mlforex.forecast.generadorEntornos.logic.Command;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class Command {

    Logger logger = LoggerFactory.getLogger(Command.class);

    Invocador invocador;

    protected boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

    public Command(Invocador invocador){
        this.invocador = invocador;
    }
    public abstract void rollback();
    public abstract boolean ejecutar();

    protected Boolean ejecutarComando(String [] comandos){
        try{
            String shell = isWindows ? "cmd.exe":"sh";
            ProcessBuilder builder = new ProcessBuilder(shell);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            if (null != process) {
                final OutputStream out = process.getOutputStream();
                final OutputStreamWriter outWriter = new OutputStreamWriter(out);
                final BufferedWriter bWriter = new BufferedWriter(outWriter);
                for (String c : comandos) {
                    bWriter.write(c);
                    bWriter.newLine();
                    bWriter.flush();
                }
                bWriter.close();
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            logger.info("Info de ejecución de comandos...");
            while ((line = r.readLine()) != null) {
                logger.info("Line: " + line);
                if(line.toLowerCase().contains("not found")|| line.toLowerCase().contains("error")||
                        line.toLowerCase().contains("fatal") || line.toLowerCase().contains("invalid")
                        || line.toLowerCase().contains("failed")){
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }catch (final Exception e){
            logger.debug("Error en Command.ejecutarComando: ",e.getMessage());
            return Boolean.FALSE;
        }
    }
}
