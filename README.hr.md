# Cryptara

[English](README.md) | **Hrvatski**

Cryptara je desktop aplikacija za kriptiranje, dekriptiranje, izračun sažetka (hash) i digitalno potpisivanje datoteka. Napisana je u čistoj Javi (Swing, bez vanjskih biblioteka) i podržava i klasične algoritme (AES, RSA) i nove post-kvantne standarde (ML-KEM, ML-DSA).

> Sučelje aplikacije je na engleskom jeziku. U ovim uputama nazivi gumba i polja navedeni su točno onako kako se prikazuju u aplikaciji, uz prijevod gdje je potrebno.

---

## Sadržaj

1. [Pregled mogućnosti](#pregled-mogućnosti)
2. [Preduvjeti](#preduvjeti)
3. [Prevođenje i pokretanje](#prevođenje-i-pokretanje)
4. [Brzi početak](#brzi-početak)
5. [Korištenje aplikacije po karticama](#korištenje-aplikacije-po-karticama)
   - [Keys (ključevi)](#kartica-keys-ključevi)
   - [Symmetric (simetrično)](#kartica-symmetric-simetrično-kriptiranje)
   - [Asymmetric (asimetrično)](#kartica-asymmetric-asimetrično-kriptiranje)
   - [Hash (sažetak)](#kartica-hash-sažetak)
   - [Signature (potpis)](#kartica-signature-digitalni-potpis)
6. [Datoteke koje aplikacija stvara](#datoteke-koje-aplikacija-stvara)
7. [Razmjena ključeva s drugim osobama](#razmjena-ključeva-s-drugim-osobama)
8. [Poruke i rješavanje problema](#poruke-i-rješavanje-problema)
9. [Sigurnosne napomene i ograničenja](#sigurnosne-napomene-i-ograničenja)
10. [Tehnički detalji](#tehnički-detalji)
11. [Struktura projekta](#struktura-projekta)
12. [Licenca](#licenca)

---

## Pregled mogućnosti

| Kartica | Što radi | Algoritam |
|---|---|---|
| **Keys** | Jednim klikom generira sve ključeve koje aplikacija treba | AES-256, RSA-3072 (×2), ML-KEM-768, ML-DSA-65 |
| **Symmetric** | Kriptira/dekriptira datoteku zajedničkim tajnim ključem | AES-256-GCM |
| **Asymmetric** | Kriptira datoteku javnim ključem, dekriptira privatnim | RSA-OAEP (3072-bit) ili ML-KEM-768 (post-kvantni), oba u kombinaciji s AES-256-GCM |
| **Hash** | Izračunava "otisak" datoteke i kasnije provjerava je li se datoteka promijenila | SHA-256 |
| **Signature** | Potpisuje datoteku privatnim ključem i provjerava potpis javnim ključem | RSA-PSS (3072-bit) ili ML-DSA-65 (post-kvantni) |

---

## Preduvjeti

- **Java JDK 24 ili novija** (preporučeno JDK 25 LTS).
  Post-kvantni algoritmi ML-KEM i ML-DSA dodani su u standardnu Java biblioteku tek u verziji JDK 24. Na starijim verzijama kod se neće prevesti.
  Verziju provjerite naredbama:
  ```
  java -version
  javac -version
  ```
  Ako trebate JDK, besplatna opcija je [Eclipse Temurin](https://adoptium.net/).
- Bilo koji operacijski sustav s grafičkim sučeljem koji pokreće Javu (Windows, macOS, Linux).
- Nisu potrebne dodatne biblioteke, alati za izgradnju ni internetska veza.

---

## Prevođenje i pokretanje

Projekt nema alat za izgradnju (nema Mavena/Gradlea); prevodi se izravno naredbom `javac`. Naredbe pokrenite iz korijenske mape projekta (one koja sadrži `src/`).

### Windows (PowerShell)

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out cryptara.Main
```

### macOS / Linux / Git Bash

```bash
javac -d out $(find src -name "*.java")
java -cp out cryptara.Main
```

### Opcionalno: izrada izvršne JAR datoteke

Nakon prevođenja u mapu `out/`:

```
jar --create --file cryptara.jar --main-class cryptara.Main -C out cryptara
java -jar cryptara.jar
```

### Iz razvojnog okruženja (IDE)

Otvorite mapu u VS Codeu (s Java ekstenzijama), IntelliJ IDEA ili Eclipseu, označite `src` kao izvornu mapu, postavite JDK projekta na 24+ i pokrenite metodu `main` u `src/cryptara/Main.java`.

> **Važno — radna mapa.** Zadana mapa s ključevima je relativna putanja `keys`, koja se računa u odnosu na mapu **iz koje ste pokrenuli aplikaciju**. Ako aplikaciju uvijek pokrećete iz korijena projekta, ključevi će biti u `<projekt>/keys/`. Ako je pokrećete s drugog mjesta, u polja *Keys folder* upišite punu putanju ili koristite *Browse...*.

Prozor se otvara maksimiziran, s tamnom temom i pet kartica: **Keys, Symmetric, Asymmetric, Hash, Signature**.

---

## Brzi početak

1. Pokrenite aplikaciju.
2. Otvorite karticu **Keys** i kliknite **Generate keys**. Pričekajte poruku `Keys saved to ...`.
3. Otvorite karticu **Symmetric**, kliknite **Browse...** pokraj *Input file*, odaberite bilo koju datoteku i kliknite **Encrypt**. Pokraj originala pojavit će se datoteka `<naziv>.aes`.
4. Odaberite `.aes` datoteku kao ulaz i kliknite **Decrypt**. Pojavit će se datoteka `decrypted_<naziv>` — identična originalu.

To je sve. Ostale kartice rade na isti način: odaberite mapu s ključevima (zadani `keys` obično je u redu), odaberite datoteku, odaberite algoritam gdje je ponuđen i pritisnite gumb. Rezultati i greške ispisuju se u području za zapis (log) na dnu svake kartice.

---

## Korištenje aplikacije po karticama

Svaka kartica ima:
- **Polja za putanje** s gumbom **Browse...** (pregledaj). Putanju možete upisati, zalijepiti ili odabrati.
- **Gumbe za radnje.** Dok se radnja izvodi, gumbi su onemogućeni; ponovno se omogućuju kad završi.
- **Područje za zapis** (na dnu) koje prikazuje što se dogodilo. Zapis se nigdje ne sprema; briše se zatvaranjem aplikacije.
- **Zaštitu od prepisivanja.** Ako izlazna datoteka već postoji, aplikacija pita *"... already exists. Overwrite it?"* ("... već postoji. Prepisati?") prije pisanja. Odaberite **No** za odustajanje.

### Kartica Keys (ključevi)

Generira cijeli skup ključeva i sprema ih u mapu.

| Polje / gumb | Značenje |
|---|---|
| **Output folder** | Mapa u koju se spremaju ključevi. Zadano: `keys`. Mapa se stvara ako ne postoji. |
| **Browse...** | Odabir mape. |
| **Generate keys** | Generira sve ključeve. Generiranje RSA ključeva može potrajati nekoliko sekundi. |

Jedan klik generira **sve** sljedeće:

| Ključ | Koristi ga |
|---|---|
| AES-256 tajni ključ | Kartica Symmetric |
| RSA-3072 par ključeva (RSA-OAEP) | Kartica Asymmetric, RSA-OAEP |
| ML-KEM-768 par ključeva | Kartica Asymmetric, ML-KEM |
| RSA-3072 par ključeva (RSA-PSS) — zaseban par, različit od gornjeg | Kartica Signature, RSA-PSS |
| ML-DSA-65 par ključeva | Kartica Signature, ML-DSA |

Ključevi se zapisuju u tri datoteke:

| Datoteka | Sadrži | Čuvati u tajnosti? |
|---|---|---|
| `tajni_kljuc.txt` | AES-256 ključ | **Da.** Dijelite ga samo s osobama koje smiju dekriptirati vaše simetrično kriptirane datoteke. |
| `javni_kljuc.txt` | Javne dijelove četiriju parova ključeva | Ne — namijenjen je dijeljenju. |
| `privatni_kljuc.txt` | Privatne dijelove četiriju parova ključeva | **Da — nikad ga ne dijelite.** |

> ⚠️ **Ponovno generiranje ključeva zamjenjuje sve tri datoteke.** Ako mapa već sadrži ključeve, aplikacija najprije traži potvrdu. Nakon ponovnog generiranja, datoteke kriptirane starim ključevima **više se ne mogu dekriptirati**, a stari potpisi više neće biti valjani. Napravite sigurnosnu kopiju starih ključeva ako vam još trebaju.

### Kartica Symmetric (simetrično kriptiranje)

Kriptira i dekriptira datoteke AES-256 tajnim ključem (`tajni_kljuc.txt`). Isti ključ koristi se u oba smjera.

| Polje / gumb | Značenje |
|---|---|
| **Keys folder** | Mapa koja sadrži `tajni_kljuc.txt`. Zadano: `keys`. |
| **Input file** | Datoteka za kriptiranje ili `.aes` datoteka za dekriptiranje. |
| **Encrypt** | Zapisuje `<ulaz>.aes` pokraj ulazne datoteke. Primjer: `izvjestaj.pdf` → `izvjestaj.pdf.aes`. |
| **Decrypt** | Zapisuje `decrypted_<naziv>` pokraj ulazne datoteke, uz uklanjanje nastavka `.aes` ako postoji. Primjer: `izvjestaj.pdf.aes` → `decrypted_izvjestaj.pdf`. |

AES-GCM je *autentificirano* kriptiranje: ako je kriptirana datoteka promijenjena makar za jedan bajt ili se koristi pogrešan ključ, dekriptiranje javlja grešku *"Wrong key or the file was modified"* (pogrešan ključ ili je datoteka izmijenjena) umjesto da proizvede besmislen sadržaj.

Kriptiranje iste datoteke dvaput svaki put daje drugačiji rezultat — to je normalno (svaki put se koristi nova slučajna vrijednost).

### Kartica Asymmetric (asimetrično kriptiranje)

Kriptiranje javnim ključem: **svatko tko ima javni ključ može kriptirati** datoteku, ali **samo vlasnik privatnog ključa može je dekriptirati**.

| Polje / gumb | Značenje |
|---|---|
| **Keys folder** | Mapa s `javni_kljuc.txt` (za kriptiranje) i/ili `privatni_kljuc.txt` (za dekriptiranje). |
| **Input file** | Datoteka za kriptiranje ili kriptirana datoteka za dekriptiranje. |
| **Algorithm** | `RSA-OAEP (3072-bit)` ili `ML-KEM-768 (post-quantum)`. |
| **Encrypt** | Koristi **javni** ključ. Zapisuje `<ulaz>.rsa` ili `<ulaz>.kem`. |
| **Decrypt** | Koristi **privatni** ključ. Zapisuje `decrypted_<naziv>` (uz uklanjanje `.rsa`/`.kem` ako odgovara odabranom algoritmu). |

**Koji algoritam odabrati?**
- **RSA-OAEP** — klasičan, široko korišten izbor.
- **ML-KEM-768** — *post-kvantni* algoritam standardiziran od NIST-a (FIPS 203), osmišljen da ostane siguran i protiv budućih kvantnih računala.

> Dekriptirati morate **istim algoritmom** kojim ste kriptirali. Za `.rsa` datoteku odaberite *RSA-OAEP*, za `.kem` datoteku *ML-KEM-768*. Pogrešan odabir rezultira greškom.

Kako to radi iznutra (hibridno kriptiranje): sama datoteka uvijek se kriptira novim, slučajnim AES-256-GCM ključem. RSA-OAEP-om se štiti (odnosno ML-KEM-om dogovara) samo taj mali AES ključ. Zahvaljujući tome mogu se kriptirati datoteke bilo koje veličine, a datoteka je zaštićena i od neovlaštenih izmjena.

### Kartica Hash (sažetak)

Izračunava SHA-256 sažetak ("otisak") datoteke. Sažetak omogućuje da kasnije provjerite je li se datoteka promijenila (npr. nakon preuzimanja ili kopiranja). **Sažetak nije kriptiranje** — ne skriva sadržaj i ne treba ključeve.

| Polje / gumb | Značenje |
|---|---|
| **Input file** | Datoteka čiji se sažetak računa ili provjerava. |
| **Hash file** | *Opcionalno.* Gdje se sažetak sprema / odakle se čita. Ako je prazno, koristi se `<ulaz>.sha256` pokraj ulazne datoteke. |
| **Compute hash** | Prikazuje sažetak u zapisu i sprema ga u datoteku sažetka. |
| **Verify hash** | Ponovno izračunava sažetak i uspoređuje ga sa spremljenim. |

Rezultati:
- `OK: the file matches the stored hash.` — datoteka je nepromijenjena.
- `MISMATCH: the file or the hash file has been changed.` — nešto se razlikuje (datoteka ili datoteka sažetka je promijenjena).

Format datoteke sažetka isti je kao kod standardnog alata `sha256sum` (`<64 heksadekadska znaka>  <naziv datoteke>`), pa Cryptarine datoteke sažetka možete provjeriti s `sha256sum -c`, a Cryptara može provjeriti datoteke koje je stvorio `sha256sum` (čita se samo prvi sažetak u datoteci).

> Sažetak dokazuje samo da datoteka odgovara datoteci sažetka. Tko može promijeniti datoteku, može promijeniti i datoteku sažetka. Ako trebate dokaz o tome **tko** je stvorio datoteku, koristite karticu **Signature**.

### Kartica Signature (digitalni potpis)

Digitalni potpis dokazuje da je datoteku potpisao vlasnik određenog privatnog ključa i da od tada nije mijenjana.

| Polje / gumb | Značenje |
|---|---|
| **Keys folder** | Mapa s `privatni_kljuc.txt` (za potpisivanje) i/ili `javni_kljuc.txt` (za provjeru). |
| **Input file** | Datoteka koja se potpisuje ili provjerava (originalna datoteka, ne potpis). |
| **Signature file** | *Opcionalno.* Gdje se potpis sprema / odakle se čita. Ako je prazno: `<ulaz>.rsa.sig` (RSA-PSS) ili `<ulaz>.mldsa.sig` (ML-DSA). |
| **Algorithm** | `RSA-PSS (3072-bit)` ili `ML-DSA-65 (post-quantum)` (FIPS 204). |
| **Sign** | Potpisuje datoteku **privatnim** ključem i sprema potpis. Originalna datoteka se ne mijenja. |
| **Verify** | Provjerava potpis **javnim** ključem. |

Rezultati:
- `VALID: the file is unchanged and the signature matches the public key.` — potpis je valjan.
- `NOT VALID: the file or the signature has been changed, or it was made with another key.` — datoteka ili potpis su mijenjani, ili je potpis napravljen drugim ključem.
- `NOT VALID: the signature file is damaged.` — datoteka potpisa je oštećena.

Da bi netko provjerio vašu datoteku, pošaljite mu **datoteku, `.sig` datoteku i svoj `javni_kljuc.txt`**. Provjera se radi istim algoritmom kojim je datoteka potpisana.

---

## Datoteke koje aplikacija stvara

Sve izlazne datoteke zapisuju se **u istu mapu u kojoj je ulazna datoteka**.

| Radnja | Ulaz | Izlaz |
|---|---|---|
| Generiranje ključeva | — | `tajni_kljuc.txt`, `javni_kljuc.txt`, `privatni_kljuc.txt` u odabranoj mapi |
| Simetrično kriptiranje | `datoteka.ext` | `datoteka.ext.aes` |
| Simetrično dekriptiranje | `datoteka.ext.aes` | `decrypted_datoteka.ext` |
| RSA-OAEP kriptiranje | `datoteka.ext` | `datoteka.ext.rsa` |
| ML-KEM kriptiranje | `datoteka.ext` | `datoteka.ext.kem` |
| Asimetrično dekriptiranje | `datoteka.ext.rsa` / `datoteka.ext.kem` | `decrypted_datoteka.ext` |
| Izračun sažetka | `datoteka.ext` | `datoteka.ext.sha256` (osim ako je zadana datoteka sažetka) |
| RSA-PSS potpis | `datoteka.ext` | `datoteka.ext.rsa.sig` (osim ako je zadana datoteka potpisa) |
| ML-DSA potpis | `datoteka.ext` | `datoteka.ext.mldsa.sig` (osim ako je zadana datoteka potpisa) |

Napomene:
- Originalna datoteka nikad se ne mijenja ni briše. Ako želite da ostane samo kriptirana kopija, original izbrišite sami.
- Ulazna i izlazna datoteka moraju biti različite — aplikacija odbija prepisati ulaz vlastitim izlazom.
- Datoteke ključeva i potpisa običan su tekst, jedan zapis `NAZIV=Base64` po retku, npr. `AES-256=6suL...`. Ne uređujte ih ručno; neispravan redak čini datoteku nečitljivom za aplikaciju.

---

## Razmjena ključeva s drugim osobama

Aplikacija ključeve uvijek traži po fiksnim nazivima datoteka (`tajni_kljuc.txt`, `javni_kljuc.txt`, `privatni_kljuc.txt`) unutar odabrane mape **Keys folder**. Za rad s tuđim ključem stavite njegovu datoteku u zasebnu mapu i u polju *Keys folder* odaberite tu mapu.

**Netko vam želi poslati kriptiranu datoteku (Asymmetric):**
1. Vi generirate ključeve i pošaljete mu svoj `javni_kljuc.txt`.
2. On ga spremi u mapu (npr. `kljucevi_prijatelja/javni_kljuc.txt`), odabere tu mapu u kartici Asymmetric i kriptira.
3. Pošalje vam `.rsa` / `.kem` datoteku; vi je dekriptirate svojom mapom ključeva (koja sadrži `privatni_kljuc.txt`).

**Želite dokazati da je datoteka od vas (Signature):**
1. Potpišite datoteku koristeći svoju mapu ključeva.
2. Pošaljite datoteku, `.sig` datoteku i svoj `javni_kljuc.txt`.
3. Primatelj stavi `javni_kljuc.txt` u mapu, odabere je i klikne **Verify**.

**Dijeljenje simetričnog ključa:** obje strane trebaju isti `tajni_kljuc.txt`. Tko god ga ima, može dekriptirati sve vaše `.aes` datoteke, zato ga prenesite samo kanalom kojem vjerujete (najbolje osobno ili kriptiranog pomoću kartice Asymmetric).

**Nikad nikome ne šaljite `privatni_kljuc.txt`.**

---

## Poruke i rješavanje problema

| Poruka / problem | Uzrok i rješenje |
|---|---|
| Kod se ne prevodi, ne pronalazi `ML-KEM` / `ML-DSA` / `KEM` | JDK je stariji od verzije 24. Instalirajte JDK 24+ (preporučeno 25 LTS). |
| `package cryptara.keys does not exist` pri prevođenju svježe kloniranog repozitorija | Mapa `src/cryptara/keys/` nedostaje u repozitoriju (vidi napomenu u [Strukturi projekta](#struktura-projekta)). |
| `Error: choose an input file.` | Polje *Input file* je prazno — odaberite ulaznu datoteku. |
| `Error: invalid path.` | Putanja sadrži nedopuštene znakove. Koristite *Browse...*. |
| `Error: choose a file, not a folder root.` | Upisali ste disk/korijen (npr. `C:\`) umjesto datoteke. |
| `Error: java.nio.file.NoSuchFileException: keys\tajni_kljuc.txt` (ili druga datoteka ključa) | Ključevi nisu generirani ili *Keys folder* pokazuje na krivo mjesto. Generirajte ključeve ili odaberite ispravnu mapu. Zapamtite da je `keys` relativan u odnosu na mapu iz koje je aplikacija pokrenuta. |
| `Error: java.nio.file.NoSuchFileException: ...` (ulazna datoteka / sažetak / potpis) | Odabrana datoteka ne postoji. Provjerite putanju. |
| `No RSA-OAEP key found in javni_kljuc.txt` (ili slično) | Datoteka ključa postoji, ali ne sadrži potreban ključ — uređivana je ili je iz drugog izvora. Ponovno generirajte ključeve ili nabavite ispravnu datoteku. |
| `Invalid key file line` / `Invalid Base64 in key file line` | Datoteka ključa ili potpisa je oštećena (npr. ručno uređivana). Vratite je iz sigurnosne kopije. |
| `Error: decryption failed. Wrong key or the file was modified.` (Symmetric) | Koristi se drugi `tajni_kljuc.txt` od onog kojim je kriptirano (npr. ključevi su ponovno generirani) ili je `.aes` datoteka promijenjena/oštećena. |
| `Error: decryption failed. Wrong key, wrong algorithm, or the file was modified.` (Asymmetric) | Pogrešan privatni ključ, odabran je pogrešan algoritam (npr. RSA-OAEP za `.kem` datoteku) ili je datoteka promijenjena. |
| `Envelope is too short.` / `Envelope is corrupted.` / `Encrypted data is too short.` | Datoteka koju dekriptirate nije ispravna Cryptara kriptirana datoteka ili je skraćena. |
| `Input and output files must be different.` | Ulazna i izlazna putanja pokazuju na istu datoteku. Odaberite drugu datoteku. |
| `Hash file does not contain a valid SHA-256 digest.` | Odabrana datoteka sažetka ne sadrži SHA-256 sažetak. |
| `MISMATCH` pri provjeri sažetka | Datoteka (ili datoteka sažetka) promijenila se nakon izračuna sažetka. |
| `NOT VALID` pri provjeri potpisa | Datoteka ili potpis su promijenjeni, koristi se pogrešan javni ključ ili je odabran pogrešan algoritam. |
| `java.lang.OutOfMemoryError` | Datoteka je prevelika za memoriju (vidi ograničenja). Pokrenite s više memorije, npr. `java -Xmx4g -cp out cryptara.Main`. |

---

## Sigurnosne napomene i ograničenja

- **Datoteke ključeva nisu zaštićene.** `tajni_kljuc.txt` i `privatni_kljuc.txt` običan su Base64 tekst bez lozinke. Tko dođe do tih datoteka, može dekriptirati vaše podatke ili potpisivati u vaše ime. Čuvajte ih na uređaju kojem vjerujete i radite sigurnosne kopije.
- **Gubitak ključeva znači gubitak podataka.** Nema načina oporavka. Ako izgubite ili prepišete `tajni_kljuc.txt` ili `privatni_kljuc.txt`, pripadajuće kriptirane datoteke ne mogu se dekriptirati.
- **Originali ostaju na disku.** Kriptiranje stvara novu datoteku, a original ostaje čitljiv. Izbrišite original ako ne smije ostati na disku (obično brisanje i dalje može ostaviti podatke koji se mogu oporaviti).
- **Nazivi datoteka nisu kriptirani.** `tajni_planovi.pdf.aes` i dalje otkriva naziv.
- **Autentičnost javnog ključa.** Prije korištenja tuđeg `javni_kljuc.txt` provjerite dolazi li zaista od te osobe (npr. usporedite njegov SHA-256 sažetak telefonom). U suprotnom bi vam napadač mogao podmetnuti vlastiti javni ključ.
- **Potrošnja memorije.** Kriptiranje i dekriptiranje učitavaju cijelu datoteku u memoriju, pa vrlo velike datoteke (više GB) mogu zahtijevati više Java memorije. Izračun sažetka i potpisivanje čitaju datoteke u dijelovima i rade s datotekama bilo koje veličine.
- Aplikacija je **studentski projekt** (FOI, kolegij *Napredni operacijski sustavi*). Koristi standardne, provjerene Java kriptografske implementacije, ali nije neovisno revidirana. Ne oslanjajte se na nju kao jedinu zaštitu vrlo osjetljivih podataka.

---

## Tehnički detalji

Sva kriptografija dolazi iz standardne Java biblioteke (JCA/JCE); ne koristi se kriptografski kod trećih strana.

| Namjena | Algoritam i parametri |
|---|---|
| Simetrično kriptiranje | AES-256 u GCM načinu (`AES/GCM/NoPadding`), slučajni 12-bajtni IV za svako kriptiranje, 128-bitna autentifikacijska oznaka |
| RSA kriptiranje | RSA-3072, `RSA/ECB/OAEPPadding`, OAEP sa SHA-256 i MGF1-SHA-256 |
| Post-kvantni KEM | ML-KEM-768 (FIPS 203) preko `javax.crypto.KEM`; 32-bajtna zajednička tajna koristi se izravno kao AES-256 ključ |
| Sažetak | SHA-256 |
| RSA potpis | RSASSA-PSS, RSA-3072, SHA-256, MGF1-SHA-256, sol od 32 bajta |
| Post-kvantni potpis | ML-DSA-65 (FIPS 204) |
| Kodiranje ključeva | Javni ključevi X.509 (SubjectPublicKeyInfo), privatni PKCS#8, spremljeni kao Base64 |

### Formati datoteka

**`.aes`** (binarno):
```
[12-bajtni IV][AES-GCM kriptirani tekst][16-bajtna autentifikacijska oznaka]
```

**`.rsa` / `.kem`** (binarna "omotnica"):
```
[4-bajtna big-endian duljina N][N bajtova ključnog materijala][AES-GCM podaci kao u .aes]
```
Ključni materijal je RSA-OAEP-om kriptirani AES ključ (384 bajta za RSA-3072) ili ML-KEM-768 enkapsulacija (1088 bajtova). Za svaku datoteku koristi se novi slučajni AES ključ.

**Datoteke ključeva** (`tajni_kljuc.txt`, `javni_kljuc.txt`, `privatni_kljuc.txt`) i **datoteke potpisa** (`.sig`), UTF-8 tekst, jedan zapis po retku:
```
OZNAKA=Base64Podaci
```
Oznake: `AES-256`, `RSA-OAEP`, `ML-KEM-768`, `RSA-PSS`, `ML-DSA-65`. Datoteka potpisa sadrži jedan zapis s oznakom `RSA-PSS` ili `ML-DSA-65`.

**`.sha256`** (tekst, kompatibilno sa `sha256sum`):
```
<64 mala heksadekadska znaka><dva razmaka><naziv datoteke>
```

---

## Struktura projekta

```
Cryptara/
├── src/cryptara/
│   ├── Main.java                  Ulazna točka: primjenjuje temu, otvara prozor
│   ├── keys/                      Generiranje, spremanje i učitavanje ključeva
│   │   ├── KeyGeneration.java     Generira AES, RSA, ML-KEM i ML-DSA ključeve
│   │   ├── KeyManager.java        Generira sve ključeve i zapisuje tri datoteke ključeva
│   │   ├── KeyLoader.java         Učitava ključeve iz datoteka ključeva
│   │   └── KeyFiles.java          Tekstualni format OZNAKA=Base64
│   ├── symmetric/                 AES-GCM kriptiranje bajtova i datoteka
│   ├── asymmetric/                RSA-OAEP, ML-KEM, format omotnice, kriptiranje datoteka
│   ├── hash/                      Izračun, spremanje i provjera SHA-256 sažetka
│   ├── signature/                 RSA-PSS / ML-DSA potpisivanje i datoteke potpisa
│   ├── util/PathChecks.java       Provjerava jesu li ulazna i izlazna datoteka različite
│   └── ui/                        Swing prozor, jedan panel po kartici, tamna tema
├── LICENSE
├── README.md                      Engleska verzija
└── README.hr.md                   Ova datoteka (hrvatski)
```

---

## Licenca

Objavljeno pod [MIT licencom](LICENSE). © 2026 Jakov Kordić.
