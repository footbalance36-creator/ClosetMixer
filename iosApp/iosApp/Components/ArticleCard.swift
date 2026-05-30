import SwiftUI

struct ArticleCardView: View {
    let photoPath: String
    let sousCategorie: String
    let couleur: String?
    let isFavori: Bool
    let onFavoriteToggle: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            ZStack(alignment: .topTrailing) {
                AsyncImage(url: URL(string: photoPath)) { image in
                    image.resizable().scaledToFill()
                } placeholder: {
                    Color(.systemGray5)
                }
                .frame(height: 140)
                .clipped()
                .clipShape(RoundedRectangle(cornerRadius: 8))

                Button(action: onFavoriteToggle) {
                    Image(systemName: isFavori ? "heart.fill" : "heart")
                        .foregroundStyle(isFavori ? .red : .white)
                        .padding(8)
                }
            }

            Text(sousCategorie)
                .font(.caption)
                .fontWeight(.medium)

            if let couleur {
                Text(couleur)
                    .font(.caption2)
                    .foregroundStyle(.secondary)
            }
        }
    }
}
