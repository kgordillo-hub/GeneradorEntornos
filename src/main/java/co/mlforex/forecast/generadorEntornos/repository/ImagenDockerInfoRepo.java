package co.mlforex.forecast.generadorEntornos.repository;

import co.mlforex.forecast.generadorEntornos.model.TransaccionInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface ImagenDockerInfoRepo extends CrudRepository<TransaccionInfo, String> {

    Optional<TransaccionInfo> findById(String id);

    TransaccionInfo save(TransaccionInfo transaccionInfo);
}
