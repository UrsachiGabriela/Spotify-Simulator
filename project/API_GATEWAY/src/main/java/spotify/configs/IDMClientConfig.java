package spotify.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import spotify.clients.IDMClient;

@Configuration
public class IDMClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("com.spotify.idmclient.wsdl");
        return marshaller;
    }


    @Bean
    public IDMClient idmClient(Jaxb2Marshaller marshaller){
        IDMClient client = new IDMClient();
        client.setDefaultUri("http://localhost:8000");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
