import SwiftUI

struct WeatherBannerView: View {
    let temperature: Double?
    let description: String

    var body: some View {
        HStack {
            Text(weatherIcon)
                .font(.largeTitle)
            VStack(alignment: .leading) {
                if let temp = temperature {
                    Text("\(Int(temp))°C")
                        .font(.title2)
                        .fontWeight(.semibold)
                }
                Text(description)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            Spacer()
        }
        .padding()
        .background(Color(.systemGray6))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private var weatherIcon: String {
        guard let temp = temperature else { return "🌡️" }
        if temp < 5 { return "🥶" }
        if temp < 15 { return "🧥" }
        if temp < 25 { return "☀️" }
        return "🌞"
    }
}

#Preview {
    WeatherBannerView(temperature: 22.0, description: "Ciel dégagé")
        .padding()
}
