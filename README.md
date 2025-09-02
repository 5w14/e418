# e418

This is a library mod providing functionality for data-driven gameplay events

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

For developer support join the [discord server](https://discord.gg/98eypvw2FS).
