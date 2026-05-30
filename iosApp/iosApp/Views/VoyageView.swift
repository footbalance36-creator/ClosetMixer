import SwiftUI

struct VoyageView: View {
    var body: some View {
        NavigationStack {
            Text("Aucun voyage planifié")
                .foregroundStyle(.secondary)
                .navigationTitle("Voyage")
                .toolbar {
                    ToolbarItem(placement: .primaryAction) {
                        Button(action: {}) {
                            Image(systemName: "plus")
                        }
                    }
                }
        }
    }
}

#Preview { VoyageView() }
