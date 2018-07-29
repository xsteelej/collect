package org.odk.collect.android.http.injection;

import org.odk.collect.android.http.CollectServerClient;
import org.odk.collect.android.http.OkHttpConnection;
import org.odk.collect.android.http.OpenRosaHttpInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HttpInterfaceModule {

    @Provides
    @Singleton
    public OpenRosaHttpInterface provideHttpInterface() {
        return new OkHttpConnection();
    }

    @Provides
    @Singleton
    public CollectServerClient provideCollectServerClient(OpenRosaHttpInterface httpInterface) {
        return new CollectServerClient(httpInterface);
    }

}
