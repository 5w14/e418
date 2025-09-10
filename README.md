# e418

This is a library mod providing functionality for data-driven gameplay events.

Here's an example of a data-driven event.

```jsonc
{
  "name": "Example event",
  "description": "",
  "behaviours": [
    {
      // Modify context to select a random location around a random player
      "type": "e418:mutate_context",
      "mutators": [
        {
          "type": "e418:select_random_player"
        },
        {
          "type": "e418:select_random_location_around_player"
        }
      ],

      // Run only if the player is below 30 on Y-axis
      "run_conditions": [
        {
          "type": "e418:at_height",
          "below": 30
        }
      ],

      // Play a cave sound
      "behaviours": [
        {
          "type": "e418:play_sound",
          "sound": "minecraft:ambient.cave",
          "volume": 1.0,
          "pitch": 0.8
        }
      ]
    }
  ],
  "run_conditions": [],
  "queue_conditions": []
}
```

### Use as a dependency

While this library could work as a driver for a datapack-driven addon, you can add your content using mods.

We recommend to develop multi-platforms addons using Architectury.

Add a version string to your `gradle.properties`:

```
e418_version=0.0.2
```

Add jitpack repository and dependencies:

```gradle
// in root build.gradle
repositories {
  maven {
    url = "https://jitpack.io/"
    // Note: JitPack compile times may be slow on first build
  }
}

dependencies {
  // in common/build.gradle
  modImplementation "com.github.5w14.e418:e418-common:${project.e418_version}"

  // in fabric/build.gradle
  modImplementation "com.github.5w14.e418:e418-fabric:${project.e418_version}"

  // in neoforge/build.gradle
  modImplementation "com.github.5w14.e418:e418-neoforge:${project.e418_version}"
}
```

## Documentation & Support

- **Documentation**: Visit our [wiki](https://github.com/5w14/e418/wiki) for comprehensive guides, API reference, and examples
- **Developer Support**: Join our [Discord server](https://discord.gg/98eypvw2FS) for help, discussions, and community support
- **Issues**: Report bugs or request features on our [GitHub Issues](https://github.com/5w14/e418/issues) page
