package cbsa.device.injection;


import javax.inject.Singleton;

import cbsa.device.service.DeviceServiceNative;
import dagger.Component;

@Singleton
@Component(modules = {DeviceServiceModule.class,
        BarcodeServiceV2Module.class})
public interface DeviceServiceComponent {
    void inject(DeviceServiceNative serviceNative);
}
