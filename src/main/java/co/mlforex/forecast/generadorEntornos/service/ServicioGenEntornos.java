package co.mlforex.forecast.generadorEntornos.service;

import co.mlforex.forecast.generadorEntornos.model.EntornoVirtualInfo;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import org.springframework.stereotype.Service;

public interface ServicioGenEntornos {

    Boolean generarImagenDocker(TransaccionInfo transaccionInfo);

    Boolean generarEntornoDocker(EntornoVirtualInfo transaccionInfo);
}
