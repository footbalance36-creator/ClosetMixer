import SwiftUI

struct CalendarView: View {
    var body: some View {
        NavigationStack {
            Text("Calendrier — planification des tenues")
                .foregroundStyle(.secondary)
                .navigationTitle("Calendrier")
        }
    }
}

#Preview { CalendarView() }
