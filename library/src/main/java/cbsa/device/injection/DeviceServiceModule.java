package cbsa.device.injection;


import javax.inject.Singleton;

import cbsa.device.service.DeviceServiceNative;
import cbsa.device.presenter.DeviceServicePresenter;
import cbsa.device.presenter.DeviceServicePresenterImpl;
import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class DeviceServiceModule {

    private final DeviceServiceNative service;

    public DeviceServiceModule(DeviceServiceNative service) {
        this.service = service;
    }

    @Provides
    @Singleton
    DeviceServiceNative provideService() {
        return service;
    }

    @Provides
    @Singleton
    DeviceServicePresenter providePresenter(DeviceServicePresenterImpl presenter) {
        return presenter;
    }
}
