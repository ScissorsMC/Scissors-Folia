# Scissors Folia

Scissors Folia is the [Folia](https://github.com/PaperMC/Folia)-based edition
of [Scissors](https://github.com/ScissorsMC/Scissors). It applies Scissors'
exploit and security patches on top of Folia while retaining the Scissors
product identity.

Folia's regionized multithreading model has materially different plugin
compatibility requirements from Paper. A plugin must explicitly support Folia
and declare `folia-supported: true` before it can be loaded.

## Building

Building requires Git, an internet connection for initial setup, and Java 25.
Gradle can provision the Java 25 toolchain when run with Java 21 or newer.

```shell
git clone https://github.com/ScissorsMC/Scissors-Folia.git
cd Scissors-Folia
./gradlew applyAllPatches
./gradlew :scissors-server:createPaperclipJar
```

On Windows, enable Git long-path support before applying patches:

```powershell
git config --global core.longpaths true
```

Then use:

```powershell
.\gradlew.bat applyAllPatches
.\gradlew.bat :scissors-server:createPaperclipJar
```

The runnable server is written to
`scissors-server/build/libs/scissors-paperclip-<version>.jar`. The
`scissors-server-<version>.jar` in the same directory is a thin development
JAR and does not include the runtime dependencies required to start the
server.

Contributors can run `./gradlew build` (or `.\gradlew.bat build` on Windows)
to compile all modules and run the test suite. This verification task does not
create the runnable Paperclip JAR.

## Development

Scissors uses
[paperweight](https://github.com/PaperMC/paperweight) to store changes as Git
patches over a pinned Folia commit. Paperweight first applies Paper and Folia,
then applies Scissors' API, Paper-server, and Minecraft patch layers. Generated
Paper and Minecraft worktrees are ignored; edits to existing upstream code must
be committed or folded into the appropriate nested patch repository, then
rebuilt into tracked patch files.

Read [AGENTS.md](AGENTS.md) before contributing. It defines the patch workflow,
Git boundaries, Scissors/Folia/Paper naming boundary, and required verification.
