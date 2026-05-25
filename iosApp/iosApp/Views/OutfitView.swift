import SwiftUI

struct OutfitView: View {
    @State private var isGenerating = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                WeatherBannerView(temperature: nil, description: "Chargement…")

                Spacer()

                if isGenerating {
                    ProgressView()
                } else {
                    Text("Appuyez sur générer pour créer une tenue")
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                }

                Spacer()

                Button(action: {
                    isGenerating = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                        isGenerating = false
                    }
                }) {
                    Label("Générer une tenue", systemImage: "sparkles")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
                .padding(.horizontal)
            }
            .padding()
            .navigationTitle("Tenues")
        }
    }
}

#Preview { OutfitView() }
