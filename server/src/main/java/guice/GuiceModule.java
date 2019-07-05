package guice;

import client.ClientProcessor;
import client.impl.ClientProcessorImpl;
import com.google.inject.AbstractModule;
import server.ServerServices;
import server.impl.ServerServicesImpl;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServerServices.class).to(ServerServicesImpl.class).asEagerSingleton();
        bind(ClientProcessor.class).to(ClientProcessorImpl.class);
    }
}
