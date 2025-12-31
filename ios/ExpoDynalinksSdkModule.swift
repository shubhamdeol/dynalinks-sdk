import ExpoModulesCore
import DynalinksSDK

public class ExpoDynalinksSdkModule: Module {
    private var isDynalinksConfigured = false

    public func definition() -> ModuleDefinition {
        Name("ExpoDynalinksSdk")

        Events("onDeferredDeepLink")

        AsyncFunction("configureDynalinks") { (apiKey: String) -> Bool in
            return await self.configureAndCheckDeepLink(apiKey: apiKey)
        }
    }

    private func configureAndCheckDeepLink(apiKey: String) async -> Bool {
        if !self.isDynalinksConfigured {
            do {
                try Dynalinks.configure(clientAPIKey: apiKey)
                self.isDynalinksConfigured = true
                print("[Dynalinks] SDK configured successfully")
            } catch {
                print("[Dynalinks] Failed to configure SDK: \(error)")
                return false
            }
        }

        Task {
            do {
                let result = try await Dynalinks.checkForDeferredDeepLink()

                let jsonData = try JSONEncoder().encode(result)
                let jsonString = String(data: jsonData, encoding: .utf8) ?? "{\"matched\":false}"
                print("[Dynalinks] Deferred deep link result: \(jsonString)")

                if let eventData = try JSONSerialization.jsonObject(with: jsonData) as? [String: Any] {
                    await MainActor.run {
                        self.sendEvent("onDeferredDeepLink", eventData)
                    }
                } else {
                    await MainActor.run {
                        self.sendEvent("onDeferredDeepLink", ["matched": false])
                    }
                }
            } catch {
                print("[Dynalinks] Error checking deferred deep link: \(error)")
                await MainActor.run {
                    self.sendEvent("onDeferredDeepLink", ["matched": false])
                }
            }
        }

        return true
    }
}
