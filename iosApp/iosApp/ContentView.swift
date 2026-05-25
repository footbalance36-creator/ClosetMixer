import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        TabView {
            WardrobeView()
                .tabItem {
                    Label("Garde-robe", systemImage: "tshirt")
                }
            OutfitView()
                .tabItem {
                    Label("Tenues", systemImage: "sparkles")
                }
            CalendarView()
                .tabItem {
                    Label("Calendrier", systemImage: "calendar")
                }
            VoyageView()
                .tabItem {
                    Label("Voyage", systemImage: "airplane")
                }
            StatsView()
                .tabItem {
                    Label("Stats", systemImage: "chart.bar")
                }
        }
    }
}

#Preview {
    ContentView()
}
