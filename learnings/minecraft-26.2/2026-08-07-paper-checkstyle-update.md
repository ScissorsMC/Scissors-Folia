# Paper API Checkstyle project update

## Version

- Minecraft: `26.2`
- Folia: `24c5c95dc45e02caff98a97ed6ffee7565523464`
- Revalidate after changing either version above

## What changed upstream

Paper added a `paper-checkstyle` Gradle project, root `.checkstyle` configuration, API-specific `.checkstyle` files,
and Checkstyle wiring in `paper-api/build.gradle.kts`. Folia layers its own `folia-checkstyle/build.gradle.kts` over
that Paper-owned source project. A downstream fork must materialize both layers; including only the generated API
project leaves its Checkstyle project, configuration, or source paths unresolved.

## Scissors Folia integration

- Include the upstream-owned build project as `folia-checkstyle`; do not rename it to Scissors.
- Apply Folia's `folia-checkstyle/build.gradle.kts` as a single-file patch target.
- Materialize Paper's `paper-checkstyle/` sources and root `.checkstyle/` configuration through paperweight `patchRepo`
  entries with empty Scissors patch sets.
- Keep `folia-checkstyle` build output outside the generated upstream worktree so patch regeneration never scans class
  files or test results as source changes.
- Keep Folia's Paper Checkstyle plugin, dependency, custom Javadoc tags, and tests when rebasing the generated API
  build patch.
- The applied Paper API sources live under `paper-api/`, but the Gradle project using them is `scissors-api/`.
  Therefore point `directoriesToSkipFile` and `MergeCheckstyleConfigs.overrideConfigFile` at
  `paper-api/.checkstyle/...`.
- Folia's Checkstyle build script must reference the materialized `paper-checkstyle/` sources and configuration rather
  than expecting them inside the generated `folia-checkstyle/` directory.

## Verification

`applyAllPatches` must materialize the Folia Checkstyle build script plus the Paper Checkstyle source and configuration
patch sets. A full `build` must execute and pass `folia-checkstyle` tests plus `scissors-api:checkstyleMain` and
`scissors-api:checkstyleTest`.
