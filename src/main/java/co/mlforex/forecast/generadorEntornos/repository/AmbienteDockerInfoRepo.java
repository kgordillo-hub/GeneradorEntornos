package co.mlforex.forecast.generadorEntornos.repository;

import co.mlforex.forecast.generadorEntornos.model.EntornoVirtualInfo;
import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface AmbienteDockerInfoRepo extends CrudRepository<EntornoVirtualInfo, String> {

    Optional<EntornoVirtualInfo> findById(String id);

    EntornoVirtualInfo save(EntornoVirtualInfo entornoVirtualInfo);
}
