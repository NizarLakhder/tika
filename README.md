# IFT3913 – Tâche 2 : augmentation de la suite de tests d'Apache Tika

[![IFT3913 tache 2](https://github.com/NizarLakhder/tika/actions/workflows/tache2.yml/badge.svg)](https://github.com/NizarLakhder/tika/actions/workflows/tache2.yml)

| Nom complet | Identifiant GitHub |
|---|---|
| Lakhder Nizar | NizarLakhder |

- Dépôt : https://github.com/NizarLakhder/tika
- Cas d'étude : fork du cours [umontreal-diro/tika](https://github.com/umontreal-diro/tika), module `tika-core`
- Classe étudiée : [`org.apache.tika.io.EndianUtils`](tika-core/src/main/java/org/apache/tika/io/EndianUtils.java)

Ce fichier remplace le README d'Apache Tika, qui reste disponible dans le [dépôt d'origine](https://github.com/apache/tika#readme).

## Résumé

`EndianUtils` regroupe 31 méthodes statiques qui lisent des entiers dans un flux ou un tableau d'octets, en petit-boutiste (LE), gros-boutiste (BE) ou boutisme mixte (ME). Les tests d'origine n'exécutaient que 4 de ces méthodes.

| Suite de tests | Tests | Mutants tués (sur 206) | Score de mutation | Lignes couvertes |
|---|---|---|---|---|
| Origine (`EndianUtilsTest`) | 4 | 38 | 18 % | 31 / 121 |
| + générés par ChatUniTest, tels quels | 15 | 126 | 61 % | 85 / 121 |
| + générés dont l'oracle a été corrigé | 40 | 162 | 79 % | 113 / 121 |
| + écrits à la main | 51 | 204 | 99 % | 120 / 121 |

Les deux mutants restants sont équivalents ([section 6.5](#65-les-deux-mutants-équivalents)). ChatUniTest a été utilisé avec le modèle ouvert `granite-code:3b`, exécuté localement par Ollama. Sur 33 tests générés, 11 passaient tels quels et 22 avaient un oracle faux.

Le plan suit la grille d'évaluation, une section par critère.

## 1. Classes à tester

### 1.1 Démarche

Quatre classes utilitaires de `tika-core` qui possèdent déjà une classe de test ont été mesurées avant tout changement : couverture avec JaCoCo (déjà configuré dans `tika-parent`) et analyse de mutation avec PIT, en lançant pour chacune sa classe de test dédiée.

| Classe | Lignes couvertes | Mutants | Tués | Survivants | Non couverts | Score |
|---|---|---|---|---|---|---|
| `io.EndianUtils` | 31 / 121 (25,6 %) | 206 | 38 | 14 | 154 | 18 % |
| `io.FilenameUtils` | 153 / 175 (87,4 %) | 115 | 73 | 20 | 22 | 63 % |
| `utils.CharsetUtils` | 62 / 81 (76,5 %) | 25 | 20 | 0 | 5 | 80 % |
| `utils.RegexUtils` | 9 / 10 (90 %) | 4 | 4 | 0 | 0 | 100 % |

Vocabulaire utilisé dans tout le document :

- **tué** : au moins un test échoue sur le mutant ;
- **survivant** : la ligne mutée est exécutée, mais aucun test n'échoue (oracle trop faible) ;
- **non couvert** : aucun test n'exécute la ligne mutée (test manquant) ;
- **score de mutation** : mutants tués / mutants générés. Les mutants « vivants » sont les survivants et les non couverts.

### 1.2 Pourquoi `EndianUtils`

- **Elle n'est pas couverte à 100 %** : 31 lignes sur 121, 10 branches sur 28, 4 méthodes sur 32 (les 31 méthodes déclarées et le constructeur implicite). Aucun autre fichier de `tika-core` ne référence `EndianUtils`, donc cette mesure n'est pas sous-estimée par des tests indirects.
- **Elle a le plus de mutants vivants** : 168 sur 206 (14 survivants et 154 non couverts).
- **Ses oracles sont vérifiables à la main** : fonctions pures, des octets en entrée, un entier en sortie.

Les autres candidates ont été écartées : `RegexUtils` n'a aucun mutant vivant ; `CharsetUtils` n'a aucun survivant et ses 5 mutants non couverts sont sur des chemins qui exigent la bibliothèque ICU4J, absente de `tika-core` ; `FilenameUtils` a moins de mutants vivants (42), surtout dans des méthodes privées.

### 1.3 Détail par méthode, avec les tests d'origine

| Méthode (surcharges regroupées) | Mutants | Tués | Survivants | Non couverts |
|---|---|---|---|---|
| `readShortLE`, `readShortBE` | 2 | 0 | 0 | 2 |
| `readUShortLE`, `readUShortBE` | 12 | 0 | 0 | 12 |
| `readUIntLE` | 13 | 10 | 3 | 0 |
| `readUIntBE` | 13 | 9 | 4 | 0 |
| `readIntLE`, `readIntBE` | 24 | 0 | 0 | 24 |
| `readIntME` | 12 | 9 | 3 | 0 |
| `readLongLE`, `readLongBE` | 48 | 0 | 0 | 48 |
| `readUE7` | 14 | 10 | 4 | 0 |
| `getShortLE`, `getShortBE` | 4 | 0 | 0 | 4 |
| `getUShortLE`, `getUShortBE` | 14 | 0 | 0 | 14 |
| `getIntLE`, `getIntBE` | 32 | 0 | 0 | 32 |
| `getUIntLE`, `getUIntBE` | 6 | 0 | 0 | 6 |
| `getLongLE`, `ubyteToInt`, `getUByte` | 12 | 0 | 0 | 12 |
| **Total** | **206** | **38** | **14** | **154** |

Seules `readUIntLE`, `readUIntBE`, `readIntME` et `readUE7` sont testées, et elles ont toutes des survivants. Les 27 autres méthodes n'ont aucun test. La génération a donc porté sur les 31 méthodes.

L'analyse de mutation révèle aussi un défaut du test d'origine : `testReadUIntBE` vérifie le cas du flux trop court en appelant `readUIntLE` au lieu de `readUIntBE` ([EndianUtilsTest.java, ligne 68](tika-core/src/test/java/org/apache/tika/io/EndianUtilsTest.java#L68)), une erreur de copier-coller. Le chemin d'erreur de `readUIntBE` n'est jamais vérifié, d'où un survivant de plus que pour `readUIntLE`.

## 2. IA et test : ChatUniTest dans le pipeline Maven

### 2.1 Configuration

ChatUniTest est installé dans [`tika-core/pom.xml`](tika-core/pom.xml) : le plugin `chatunitest-maven-plugin` 2.1.1 et la dépendance `chatunitest-starter` 1.4.0.

```xml
<plugin>
  <groupId>io.github.zju-aces-ise</groupId>
  <artifactId>chatunitest-maven-plugin</artifactId>
  <version>2.1.1</version>
  <configuration>
    <apiKeys>ollama</apiKeys>
    <url>http://localhost:11434/v1/chat/completions</url>
    <model>code-llama</model>
    <tmpOutput>${project.build.directory}/chatunitest-info</tmpOutput>
    <thread>false</thread>
    <testNumber>3</testNumber>
    <maxRounds>1</maxRounds>
  </configuration>
</plugin>
```

```xml
<profile>
  <id>chatunitest</id>
  <dependencies>
    <dependency>
      <groupId>io.github.ZJU-ACES-ISE</groupId>
      <artifactId>chatunitest-starter</artifactId>
      <version>1.4.0</version>
      <type>pom</type>
      <scope>test</scope>
    </dependency>
  </dependencies>
</profile>
```

Génération : `mvn chatunitest:class -pl tika-core -Pchatunitest -DselectClass=EndianUtils`.

### 2.2 Écarts par rapport à la documentation de ChatUniTest

L'installation décrite par ChatUniTest ne fonctionne pas telle quelle sur Tika, qui utilise JUnit 6. Chaque écart ci-dessous répond à un problème observé.

| Problème observé | Cause | Solution retenue |
|---|---|---|
| Avec le starter en dépendance normale, Surefire affiche `Tests run: 0` et `BUILD SUCCESS` : toute la suite de `tika-core` est ignorée en silence. | Le starter amène `junit-platform-runner` ; en sa présence, Surefire choisit son lanceur JUnit 4, qui ne voit aucun test Jupiter. | Starter isolé dans le profil `chatunitest`, activé seulement pour générer, en portée `test`. Hors profil, Surefire reprend le lanceur JUnit Platform. |
| `<model>qwen2.5-coder:7b</model>` est refusé. | ChatUniTest 2.1.1 n'accepte que 7 noms de modèles codés en dur (`Model.fromString`). Pour un modèle local, seuls `code-llama` et `codeqwen:v1.5-chat` passent. | Alias Ollama `code-llama` qui désigne le modèle réel (section 2.3). |
| Sans `<url>`, les requêtes partent vers `api.gptsapi.net`. | C'est la valeur par défaut du plugin, et non l'API d'OpenAI annoncée par sa documentation. | `<url>` fixé sur le serveur Ollama local : aucun code ne quitte la machine. |
| Les tours de réparation ne corrigent rien. | Sur 6 tours de réparation observés avec deux modèles, aucun n'a produit un test qui compile. Une nouvelle génération, elle, réussit souvent. | `maxRounds=1` (pas de réparation) et `testNumber=3` (jusqu'à trois générations par méthode). |
| Requêtes simultanées inutiles. | Un modèle local traite une requête à la fois. | `thread=false`. |

### 2.3 Choix du modèle

L'énoncé demande un modèle ouvert exécuté localement. La machine utilisée a une carte graphique de 4 Go (GTX 1650, 12 Go de mémoire vive), ce qui limite la taille du modèle. Quatre modèles ont été comparés en leur envoyant le prompt exact de ChatUniTest (repris de son historique) pour deux méthodes, `readIntLE` et `getIntLE`, deux fois chacune, avec les paramètres de ChatUniTest (température 0,5, 1024 tokens au plus). Chaque réponse a ensuite été compilée et exécutée.

| Modèle | Licence | Réponses qui compilent | Tests qui passent | Durée par réponse |
|---|---|---|---|---|
| `granite-code:3b` | Apache 2.0 | 3 sur 4 | 3 sur 3 | 69 à 296 s |
| `qwen2.5-coder:3b` | Qwen Research | 0 sur 4 | – | 34 à 51 s |
| `qwen2.5:3b` | Qwen Research | 0 sur 4 | – | 8 à 41 s |
| `qwen2.5-coder:7b` (un seul essai) | Apache 2.0 | 0 sur 1 | – | 380 s |

`granite-code:3b` a été retenu : c'est le seul dont le code compile, et sa licence est libre. `qwen2.5-coder:7b`, prévu au départ, ne tient pas dans 4 Go : sa réponse dépasse le délai de 300 s codé dans ChatUniTest. L'échantillon est petit (4 réponses par modèle) ; il a servi à écarter les modèles inutilisables, pas à les classer finement.

Pour accélérer `granite-code:3b`, son contexte a été ramené de 16 384 à 3 072 tokens (pour cette classe, un échange complet avec ChatUniTest, prompt et réponse, tient en moins de 2 400 tokens), ce qui le fait tenir à 85 % sur la carte graphique :

```
ollama pull granite-code:3b
# Modelfile :  FROM granite-code:3b
#              PARAMETER num_ctx 3072
ollama create code-llama -f Modelfile
```

Environnement : Windows 10, Ollama 0.40.2, JDK 21 (Tika compile en `release 17`), Maven 3.9.12.

## 3. Tests générés

La génération a duré 1 h 10 pour les 31 méthodes. ChatUniTest traite une méthode à la fois : il envoie le code de la méthode au modèle, compile la réponse, l'exécute, puis l'exporte.

| Mesure | Résultat |
|---|---|
| Générations demandées au modèle | 47 |
| Rejetées à la compilation | 16 |
| Sans code exploitable dans la réponse | 2 |
| Fichiers de test exportés | 29, un par méthode |
| Méthodes sans aucun test | 2 (`readShortLE`, `readIntBE`) |
| Tests exportés | 33 |

Détail par méthode (le numéro est celui que ChatUniTest donne à la méthode, repris dans le nom du fichier `EndianUtils_<méthode>_<numéro>_<essai>_Test.java`) :

| N° | Méthode | Générations | Rejets | Tests exportés | Passent | Échouent |
|---|---|---|---|---|---|---|
| 0 | `readShortLE(InputStream)` | 3 | 3 | 0 | – | – |
| 1 | `readShortBE(InputStream)` | 3 | 2 | 1 | 0 | 1 |
| 2 | `readUShortLE(InputStream)` | 1 | 0 | 1 | 0 | 1 |
| 3 | `readUShortBE(InputStream)` | 2 | 1 | 1 | 1 | 0 |
| 4 | `readUIntLE(InputStream)` | 1 | 0 | 1 | 0 | 1 |
| 5 | `readUIntBE(InputStream)` | 1 | 0 | 1 | 1 | 0 |
| 6 | `readIntLE(InputStream)` | 1 | 0 | 1 | 1 | 0 |
| 7 | `readIntBE(InputStream)` | 3 | 3 | 0 | – | – |
| 8 | `readIntME(InputStream)` | 2 | 0 | 2 | 0 | 2 |
| 9 | `readLongLE(InputStream)` | 3 | 2 | 1 | 1 | 0 |
| 10 | `readLongBE(InputStream)` | 3 | 2 | 1 | 1 | 0 |
| 11 | `readUE7(InputStream)` | 3 | 2 | 1 | 0 | 1 |
| 12 | `getShortLE(byte[])` | 1 | 0 | 3 | 0 | 3 |
| 13 | `getShortLE(byte[], int)` | 1 | 0 | 1 | 1 | 0 |
| 14 | `getUShortLE(byte[])` | 2 | 0 | 1 | 0 | 1 |
| 15 | `getUShortLE(byte[], int)` | 1 | 0 | 1 | 0 | 1 |
| 16 | `getShortBE(byte[])` | 1 | 0 | 1 | 0 | 1 |
| 17 | `getShortBE(byte[], int)` | 1 | 0 | 2 | 0 | 2 |
| 18 | `getUShortBE(byte[])` | 1 | 0 | 1 | 0 | 1 |
| 19 | `getUShortBE(byte[], int)` | 1 | 0 | 1 | 1 | 0 |
| 20 | `getIntLE(byte[])` | 1 | 0 | 1 | 1 | 0 |
| 21 | `getIntLE(byte[], int)` | 1 | 0 | 1 | 0 | 1 |
| 22 | `getIntBE(byte[])` | 1 | 0 | 1 | 0 | 1 |
| 23 | `getIntBE(byte[], int)` | 1 | 0 | 1 | 1 | 0 |
| 24 | `getUIntLE(byte[])` | 2 | 1 | 1 | 0 | 1 |
| 25 | `getUIntLE(byte[], int)` | 1 | 0 | 1 | 0 | 1 |
| 26 | `getUIntBE(byte[])` | 1 | 0 | 1 | 1 | 0 |
| 27 | `getUIntBE(byte[], int)` | 1 | 0 | 1 | 0 | 1 |
| 28 | `getLongLE(byte[], int)` | 1 | 0 | 1 | 0 | 1 |
| 29 | `ubyteToInt(byte)` | 1 | 0 | 1 | 1 | 0 |
| 30 | `getUByte(byte[], int)` | 1 | 0 | 1 | 0 | 1 |
| | **Total** | **47** | **16** | **33** | **11** | **22** |

Pour les méthodes 8 et 14, la première réponse du modèle ne contenait pas de code que ChatUniTest a su extraire.

## 4. Documentation des tests générés

### 4.1 Où sont les tests

| Emplacement | Contenu |
|---|---|
| [`tika-core/chatunitest-tests/org/apache/tika/io/`](tika-core/chatunitest-tests/org/apache/tika/io/) | Sortie brute de ChatUniTest, non modifiée : 29 fichiers de test et `EndianUtils_Suite.java`. La documentation de l'outil annonce un dossier `chatunitest/` ; le nom réel est `chatunitest-tests/`. |
| [`tika-core/chatunitest-tests/rejected/`](tika-core/chatunitest-tests/rejected/) | Les 16 générations rejetées, avec leur erreur de compilation, copiées depuis le dossier temporaire de ChatUniTest. |
| [`EndianUtilsGeneratedTest.java`](tika-core/src/test/java/org/apache/tika/io/EndianUtilsGeneratedTest.java) | Les 11 tests qui passent tels que générés, corps recopiés à l'identique. |
| [`EndianUtilsGeneratedCorrectedTest.java`](tika-core/src/test/java/org/apache/tika/io/EndianUtilsGeneratedCorrectedTest.java) | Les 22 tests dont l'oracle a été corrigé et 3 tests récupérés de générations rejetées. Un commentaire au-dessus de chaque test cite ce que ChatUniTest avait écrit. |

Dans les deux classes intégrées, chaque méthode porte le nom de son fichier d'origine, par exemple `getIntLE_21_0_testGetIntLE` pour `EndianUtils_getIntLE_21_0_Test#testGetIntLE`.

### 4.2 Compilent-ils et s'exécutent-ils sans intervention ?

Non, à trois niveaux.

- **Compilation dans ChatUniTest** : 29 générations sur 47 compilent. 13 des 16 rejets viennent de l'exception `EndianUtils.BufferUnderrunException` : le test ne la déclare pas (8 cas) ou la nomme sans la préfixer par `EndianUtils.` (5 cas). Les 3 autres sont des littéraux invalides (`0xFF` affecté à un `byte` sans conversion, entier trop grand).
- **Compilation dans le build de Tika** : aucun fichier exporté ne compile tel quel. ChatUniTest ajoute à chaque fichier des imports Mockito, que `tika-core` n'a pas, et des imports en étoile, que le Checkstyle de Tika interdit.
- **Exécution** : sur les 33 tests exportés, 11 passent et 22 échouent, tous sur une assertion. ChatUniTest annonce pourtant « compile and execute successfully » pour chacun : il n'écarte un test que s'il ne compile pas ou s'il lève une exception, pas si son assertion échoue.

Le fichier `EndianUtils_Suite.java` est inutilisable : il repose sur le lanceur JUnit 4 (`@RunWith(JUnitPlatform.class)`), supprimé dans JUnit 6.

### 4.3 Combien de corrections

| Intervention | Quantité | Modifie la logique du test |
|---|---|---|
| Fusion des 29 fichiers en 2 classes, en-tête de licence, imports explicites | 29 fichiers | non |
| Valeur attendue corrigée, données conservées | 19 tests, 19 lignes | oui |
| Exception inexistante remplacée par le comportement réel, données conservées | 2 tests, 3 lignes | oui |
| Test réécrit en partie, données comprises (`testReadIntMEWithUnderflow`) | 1 test, 3 lignes | oui |
| Génération rejetée rendue compilable | 2 fichiers (3 tests), 2 lignes | non |
| Fichier écarté (`EndianUtils_Suite.java`) | 1 | – |

Pour les deux méthodes restées sans test, une génération rejetée a été récupérée avec une correction d'une ligne :

- `readShortLE` : `throws IOException` complété en `throws IOException, TikaException`.
- `readIntBE` : `catch (BufferUnderrunException e)` qualifié en `catch (EndianUtils.BufferUnderrunException e)`.

### 4.4 Les 22 oracles faux

Dans 21 cas sur 22, les données d'entrée ont été conservées et seule la vérification a été corrigée. L'exception est `testReadIntMEWithUnderflow`, dont les données ont dû changer ; elle est traitée à part sous le tableau. La valeur correcte se déduit de la définition de la méthode : en LE le premier octet est le moins significatif, en BE le plus significatif, et `readIntME` permute les octets de chaque paire.

| Test généré | Octets | Oracle généré | Valeur correcte | Nature de l'erreur |
|---|---|---|---|---|
| `readShortBE_1_2` | `FF 00` | `0xFFFF` | `(short) 0xFF00` | calcul |
| `readUShortLE_2_0` | `01 02` | 258 (`0x0102`) | 513 (`0x0201`) | boutisme |
| `readUIntLE_4_0`, 2ᵉ assertion | `01` à `08` | `0x08070605` | `0x04030201` | octets lus |
| `readIntME_8_1#testReadIntME` | `01 02 03 04` | `0x04030201` | `0x02010403` | boutisme |
| `readIntME_8_1#testReadIntMEWithUnderflow` | 4 octets générés, 3 après correction | `IOException` et un message inventé | `BufferUnderrunException`, et seulement avec 3 octets | comportement inventé |
| `readUE7_11_2` | `01` à `08` | `0x0102030405060708` | 1 | format mal compris |
| `getShortLE_12_0#testGetShortLE` | `01 02` | `0x0102` | `0x0201` | boutisme |
| `getShortLE_12_0#testGetShortLEWithOffset` | `01 02 03 04`, position 2 | `0x0201` | `0x0403` | position ignorée |
| `getShortLE_12_0#testGetShortLEWithInvalidData` | `01 02 03` | `IOException` attendue | `0x0201`, sans exception | comportement inventé |
| `getUShortLE_14_1` | `01 02` | 258 | 513 | boutisme |
| `getUShortLE_15_0` | `01 02 03 04` | 257 | 513 | calcul |
| `getShortBE_16_0` | `FF 00` | `(short) 0xFFFF` | `(short) 0xFF00` | calcul |
| `getShortBE_17_0#testGetShortBE` | `FF AB` | `(short) 0xFFFFABFF` | `(short) 0xFFAB` | boutisme |
| `getShortBE_17_0#testInvalidData` | tableau d'un octet | `IllegalArgumentException` et un message inventé | `ArrayIndexOutOfBoundsException` | comportement inventé |
| `getUShortBE_18_0` | `FF 00` | `0x00FF` | `0xFF00` | boutisme |
| `getIntLE_21_0` | `01 02 03 04` | 16909060 (`0x01020304`) | 67305985 (`0x04030201`) | boutisme |
| `getIntBE_22_0` | `FF` puis sept `00` | `0xFFFFFFFF` | `0xFF000000` | calcul |
| `getUIntLE_24_1` | `01` à `08` | `0x01020304` | `0x04030201` | boutisme |
| `getUIntLE_25_0` | `01` à `08` | `0x0807060504030201` | `0x04030201` | largeur |
| `getUIntBE_27_0` | `01` à `08` | `0x0102030405060708` | `0x01020304` | largeur |
| `getLongLE_28_0` | `01` à `08` | `0x0102030405060708` | `0x0807060504030201` | boutisme |
| `getUByte_30_0` | `01` à `08`, position 2 | `0x0302` | `0x03` | largeur |

Répartition : 9 confusions de boutisme, 4 erreurs de calcul, 3 erreurs de largeur (une valeur sur 64 bits attendue d'une lecture sur 32, par exemple), 2 erreurs sur les octets lus, 1 format mal compris, 3 comportements inventés. Dans les 22 cas, c'est le test qui avait tort et non `EndianUtils`.

**Le cas particulier de `testReadIntMEWithUnderflow`.** C'est le seul test dont les données ont été modifiées. Tel que généré, il fournit 4 octets à une lecture de 4 octets : le dépassement annoncé par son nom ne peut pas se produire. Deux corrections étaient possibles :

- garder les 4 octets et vérifier la valeur renvoyée, ce qui aurait dupliqué `testReadIntME` ;
- réaliser le scénario que le test annonce.

La seconde a été retenue. Le flux passe à 3 octets, l'exception attendue devient `EndianUtils.BufferUnderrunException` (qui n'est pas une `IOException`), et le message vérifié devient celui que le code produit. Trois des quatre instructions du test changent : c'est une réécriture partielle plus qu'une correction d'oracle, et ce test est compté à part dans le tableau de la section 4.3.

### 4.5 Oracles de l'IA et oracles écrits à la main

La comparaison porte sur `EndianUtilsTest` (4 tests écrits par les développeurs de Tika) et sur les 33 tests générés.

| Critère | Tests d'origine | Tests générés |
|---|---|---|
| **Pertinence** | Chaque valeur attendue est juste. Un test s'appuie sur un exemple documenté de boutisme mixte. | 22 oracles sur 33 sont faux. Le modèle écrit parfois la bonne formule en commentaire et une mauvaise valeur dessous. |
| **Spécificité** | Plusieurs cas par méthode, dont des valeurs à bit de signe (`F0 FF FF FF` donne 4294967280) et un flux trop court. | Un seul cas par méthode en général, toujours le cas nominal. Aucun cas limite réel : ni octets nuls, ni flux trop court correct. |
| **Choix des données** | Octets asymétriques, qui distinguent LE de BE. | Souvent `01 02 03 04`, bon choix car tous les octets diffèrent. Mais 3 des 11 tests qui passent utilisent des octets tous à `FF` : la valeur est la même dans tous les ordres. |
| **Vérifications triviales** | Le cas d'erreur de `testReadUIntBE` vérifie la mauvaise méthode (copier-coller). | Plusieurs, détaillées ci-dessous. |

Vérifications triviales ou trompeuses relevées dans les tests générés :

- **`getIntBE_23_0`** passe par coïncidence. Il compare `(long) result` à `0xFFFFFFFFFFFFFFFFL`, une constante de 64 bits pour une lecture de 32 bits ; les deux valent -1 parce que tous les octets sont à `FF`.
- **`readIntBE_7_1#testReadIntBEWithIOException`** n'a pas d'oracle : il appelle la méthode dans un `try` et ignore l'exception dans le `catch`, sans `fail()`. Il passe que l'exception soit levée ou non.
- **`readIntBE_7_2`**, génération rejetée, vérifie son résultat avec le mot-clé `assert` de Java au lieu d'une assertion JUnit. Cette vérification n'est évaluée que si les assertions sont activées dans la JVM (option `-ea`) : Java les désactive par défaut, alors que Surefire les active par défaut. L'oracle dépend donc du lanceur, et le même test peut passer sans rien vérifier.
- **`ubyteToInt_29_0`** est à l'opposé le meilleur test généré : il vérifie les 256 valeurs possibles.

En contrepartie, les tests d'origine restent meilleurs là où ils existent : ils tuent 5 mutants qu'aucun test généré ne tue, dont 4 dans `readUE7`, que le test généré n'exerce qu'avec un nombre d'un seul octet.

### 4.6 Critique de l'outil

- **Le prompt manque de contexte.** Il contient la méthode visée et l'en-tête de la classe, mais pas les classes imbriquées. Le modèle ne peut pas savoir que `BufferUnderrunException` appartient à `EndianUtils`, d'où 13 rejets sur 16.
- **Un test dont l'assertion échoue est exporté comme réussi**, ce qui laisse le tri à l'utilisateur.
- **Les avertissements du compilateur sont transmis au modèle comme des erreurs** lors d'une réparation, ce qui noie la vraie erreur.
- **L'outil suppose JUnit 5 et Mockito** : imports ajoutés d'office, suite JUnit 4, starter incompatible avec un projet en JUnit 6.
- **Des valeurs sont codées en dur** : liste des modèles, délai de 5 minutes par requête.

## 5. Mutation

### 5.1 Installation de PIT

PIT est ajouté dans [`tika-core/pom.xml`](tika-core/pom.xml) : `pitest-maven` 1.30.0 avec `pitest-junit5-plugin` 1.2.3. Il n'est lié à aucune phase et ne s'exécute que sur demande. Les classes et tests ciblés sont des propriétés du POM, pour ne jamais muter tout `tika-core` par accident.

```xml
<targetClasses>org.apache.tika.io.EndianUtils</targetClasses>
<targetTests>org.apache.tika.io.EndianUtils*Test</targetTests>
```

PIT fonctionne avec JUnit 6.1.3 : il ajoute lui-même le lanceur `junit-platform-launcher` de la même version. Les mutateurs sont ceux par défaut.

### 5.2 Résultats

Commande : `mvn test-compile pitest:mutationCoverage -pl tika-core -DtargetTests=<classes de test>`. PIT génère 206 mutants pour `EndianUtils`.

| Tests fournis à PIT | Tests | Tués | Survivants | Non couverts | Score | Force des tests |
|---|---|---|---|---|---|---|
| Origine | 4 | 38 | 14 | 154 | 18 % | 73 % |
| Générés tels quels, seuls | 11 | 97 | 31 | 78 | 47 % | 76 % |
| Origine + générés tels quels | 15 | 126 | 41 | 39 | 61 % | 75 % |
| Générés tels quels et corrigés, seuls | 36 | 157 | 47 | 2 | 76 % | 77 % |
| Origine + générés tels quels et corrigés | 40 | 162 | 44 | 0 | 79 % | 79 % |
| Origine + générés + écrits à la main | 51 | 204 | 2 | 0 | 99 % | 99 % |

La force des tests est la part de mutants tués parmi ceux qui sont couverts. PIT exige une suite entièrement verte : les 22 tests générés en échec ne peuvent donc être mesurés qu'une fois corrigés. La ligne « générés tels quels » ne contient que les 11 tests qui passaient sans retouche de leur logique.

**Les tests générés ne détectent pas tous les mutants.** Même corrigés et ajoutés aux tests d'origine, ils en laissent 44 vivants.

### 5.3 Mutants tués par méthode

| Méthode (surcharges regroupées) | Mutants | Origine | + générés tels quels | + corrigés | + manuels |
|---|---|---|---|---|---|
| `readShortLE` | 1 | 0 | 0 | 1 | 1 |
| `readShortBE` | 1 | 0 | 0 | 1 | 1 |
| `readUShortLE` | 6 | 0 | 0 | 4 | 6 |
| `readUShortBE` | 6 | 0 | 4 | 4 | 6 |
| `readUIntLE` | 13 | 10 | 10 | 10 | 13 |
| `readUIntBE` | 13 | 9 | 9 | 9 | 13 |
| `readIntLE` | 12 | 0 | 8 | 8 | 12 |
| `readIntBE` | 12 | 0 | 0 | 8 | 12 |
| `readIntME` | 12 | 9 | 9 | 9 | 12 |
| `readLongLE` | 24 | 0 | 16 | 16 | 24 |
| `readLongBE` | 24 | 0 | 16 | 16 | 24 |
| `readUE7` | 14 | 10 | 10 | 10 | 14 |
| `getShortLE` | 2 | 0 | 1 | 2 | 2 |
| `getUShortLE` | 7 | 0 | 6 | 7 | 7 |
| `getShortBE` | 2 | 0 | 0 | 2 | 2 |
| `getUShortBE` | 7 | 0 | 6 | 7 | 7 |
| `getIntLE` | 16 | 0 | 15 | 15 | 15 |
| `getIntBE` | 16 | 0 | 11 | 15 | 15 |
| `getUIntLE` | 3 | 0 | 0 | 3 | 3 |
| `getUIntBE` | 3 | 0 | 3 | 3 | 3 |
| `getLongLE` | 8 | 0 | 0 | 8 | 8 |
| `ubyteToInt` | 2 | 0 | 2 | 2 | 2 |
| `getUByte` | 2 | 0 | 0 | 2 | 2 |
| **Total** | **206** | **38** | **126** | **162** | **204** |

## 6. Documentation des mutants

Les données de cette section viennent du rapport XML de PIT, produit avec l'option `fullMutationMatrix`, qui indique pour chaque mutant tous les tests qui le tuent.

### 6.1 Mutants tués par type de mutation

| Mutateur de PIT | Exemple dans `EndianUtils` | Mutants | Origine | + générés tels quels | + corrigés | + manuels |
|---|---|---|---|---|---|---|
| Math | `<<` devient `>>`, `+` devient `-`, `\|` devient `&` | 139 | 27 | 94 | 110 | 139 |
| PrimitiveReturns | la valeur renvoyée devient 0 | 31 | 4 | 17 | 31 | 31 |
| NegateConditionals | `< 0` devient `>= 0` | 14 | 7 | 11 | 14 | 14 |
| ConditionalsBoundary | `< 0` devient `<= 0` | 13 | 0 | 0 | 1 | 13 |
| Increments | `i++` devient `i--` | 9 | 0 | 4 | 6 | 7 |
| **Total** | | **206** | **38** | **126** | **162** | **204** |

### 6.2 Les mutants détectés par les tests générés, et pourquoi

Les tests générés font passer le nombre de mutants tués de 38 à 162 : 88 de plus avec les tests tels quels, puis 36 avec les tests corrigés.

- **Décalages, additions et masques (Math, 83 mutants de plus).** Les méthodes calculent `(b3 << 24) + (b2 << 16) + (b1 << 8) + b0`. Un test qui fournit des octets tous différents, comme `01 02 03 04`, et attend la valeur exacte (`0x04030201`) place chaque octet à une position unique du résultat. Tout décalage inversé, toute addition changée en soustraction et tout masque `& 0xFF` changé en `| 0xFF` modifie alors la valeur. C'est le cas de `getIntLE_20_0`, qui tue à lui seul 15 des 16 mutants de `getIntLE`.
- **Valeurs de retour (PrimitiveReturns, 27 de plus).** PIT remplace la valeur renvoyée par 0. Tout test qui attend une valeur non nulle détecte ce mutant ; il suffit que la méthode soit appelée et son résultat comparé. Les 31 mutants de ce type sont tués dès qu'un test existe pour chaque surcharge.
- **Conditions inversées (NegateConditionals, 7 de plus).** La garde `if ((ch1 | ch2 | ...) < 0)` inversée lève l'exception sur une lecture valide. Tout test qui lit un flux complet et vérifie une valeur échoue alors sur l'exception.
- **Incréments (Increments, 6 de plus).** Dans `getIntLE` et `getIntBE`, `i++` changé en `i--` fait relire le mauvais octet. Le mutant n'est détecté que si l'octet relu a une valeur différente.
- **Borne de boucle (ConditionalsBoundary, 1 de plus).** Dans `getLongLE`, `j >= offset` changé en `j > offset` saute le dernier octet. Le test corrigé `getLongLE_28_0`, avec huit octets distincts, le détecte.

Ce dernier point explique l'effet des données symétriques. Pour `getIntBE`, le test généré `getIntBE_23_0` utilise huit octets à `FF` et ne tue que 11 mutants sur 16 : relire un autre octet ou garder les bits de poids fort donne le même résultat. Le test corrigé `getIntBE_22_0`, avec `FF 00 00 00`, en tue 4 de plus : deux `i++` changés en `i--`, un `& 0xFF` changé en `| 0xFF` et le retour à 0 de la surcharge à un seul argument.

### 6.3 Les mutants que les tests générés ne détectent pas

Il reste 44 mutants vivants avec les tests d'origine et tous les tests générés.

| Mutants | Nombre | Pourquoi ils survivent |
|---|---|---|
| `< 0` devient `<= 0` dans la garde de fin de flux des 9 méthodes `read*` à largeur fixe | 9 | Les deux versions ne diffèrent que si tous les octets lus sont nuls. Aucun test ne lit de zéros. |
| `\|` devient `&` dans la même garde | 29 | Le mutant ne lève plus l'exception pour certains flux incomplets. Tels que générés, aucun test ne vérifie un flux trop court ; un seul le fait une fois corrigé (`readIntME`), sur un mutant que les tests d'origine tuaient déjà. |
| Bornes de la boucle de `readUE7` | 4 | Le test généré lit un nombre d'un seul octet ; les tests d'origine n'ont ni octet nul après un octet de continuation, ni nombre de plus de 6 octets. |
| `i++` devient `i--` sur la dernière lecture de `getIntLE` et `getIntBE` | 2 | Mutants équivalents (section 6.5). |

### 6.4 Ce que la mutation ne montre pas

La ligne 247 de `EndianUtils`, qui lève une `IOException` quand un nombre UE7 est coupé au milieu, n'était exécutée par aucun test. PIT ne l'a pas signalée : aucun de ses mutateurs par défaut ne s'applique à cette ligne. C'est le rapport de couverture de JaCoCo qui l'a révélée. Un test manuel la couvre désormais (section 7, test 11).

### 6.5 Les deux mutants équivalents

Dans `getIntLE(byte[], int)` et `getIntBE(byte[], int)`, la dernière lecture s'écrit `int b3 = data[i++] & 0xFF;`. PIT change `i++` en `i--`. L'octet lu est le même, puisque l'ancienne valeur de `i` est utilisée, et `i` n'est plus lu ensuite. Le comportement est identique pour toute entrée : aucun test ne peut tuer ces deux mutants. Le score maximal atteignable est donc 204 sur 206.

## 7. Tests supplémentaires écrits à la main

Les 11 tests sont dans [`EndianUtilsSurvivingMutantsTest.java`](tika-core/src/test/java/org/apache/tika/io/EndianUtilsSurvivingMutantsTest.java). Ils tuent 42 des 44 mutants restants ; les 2 autres sont équivalents.

Plusieurs tests utilisent un flux « scripté », défini dans la classe de test : son `read()` renvoie une suite de valeurs fixée d'avance. Contrairement à un `ByteArrayInputStream`, il peut signaler une fin de flux (-1) puis renvoyer encore des octets, comme un flux sur un fichier en cours d'écriture.

### Tests 1 à 4 : des octets nuls ne sont pas une fin de flux

| N° | Nom du test | Méthodes | Données | Mutants tués |
|---|---|---|---|---|
| 1 | `testReadUShortOfZeroBytesIsZero` | `readUShortLE`, `readUShortBE` | 2 octets nuls | 2 |
| 2 | `testReadUIntOfZeroBytesIsZero` | `readUIntLE`, `readUIntBE` | 4 octets nuls | 2 |
| 3 | `testReadIntOfZeroBytesIsZero` | `readIntLE`, `readIntBE`, `readIntME` | 4 octets nuls | 3 |
| 4 | `testReadLongOfZeroBytesIsZero` | `readLongLE`, `readLongBE` | 8 octets nuls | 2 |

- **Comportement vérifié** : zéro est une valeur valide. La garde `if ((ch1 | ch2 | ...) < 0)` ne doit pas la confondre avec une fin de flux.
- **Choix des données** : des octets tous nuls sont la seule entrée pour laquelle `ch1 | ch2 | ...` vaut exactement 0, la frontière de la condition. Avec toute autre entrée valide, `< 0` et `<= 0` donnent le même résultat.
- **Oracle** : 0. Un entier dont tous les octets sont nuls vaut 0 quel que soit l'ordre des octets ; aucun calcul de boutisme n'intervient.
- **Mutants tués** : `< 0` changé en `<= 0` aux lignes 64, 73, 92, 111, 130, 149, 168, 191 et 217. Le mutant lève `BufferUnderrunException` au lieu de renvoyer 0.

### Test 5 : `testTruncatedStreamIsRejected`

- **Comportement vérifié** : un flux plus court d'un octet que la largeur demandée lève `BufferUnderrunException`, pour les 9 méthodes à largeur fixe.
- **Choix des données** : 1 octet pour les lectures sur 16 bits, 3 pour celles sur 32 bits, 7 pour celles sur 64 bits. « Un octet de moins » est la frontière entre une lecture valide et un dépassement, et c'est le cas réaliste d'un fichier tronqué. Les octets valent `01`, `02`, `03`... pour être non nuls et distincts.
- **Oracle** : l'exception, d'après la Javadoc des méthodes : `@throws BufferUnderrunException if the stream cannot provide enough bytes`.
- **Mutants tués** : 7, le dernier `|` de la garde changé en `&` dans `readUShortLE`, `readUShortBE`, `readUIntBE`, `readIntLE`, `readIntBE`, `readLongLE` et `readLongBE`. Avec ce mutant, les octets valides masquent le -1 final et l'exception n'est plus levée.
- Ce test répare aussi le défaut du test d'origine : le flux trop court est enfin vérifié sur `readUIntBE`.

### Tests 6 à 8 : la fin de flux est détectée à n'importe quelle lecture

| N° | Nom du test | Méthodes | Suite renvoyée par le flux scripté | Mutants tués |
|---|---|---|---|---|
| 6 | `testReadUIntDetectsEndOfStreamOnFirstRead` | `readUIntLE`, `readUIntBE` | -1, `01`, `02`, `03` | 5 |
| 7 | `testReadIntDetectsEndOfStreamOnFirstRead` | `readIntLE`, `readIntBE`, `readIntME` | -1, `01`, `02`, `03` | 8 |
| 8 | `testReadLongDetectsEndOfStreamOnFirstRead` | `readLongLE`, `readLongBE` | -1, `01` à `07` | 14 |

- **Comportement vérifié** : chaque lecture participe au contrôle de fin de flux, pas seulement la dernière.
- **Choix des données** : avec un flux ordinaire, une fois la fin atteinte, toutes les lectures suivantes renvoient -1. La dernière valeur est alors toujours -1, et `((ch1 | ch2) & ch3) | ch4` reste négatif : les mutants sur les `|` intermédiaires sont indétectables. Il faut un flux où une lecture échoue et les suivantes réussissent. Placer le -1 en première position est le seul choix qui tue tous les `|` d'une garde d'un coup : dès que le -1 rencontre un `&` avec un octet valide, le résultat redevient positif, et plus rien ensuite ne le rend négatif.
- **Oracle** : `BufferUnderrunException`. La garde `(ch1 | ch2 | ...) < 0` signifie « au moins une lecture a renvoyé -1 » ; la première lecture ayant échoué, le nombre n'a pas pu être lu en entier.
- **Mutants tués** : 27, chaque `|` changé en `&` aux lignes 92, 111, 130, 149, 168, 191 et 217 (certains sont aussi tués par le test 5).

### Test 9 : `testReadUE7ZeroByteAfterContinuation`

- **Comportement vérifié** : dans `readUE7`, un octet nul qui termine un nombre est une donnée et compte pour un groupe de 7 bits.
- **Choix des données** : `81 00`. L'octet `81` porte le bit de continuation et la valeur 1 ; l'octet `00` est le dernier groupe, de valeur 0. L'octet nul est la frontière des deux conditions visées, `>= 0` dans la boucle et `< 0` après elle.
- **Oracle** : 128, soit `(1 << 7) + 0`. Le format, décrit dans la Javadoc, range les groupes de 7 bits du plus significatif au moins significatif.
- **Mutants tués** : 2. Avec `>= 0` changé en `> 0` (ligne 235), la boucle s'arrête sur l'octet nul sans décaler et renvoie 1. Avec `< 0` changé en `<= 0` (ligne 246), l'octet nul est pris pour une fin de flux et une `IOException` est levée.

### Test 10 : `testReadUE7StopsAfterSixBytes`

- **Comportement vérifié** : `readUE7` décode au plus 6 octets.
- **Choix des données** : `81 80 80 80 80 80 01`, soit 6 octets de continuation suivis d'un septième. Les cinq octets `80` portent la valeur 0 pour que le résultat attendu se calcule de tête. Sept octets est la plus petite entrée qui dépasse la limite.
- **Oracle** : `1L << 35`. La valeur 1 du premier octet est décalée de 7 bits par chacun des cinq octets suivants ; le septième est lu mais n'entre pas dans la valeur. C'est un test de caractérisation : la limite vient de la constante `max = 6` du code, la Javadoc n'en précisant aucune.
- **Mutants tués** : 2 à la ligne 235, `read++ < max` changé en `read++ <= max` et `read++` changé en `read--`. Les deux décodent le septième octet et renvoient `(1L << 42) + 1`.

### Test 11 : `testReadUE7TruncatedNumberIsRejected`

- **Comportement vérifié** : un nombre UE7 coupé après un octet de continuation est une erreur.
- **Choix des données** : l'octet `81` seul. Son bit de continuation annonce une suite, mais le flux s'arrête.
- **Oracle** : `IOException`, levée par le code à la ligne 247 avec le message « expected one more byte ».
- **Mutants tués** : aucun. Ce test ne vise pas un survivant mais la ligne non couverte décrite en section 6.4. Il porte la couverture de 119 à 120 lignes sur 121 et de 26 à 28 branches sur 28. La dernière ligne non couverte est le constructeur implicite de cette classe utilitaire.

## 8. Exécution dans GitHub Actions

Le workflow [`.github/workflows/tache2.yml`](.github/workflows/tache2.yml) s'exécute à chaque push et à chaque pull request sur `main`, et peut être lancé à la main. Sur Ubuntu avec JDK 17, il :

1. installe les deux modules dont `tika-core` dépend (`tika-parent`, `tika-annotation-processor`) ;
2. exécute les quatre classes de test d'`EndianUtils` avec `mvn -Pci test -pl tika-core -Dtest='EndianUtils*Test'`, en laissant actifs les contrôles de Tika (Enforcer, Checkstyle, Spotless) ;
3. lance PIT sur `EndianUtils` six fois, une par ligne du tableau de la section 5.2, avec l'option `fullMutationMatrix` ; la dernière exécution, avec tous les tests, fait échouer le build si le score de mutation passe sous 95 % ;
4. écrit le tableau des six scores dans le résumé de l'exécution ;
5. joint deux artefacts : `pit-reports-endianutils`, les six rapports de PIT (HTML et XML), et `jacoco-report-endianutils`, le rapport de couverture.

Les tableaux des sections 5 à 7 peuvent ainsi être revérifiés à partir des artefacts d'une exécution, sans rien installer. L'état de la dernière exécution est donné par le badge en tête de ce fichier ; le détail de chaque exécution, avec son résumé et ses artefacts, est sur la [page du workflow](https://github.com/NizarLakhder/tika/actions/workflows/tache2.yml).

Les nouveaux tests respectent les règles du projet : aucune violation Checkstyle, formatage Spotless conforme, en-têtes de licence présents. Le dossier `chatunitest-tests/`, conservé tel que généré, est exclu du contrôle des licences dans `tika-core/pom.xml`.

Les autres workflows du dépôt sont ceux d'Apache Tika (builds complets, images Docker) et ne font pas partie de cette tâche.

## 9. Reproduire les résultats

```bash
# Dépendances de tika-core
mvn install -pl tika-annotation-processor -am -Pfast

# Les 51 tests d'EndianUtils
mvn -Pci test -pl tika-core -Dtest='EndianUtils*Test'

# Les six analyses de mutation du tableau de la section 5.2, dans le même ordre
p=org.apache.tika.io
pit() {
  mvn pitest:mutationCoverage -pl tika-core -DfullMutationMatrix=true \
    "-DreportsDirectory=target/pit-reports/$1" "-DtargetTests=$2"
}
pit 0-original "$p.EndianUtilsTest"
pit 1-generated-only "$p.EndianUtilsGeneratedTest"
pit 2-original-generated "$p.EndianUtilsTest,$p.EndianUtilsGeneratedTest"
pit 3-generated-corrected-only "$p.EndianUtilsGeneratedTest,$p.EndianUtilsGeneratedCorrectedTest"
pit 4-original-generated-corrected "$p.EndianUtilsTest,$p.EndianUtilsGeneratedTest,$p.EndianUtilsGeneratedCorrectedTest"
pit 5-all "$p.EndianUtils*Test"

# Génération avec ChatUniTest (Ollama et l'alias code-llama de la section 2.3 requis)
mvn chatunitest:class -pl tika-core -Pchatunitest -DselectClass=EndianUtils
```

Les analyses de mutation réutilisent les classes compilées par la commande de test qui les précède. Chacune écrit son rapport dans `tika-core/target/pit-reports/<nom>/` : `index.html` pour la lecture, et `mutations.xml`. Avec `fullMutationMatrix`, ce fichier XML donne pour chaque mutant tous les tests qui le tuent ; c'est la source des sections 6 et 7. La commande de test écrit aussi le rapport de couverture JaCoCo dans `tika-core/target/site/jacoco/`.

Ces commandes sont celles du workflow de la section 8, qui publie les mêmes rapports en artefacts à chaque exécution.

Sous PowerShell, il faut utiliser `.\mvnw.cmd` et mettre chaque argument `-D...` entre guillemets.

Les tests générés par un modèle de langage ne sont pas reproductibles à l'identique : une nouvelle génération donnera d'autres tests. La sortie utilisée ici est conservée dans `tika-core/chatunitest-tests/`.

## 10. Utilisation de l'intelligence artificielle

J'ai utilisé [ChatGPT](https://chatgpt.com/) les 11 et 12 octobre 2026 (modèle déclaré : Luna 5.6). Je lui ai présenté les résultats obtenus pour cette tâche afin d'obtenir une aide à la rédaction et à la révision de la documentation, ainsi qu'à la vérification de la cohérence des tests, de leurs assertions et des résultats. Ses réponses ont servi à améliorer les explications présentées dans le README et à repérer des points à vérifier.

Les deux exemples ci-dessous sont des reformulations des requêtes, à partir de l'usage décrit :

1. **Documentation** : « Voici les résultats de génération des tests avec ChatUniTest et des analyses de mutation avec PIT. Peux-tu m'aider à les organiser et à les expliquer clairement dans mon README pour la tâche 2 d'IFT3913 ? »
2. **Vérification** : « Voici mes tests, leurs assertions et les résultats obtenus. Peux-tu vérifier leur cohérence et m'aider à repérer des erreurs dans les valeurs attendues ou dans mes explications des scores de mutation ? »

La génération automatique des tests avec ChatUniTest 2.1.1 et le modèle `granite-code:3b`, exécuté localement via Ollama, est documentée dans les sections 2 à 4.
