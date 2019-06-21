package guice;

import com.google.inject.AbstractModule;
import controller.ChatFunctionalities;
import controller.LoginFunctionalities;
import controller.impl.ChatController;
import controller.impl.LoginController;
import network.InputStreamReader;
import network.ServerServices;
import network.impl.InputStreamReaderImpl;
import network.impl.ServerServicesImpl;
import view.ChatView;
import view.LoginView;
import view.impl.ChatViewImpl;
import view.impl.LoginViewImpl;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LoginView.class).to(LoginViewImpl.class).asEagerSingleton();
        bind(ChatView.class).to(ChatViewImpl.class).asEagerSingleton();
        bind(LoginFunctionalities.class).to(LoginController.class).asEagerSingleton();
        bind(ChatFunctionalities.class).to(ChatController.class).asEagerSingleton();
        bind(InputStreamReader.class).to(InputStreamReaderImpl.class).asEagerSingleton();
        bind(ServerServices.class).to(ServerServicesImpl.class).asEagerSingleton();
    }
}
