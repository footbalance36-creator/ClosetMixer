package com.closetmixer.domain.model

enum class ArticleCategory(val key: String) {
    VETEMENT("vetement"),
    CHAUSSURE("chaussure"),
    BIJOU("bijou"),
    MAQUILLAGE("maquillage"),
    COUVRE_CHEF("couvre_chef"),
    ACCESSOIRE("accessoire"),
    SKINCARE("skincare")
}

enum class ArticleSubCategory(val category: ArticleCategory, val key: String) {
    HAUT(ArticleCategory.VETEMENT, "haut"),
    CHEMISE(ArticleCategory.VETEMENT, "chemise"),
    PULL(ArticleCategory.VETEMENT, "pull"),
    PANTALON(ArticleCategory.VETEMENT, "pantalon"),
    JUPE(ArticleCategory.VETEMENT, "jupe"),
    SHORT(ArticleCategory.VETEMENT, "short"),
    ROBE(ArticleCategory.VETEMENT, "robe"),
    VESTE(ArticleCategory.VETEMENT, "veste"),
    MANTEAU(ArticleCategory.VETEMENT, "manteau"),
    ABAYA(ArticleCategory.VETEMENT, "abaya"),
    KAFTAN(ArticleCategory.VETEMENT, "kaftan"),
    KIMONO(ArticleCategory.VETEMENT, "kimono"),
    HANBOK(ArticleCategory.VETEMENT, "hanbok"),
    SARI(ArticleCategory.VETEMENT, "sari"),

    TALON(ArticleCategory.CHAUSSURE, "talon"),
    BASKET(ArticleCategory.CHAUSSURE, "basket"),
    SANDALE(ArticleCategory.CHAUSSURE, "sandale"),
    BOTTE(ArticleCategory.CHAUSSURE, "botte"),
    BABOUCHE(ArticleCategory.CHAUSSURE, "babouche"),

    COLLIER(ArticleCategory.BIJOU, "collier"),
    BAGUE(ArticleCategory.BIJOU, "bague"),
    BRACELET(ArticleCategory.BIJOU, "bracelet"),
    BOUCLE_OREILLE(ArticleCategory.BIJOU, "boucle_oreille"),
    MONTRE(ArticleCategory.BIJOU, "montre"),

    ROUGE_LEVRES(ArticleCategory.MAQUILLAGE, "rouge_levres"),
    FOND_DE_TEINT(ArticleCategory.MAQUILLAGE, "fond_de_teint"),
    PALETTE_YEUX(ArticleCategory.MAQUILLAGE, "palette_yeux"),
    MASCARA(ArticleCategory.MAQUILLAGE, "mascara"),
    PARFUM(ArticleCategory.MAQUILLAGE, "parfum"),

    HIJAB(ArticleCategory.COUVRE_CHEF, "hijab"),
    TURBAN(ArticleCategory.COUVRE_CHEF, "turban"),
    VOILE(ArticleCategory.COUVRE_CHEF, "voile"),
    CHAPEAU(ArticleCategory.COUVRE_CHEF, "chapeau"),
    CASQUETTE(ArticleCategory.COUVRE_CHEF, "casquette"),
    BONNET(ArticleCategory.COUVRE_CHEF, "bonnet"),
    KANZASHI(ArticleCategory.COUVRE_CHEF, "kanzashi"),

    SAC(ArticleCategory.ACCESSOIRE, "sac"),
    CEINTURE(ArticleCategory.ACCESSOIRE, "ceinture"),
    LUNETTES(ArticleCategory.ACCESSOIRE, "lunettes"),
    ECHARPE(ArticleCategory.ACCESSOIRE, "echarpe"),
    EVENTAIL(ArticleCategory.ACCESSOIRE, "eventail"),

    SERUM(ArticleCategory.SKINCARE, "serum"),
    CREME(ArticleCategory.SKINCARE, "creme"),
    CUSHION(ArticleCategory.SKINCARE, "cushion"),
    MASQUE(ArticleCategory.SKINCARE, "masque")
}

enum class Metal(val key: String) {
    OR("or"), ARGENT("argent"), ROSE("rose"), FANTAISIE("fantaisie"), AUCUN("aucun")
}

enum class Gender(val key: String, val label: String, val emoji: String) {
    FEMME("femme", "Femme", "♀"),
    HOMME("homme", "Homme", "♂"),
    AUTRE("autre", "Autre", "⚧")
}

enum class AppLanguage(val code: String, val isRTL: Boolean, val nativeName: String) {
    FRENCH("fr", false, "Français"),
    ENGLISH("en", false, "English"),
    ARABIC("ar", true, "العربية"),
    TURKISH("tr", false, "Türkçe"),
    INDONESIAN("id", false, "Indonesia"),
    SPANISH("es", false, "Español"),
    KOREAN("ko", false, "한국어"),
    JAPANESE("ja", false, "日本語")
}

enum class CulturalStyle(val key: String) {
    MODEST("modest"),
    K_FASHION("k_fashion"),
    J_FASHION("j_fashion"),
    TRADITIONAL("traditional"),
    NEUTRAL("neutral")
}
