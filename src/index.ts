// Reexport the native module. On web, it will be resolved to ExpoDynalinksSdkModule.web.ts
// and on native platforms to ExpoDynalinksSdkModule.ts
export * from './ExpoDynalinksSdk.types';
import { DeferredDeepLinkEvent } from './ExpoDynalinksSdk.types';
import ExpoDynalinksSdkModule from './ExpoDynalinksSdkModule';

export type Subscription = {
  remove: () => void;
};

export async function configureDynalinks(apiKey: string): Promise<boolean> {
  return await ExpoDynalinksSdkModule.configureDynalinks(apiKey);
}

export function addDeferredDeepLinkListener(
  listener: (event: DeferredDeepLinkEvent) => void,
): Subscription {
  return ExpoDynalinksSdkModule.addListener('onDeferredDeepLink', listener);
}
