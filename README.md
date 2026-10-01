# Client Capes (Fabric 1.21.11, client-side only)

Shows any cape texture on YOUR player only. Other players and servers never see it.
Uses Mojang mappings (Yarn ends after 1.21.11).

## Build
1. Install JDK 21.
2. Loom 1.14 needs Gradle 9.2+. Easiest: copy `gradlew`, `gradlew.bat` and the `gradle/` folder from the
   official Fabric example mod into this folder, or run `gradle wrapper --gradle-version 9.2.0` once.
3. `./gradlew build` -> `build/libs/clientcapes-1.0.0.jar`
4. Put the jar in your mods folder (needs Fabric API; Mod Menu optional).

## Use
1. Start the game once, then put cape PNGs into `.minecraft/config/clientcapes/capes/`
   (64x32, 22x17, or HD multiples of those; the file name is the cape name).
2. Press K in-game (rebindable) or Mod Menu -> Client Capes -> Configure.
3. Use < / > to switch capes, toggle on/off, reload after adding files.

The "Cape" toggle in Options > Skin Customization still applies, and vanilla rules
(e.g. an elytra replaces the cape) are unchanged.
