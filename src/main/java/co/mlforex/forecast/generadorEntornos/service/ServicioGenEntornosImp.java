package co.mlforex.forecast.generadorEntornos.service;

import co.mlforex.forecast.generadorEntornos.logic.Invoker.Invocador;
import co.mlforex.forecast.generadorEntornos.model.EntornoVirtualInfo;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import co.mlforex.forecast.generadorEntornos.repository.AmbienteDockerInfoRepo;
import co.mlforex.forecast.generadorEntornos.repository.ImagenDockerInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ServicioGenEntornosImp implements ServicioGenEntornos {


    @Autowired
    ImagenDockerInfoRepo imagenDockerInfoRepo;

    @Autowired
    AmbienteDockerInfoRepo ambienteDockerInfoRepo;

    @Value("${git.repo.folder}")
    String repoFolder;
    @Value("${aws.ecr.repo.url}")
    String ecrUrl;
    @Value("${aws.sns.topic.genImgOut.arn}")
    String topicArnImgOut;

    @Value("${aws.sns.topic.genEnvOut.arn}")
    String topicArnEnvOut;

    @Override
    public Boolean generarImagenDocker(TransaccionInfo transaccionInfo) {

        final String UID = transaccionInfo.generateUID();
        final TransaccionInfo tInfo = findByAppV(transaccionInfo);
        // Si no esta vacio quiere decir que ya hay una imagen asociada
        if (tInfo != null) {
            return Boolean.FALSE;
        } else if (transaccionInfo.getMensaje() != null) {
            transaccionInfo.setUID(UID);
            transaccionInfo.getMensaje().setImagenGenerada(Boolean.FALSE);
            new Invocador(transaccionInfo, repoFolder, ecrUrl, topicArnImgOut, Boolean.TRUE, imagenDockerInfoRepo).start();
            imagenDockerInfoRepo.save(transaccionInfo);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean generarEntornoDocker(EntornoVirtualInfo entornoInfo) {
        entornoInfo.setNumeroPuerto(getNextPortAvaliable());
        String UID = entornoInfo.generateUID();
        entornoInfo.setUID(UID);

        final TransaccionInfo ti = findByAppV(new TransaccionInfo(entornoInfo.getNombreApp(), entornoInfo.getVersion()));
        if (ti != null) {
            new Invocador(entornoInfo, ti, topicArnEnvOut, Boolean.FALSE, ambienteDockerInfoRepo).start();
            //ambienteDockerInfoRepo.save(entornoInfo);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }

    }

    private Integer getNextPortAvaliable(){
        final Iterable<EntornoVirtualInfo> transactions = ambienteDockerInfoRepo.findAll();
        Integer numeroPuertoDefecto = 4999;
        if (transactions != null) {
            final Iterator<EntornoVirtualInfo> it = transactions.iterator();
            while (it.hasNext()) {
                EntornoVirtualInfo ev = it.next();
                if(ev.getNumeroPuerto() >= numeroPuertoDefecto){
                    numeroPuertoDefecto = ev.getNumeroPuerto();
                }
            }
        }
        return numeroPuertoDefecto + 1;
    }

    private TransaccionInfo findByAppV(TransaccionInfo transaccionInfo) {
        final Iterable<TransaccionInfo> transactions = imagenDockerInfoRepo.findAll();
        if (transactions != null) {
            final Iterator<TransaccionInfo> it = transactions.iterator();
            while (it.hasNext()) {
                TransaccionInfo ti = it.next();
                String nombreApp = ti.getNombreApp().toLowerCase();
                String version = ti.getVersion().toLowerCase();
                if (transaccionInfo.getNombreApp().equals(nombreApp) && transaccionInfo.getVersion().equals(version)
                        && ti.getMensaje().getImagenGenerada()) {
                    return ti;
                }
            }
        }
        return null;
    }

}
