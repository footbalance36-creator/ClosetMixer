# Security — ClosetMixer

## Périmètre
Application mobile locale — pas de backend propriétaire, pas d'authentification utilisateur.
Seule API externe : Open-Meteo (open source, sans clé, HTTPS).

## Photos utilisateur
- Les images sélectionnées depuis la galerie sont **copiées** dans `context.filesDir/articles/`
- Le `Uri` original (temporaire, scoped storage) n'est jamais stocké en DB
- Seul le chemin interne (`/data/data/.../files/articles/<uuid>.jpg`) est persisté
- Les photos ne quittent jamais l'appareil

## Permissions déclarées
| Permission | Justification |
|-----------|--------------|
| `INTERNET` | Open-Meteo API (météo) |
| `READ_MEDIA_IMAGES` | Sélection photo galerie (Android 13+) |
| `CAMERA` | Réservé — non utilisé actuellement |
| `ACCESS_FINE_LOCATION` | Météo locale (géolocalisation) |
| `ACCESS_COARSE_LOCATION` | Fallback géolocalisation |

## Base de données
- SQLite local via SQLDelight — pas de synchronisation cloud
- Pas de données sensibles (pas de mot de passe, pas d'identifiant personnel)
- Fichier DB dans `context.filesDir` — protégé par sandbox Android

## Réseau
- HTTPS uniquement (Open-Meteo force TLS)
- Aucune clé API → aucune secret à protéger
- Ktor `LogLevel.NONE` en production (pas de log des réponses HTTP)

## Ce qu'il ne faut PAS faire
- Ne jamais stocker le `Uri` brut en base — il expire après redémarrage
- Ne jamais logger les chemins de fichiers utilisateur
- Ne jamais ajouter de dépendance analytics/trackers sans validation
- Ne jamais commiter de fichier `.env` ou de clé en clair

## Futur (si auth ajoutée)
- Utiliser Android Keystore pour les tokens
- Ne jamais stocker de token dans SharedPreferences non chiffré
- Préférer `EncryptedSharedPreferences` (Jetpack Security)
