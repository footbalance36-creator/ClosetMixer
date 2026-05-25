import SwiftUI

struct SettingsView: View {
    @State private var isDarkMode = false
    @State private var selectedLanguage = "Français"
    @State private var selectedStyle = "neutral"

    let languages = ["Français", "English", "العربية", "Türkçe", "Indonesia", "Español", "한국어", "日本語"]
    let styles = ["neutral", "modest", "k_fashion", "j_fashion", "traditional"]

    var body: some View {
        NavigationStack {
            Form {
                Section("Apparence") {
                    Toggle("Mode sombre", isOn: $isDarkMode)
                }
                Section("Localisation") {
                    Picker("Langue", selection: $selectedLanguage) {
                        ForEach(languages, id: \.self) { Text($0) }
                    }
                }
                Section("Style") {
                    Picker("Style culturel", selection: $selectedStyle) {
                        ForEach(styles, id: \.self) { Text($0) }
                    }
                }
            }
            .navigationTitle("Paramètres")
        }
    }
}

#Preview { SettingsView() }
