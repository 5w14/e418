# Myths of Entity 418

is a horror mod designed to be less intrusive, inspired by mrdrnose's [Voices
of The Void](https://mrdrnose.itch.io/votv).

## Design philosophies

1. The mod will not affect any buildings or other creations made by a player,
   unless specifically asked to.

2. The mod will not interfere with normal gameplay, be it vanilla or modded.

## Data-driven events

The content in this mod is entirely data-driven. This means that you could add
or remove content using Minecraft's datapacks.

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
          "type": "e418:select_random_player",
        },
        {
          "type": "e418:select_random_location_around_player",
        },
      ],

      // Run only if the player is below 30 on Y-axis
      "run_conditions": [
        {
          "type": "e418:at_height",
          "below": 30,
        },
      ],

      // Play a cave sound
      "behaviours": [
        {
          "type": "e418:play_sound",
          "sound": "minecraft:ambient.cave",
          "volume": 1.0,
          "pitch": 0.8,
        },
      ],
    },
  ],
  "run_conditions": [],
  "queue_conditions": [],
}
```

Check the [wiki](https://github.com/5w14/e418/wiki) to learn more!
