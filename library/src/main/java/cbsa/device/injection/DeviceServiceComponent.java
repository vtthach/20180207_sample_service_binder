package cbsa.device.injection;


import javax.inject.Singleton;

import cbsa.device.service.DeviceServiceNative;
import dagger.Component;

@Singleton
@Component(modules = {DeviceServiceModule.class,
        BarcodeServiceModule.class})
public interface DeviceServiceComponent {
    void inject(DeviceServiceNative serviceNative);
}
