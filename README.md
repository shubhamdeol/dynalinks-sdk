# expo-dynalinks-sdk

Expo module for integrating [Dynalinks](https://dynalinks.app) deep linking SDK on iOS and Android.

## Installation

```bash
npx expo install expo-dynalinks-sdk
```

## Configuration

### iOS

The iOS SDK is automatically linked via CocoaPods. No additional setup required.

### Android

Add JitPack repository to your project's `android/build.gradle` if not already present:

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

## API Reference

### Functions

#### `configureDynalinks(apiKey: string): Promise<boolean>`

Configures the Dynalinks SDK with your API key and checks for deferred deep links.

**Parameters:**
- `apiKey` - Your Dynalinks client API key from the [Dynalinks Console](https://console.dynalinks.app)

**Returns:**
- `Promise<boolean>` - `true` if configuration was successful, `false` otherwise

**Example:**
```typescript
import { configureDynalinks } from 'expo-dynalinks-sdk';

const success = await configureDynalinks('your-api-key');
if (success) {
  console.log('Dynalinks SDK configured successfully');
}
```

---

#### `addDeferredDeepLinkListener(listener: (event: DeferredDeepLinkEvent) => void): Subscription`

Adds a listener for deferred deep link events. This is triggered after `configureDynalinks()` completes checking for deferred deep links.

**Parameters:**
- `listener` - Callback function that receives the deferred deep link event

**Returns:**
- `Subscription` - Object with a `remove()` method to unsubscribe

**Example:**
```typescript
import { addDeferredDeepLinkListener } from 'expo-dynalinks-sdk';

const subscription = addDeferredDeepLinkListener((event) => {
  if (event.matched) {
    console.log('Deep link matched:', event.link?.deep_link_value);
    // Navigate to the appropriate screen
  }
});

// Clean up when done
subscription.remove();
```

---

### Types

#### `DeferredDeepLinkEvent`

```typescript
type DeferredDeepLinkEvent = {
  matched: boolean;                           // Whether a matching link was found
  confidence?: 'high' | 'medium' | 'low';     // Confidence level of the match
  match_score?: number;                       // Match score (0-100)
  link?: DynalinksLinkData;                   // Link data if matched
};
```

#### `DynalinksLinkData`

```typescript
type DynalinksLinkData = {
  id: string;                                 // Unique identifier for the link
  name?: string;                              // Link name
  path?: string;                              // Path component of the link
  shortened_path?: string;                    // Shortened path for the link
  url?: string;                               // Original URL the link points to
  full_url?: string;                          // Full Dynalinks URL
  deep_link_value?: string;                   // Deep link value for routing in app
  ios_deferred_deep_linking_enabled?: boolean;// iOS deferred deep linking enabled (iOS only)
  android_fallback_url?: string;              // Android fallback URL
  ios_fallback_url?: string;                  // iOS fallback URL
  enable_forced_redirect?: boolean;           // Whether forced redirect is enabled
  social_title?: string;                      // Social sharing title
  social_description?: string;                // Social sharing description
  social_image_url?: string;                  // Social sharing image URL
  clicks?: number;                            // Number of clicks
};
```

#### `Subscription`

```typescript
type Subscription = {
  remove: () => void;  // Call to unsubscribe from events
};
```

---

## Usage

### Basic Setup

```typescript
import { useEffect } from 'react';
import {
  configureDynalinks,
  addDeferredDeepLinkListener
} from 'expo-dynalinks-sdk';

function App() {
  useEffect(() => {
    // Set up the listener first
    const subscription = addDeferredDeepLinkListener((event) => {
      console.log('Deferred deep link event:', event);

      if (event.matched && event.link?.deep_link_value) {
        // Handle the deep link - navigate to appropriate screen
        handleDeepLink(event.link.deep_link_value);
      }
    });

    // Configure the SDK (this will trigger the listener when done)
    configureDynalinks('your-api-key').catch((error) => {
      console.error('Failed to configure Dynalinks:', error);
    });

    // Clean up on unmount
    return () => subscription.remove();
  }, []);

  return (
    // Your app content
  );
}
```

### With Navigation

```typescript
import { useEffect } from 'react';
import { useNavigation } from '@react-navigation/native';
import {
  configureDynalinks,
  addDeferredDeepLinkListener
} from 'expo-dynalinks-sdk';

function App() {
  const navigation = useNavigation();

  useEffect(() => {
    const subscription = addDeferredDeepLinkListener((event) => {
      if (event.matched && event.link?.deep_link_value) {
        const deepLinkValue = event.link.deep_link_value;

        // Parse deep_link_value and navigate accordingly
        if (deepLinkValue.startsWith('product/')) {
          const productId = deepLinkValue.replace('product/', '');
          navigation.navigate('ProductDetails', { productId });
        } else if (deepLinkValue.startsWith('invite/')) {
          const inviteCode = deepLinkValue.replace('invite/', '');
          navigation.navigate('AcceptInvite', { inviteCode });
        }
      }
    });

    configureDynalinks('your-api-key');

    return () => subscription.remove();
  }, [navigation]);

  return (
    // Your app content
  );
}
```

---

## How It Works

1. **First App Launch After Install**: When a user clicks a Dynalinks URL and installs your app, the SDK captures the referrer information.

2. **Deferred Deep Link Check**: When you call `configureDynalinks()`, the SDK:
   - Initializes with your API key
   - Checks for any deferred deep link from the install referrer
   - Sends the result via the `onDeferredDeepLink` event

3. **Handle the Event**: Your listener receives the event with match information and link data, allowing you to navigate users to the correct content.

---

## Platform Support

| Platform | Support |
|----------|---------|
| iOS      | 13.0+   |
| Android  | API 21+ |

---

## License

MIT
