import SwiftUI

struct StatsView: View {
    var body: some View {
        NavigationStack {
            List {
                Section("Résumé") {
                    LabeledContent("Total tenues", value: "0")
                    LabeledContent("Articles jamais portés", value: "0")
                }
                Section("Par catégorie") {
                    ForEach(["vetement", "chaussure", "bijou", "maquillage", "accessoire"], id: \.self) { cat in
                        LabeledContent(cat, value: "0")
                    }
                }
            }
            .navigationTitle("Statistiques")
        }
    }
}

#Preview { StatsView() }
