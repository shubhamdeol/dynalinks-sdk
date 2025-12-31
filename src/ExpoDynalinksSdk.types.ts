export type DynalinksLinkData = {
  id: string;
  name?: string;
  path?: string;
  shortened_path?: string;
  url?: string;
  full_url?: string;
  deep_link_value?: string;
  ios_deferred_deep_linking_enabled?: boolean;
  android_fallback_url?: string;
  ios_fallback_url?: string;
  enable_forced_redirect?: boolean;
  social_title?: string;
  social_description?: string;
  social_image_url?: string;
  clicks?: number;
};

export type DeferredDeepLinkEvent = {
  matched: boolean;
  confidence?: 'high' | 'medium' | 'low';
  match_score?: number;
  link?: DynalinksLinkData;
};

export type ExpoDynalinksSdkModuleEvents = {
  onDeferredDeepLink: (event: DeferredDeepLinkEvent) => void;
};
