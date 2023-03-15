package co.mlforex.forecast.generadorEntornos.controller;

import co.mlforex.forecast.generadorEntornos.model.EntornoVirtualInfo;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import co.mlforex.forecast.generadorEntornos.service.ServicioGenEntornos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GeneradorController {

    Logger logger = LoggerFactory.getLogger(GeneradorController.class);

    @Autowired
    ServicioGenEntornos servicioGenEntornos;

    @PostMapping("/generarImagen")
    public ResponseEntity<String> generarImagenDocker(@RequestBody TransaccionInfo transaccionInfo) {
        try {
            logger.info("Entra a generar Imagen...");
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
    public ResponseEntity<String> geneerarEntornoDocker(@RequestBody EntornoVirtualInfo envInfo) {
        try{
            logger.info("Entra a generar entorno de ejecucion...");
            if (servicioGenEntornos.generarEntornoDocker(envInfo)) {
                return new ResponseEntity<>("Request dispatched", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Not able to run the docker", HttpStatus.NO_CONTENT);
            }
        }catch(final Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
