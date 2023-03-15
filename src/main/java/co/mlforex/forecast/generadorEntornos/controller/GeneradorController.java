package co.mlforex.forecast.generadorEntornos.controller;

import co.mlforex.forecast.generadorEntornos.model.EntornoVirtualInfo;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import co.mlforex.forecast.generadorEntornos.service.ServicioGenEntornos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GeneradorController {

    @Autowired
    ServicioGenEntornos servicioGenEntornos;

    @PostMapping("/generarImagen")
    public ResponseEntity<String> generarImagenDocker(@RequestBody TransaccionInfo transaccionInfo) {
        try {
            System.out.println("UID: " + transaccionInfo.getUID());
            System.out.println("Entra a generar Imagen...");
            System.out.println("El transaction ID es:" + transaccionInfo.getIdTransaccion());
            System.out.println("Info objeto" + transaccionInfo.toString());
            System.out.println(transaccionInfo.getMensaje().toString());
            if (servicioGenEntornos.generarImagenDocker(transaccionInfo)) {
                return new ResponseEntity<>("Generated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Configuration already exists", HttpStatus.NO_CONTENT);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/generarEntorno")
    public ResponseEntity<String> geneerarEntornoDocker(EntornoVirtualInfo transaccionInfo) {
        if (servicioGenEntornos.generarEntornoDocker(transaccionInfo)) {
            return new ResponseEntity<>("Generated", HttpStatus.OK);
        }
        return null;
    }
}
