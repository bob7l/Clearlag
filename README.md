
Clearlag's an extremely old plugin that has somehow survived since 2011. Much of the code is probably outdated or inefficient due to API/internal changes of Spigot from other the years. 

**Modules**
- Clearlag Legacy: For Spigot/Bukkit builds before 1.8
- Clearlag Core: For 1.8+

**Building the modern plugin**

With Maven and JDK 21, run `mvn -f "Clearlag Core/pom.xml" clean verify`.
The plugin is `Clearlag Core/target/clearlag-core.jar`. Building this module directly
avoids the root reactor's missing `savedeathdrops` module and the separate 1.7 legacy build.

Paper 26.3 support uses the public Bukkit version string and Paper's TPS API.
Older servers retain the existing internal TPS reader and estimation fallback.
