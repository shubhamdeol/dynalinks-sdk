import { NativeModule, requireNativeModule } from 'expo';

import { ExpoDynalinksSdkModuleEvents } from './ExpoDynalinksSdk.types';

declare class ExpoDynalinksSdkModule extends NativeModule<ExpoDynalinksSdkModuleEvents> {
  configureDynalinks(apiKey: string): Promise<boolean>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoDynalinksSdkModule>('ExpoDynalinksSdk');
