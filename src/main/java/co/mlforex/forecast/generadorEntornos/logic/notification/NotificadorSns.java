package co.mlforex.forecast.generadorEntornos.logic.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

public class NotificadorSns {

    Logger logger = LoggerFactory.getLogger(NotificadorSns.class);

    public NotificadorSns(){

    }

    public void publishMessageSns(String message, String topicArn){
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        pubTopic(snsClient, message, topicArn);
        snsClient.close();
    }

    private void pubTopic(SnsClient snsClient, String message, String topicArn) {

        try {

            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            logger.info(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            logger.debug("Error en NotificadorSns:pubTopic", e.getMessage());
        }
    }
}
